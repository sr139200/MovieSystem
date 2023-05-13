package com.moviebookingapp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.management.relation.RoleNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.moviebookingapp.dto.CompositeId;
import com.moviebookingapp.dto.MovieDto;
import com.moviebookingapp.entity.MovieInfo;
import com.moviebookingapp.entity.TicketInfo;
import com.moviebookingapp.entity.UserInfo;
import com.moviebookingapp.exceptions.MovieAlreadyExistsException;
import com.moviebookingapp.exceptions.MovieNotFoundException;
import com.moviebookingapp.exceptions.SeatNotAvailableException;
import com.moviebookingapp.exceptions.TicketsNotAvailableException;
import com.moviebookingapp.exceptions.UnauthorisedUserException;
import com.moviebookingapp.exceptions.UserAlreadyExistsException;
import com.moviebookingapp.exceptions.UserNotFoundException;
import com.moviebookingapp.repository.MovieInfoRepository;
import com.moviebookingapp.repository.TicketInfoRepository;
import com.moviebookingapp.repository.UserInfoRepository;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserInfoRepository userRepository;

    @Autowired
    private MovieInfoRepository movieRepository;

    @Autowired
    private TicketInfoRepository ticketRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<UserInfo> getUser(String usernmae) {
        if (isUserPresent(usernmae)) {
            return userRepository.findById(usernmae);
        } else {
            throw new UserNotFoundException("User not found");
        }
    }

    public UserInfo addUser(UserInfo userInfo) throws UserAlreadyExistsException, RoleNotFoundException, Exception {
        List<String> roleList = new ArrayList<>();
        roleList.add("admin");
        roleList.add("user");
        if (!roleList.stream().anyMatch(c -> c.equals(userInfo.getRole()))) {
            throw new RoleNotFoundException("Enter Valid Role(user or admin)");
        }
        if (userRepository.existsById(userInfo.getUsername())) {
            throw new UserAlreadyExistsException("User already Exists");
        }
        if (!userInfo.getPassword().equals(userInfo.getConfirmPassword())) {
            throw new Exception("Password and Confirm Pasword should be same");
        }
        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        // userRepository.save(userInfo);
        return userRepository.save(userInfo);
    }

    public MovieInfo addMovie(MovieDto movieDto, String loggedUser) {
        if (isAdmin(loggedUser)) {
            MovieInfo movieInfo = new MovieInfo();
            CompositeId cid = new CompositeId();
            cid.setMovieName(movieDto.getMovieName());
            cid.setTheaterName(movieDto.getTheaterName());
            if (isMovieTheaterPresent(cid)) {
                throw new MovieAlreadyExistsException("Movie Already Exists");
            }
            movieInfo.setMovieTheater(cid);
            movieInfo.setAvailableTickets(movieDto.getAvailableTickets());
            return movieRepository.save(movieInfo);
        }
        throw new UnauthorisedUserException("Not an Admin");
    }

    public TicketInfo bookTicket(TicketInfo ticketInfo) {
        CompositeId cid = new CompositeId();
        cid.setMovieName(ticketInfo.getMovieName());
        cid.setTheaterName(ticketInfo.getTheaterName());
        if (isMovieTheaterPresent(cid)) {
            Optional<MovieInfo> movieInfo = movieRepository.findById(cid);
            if (!isTicketAvailable(movieInfo.get(), ticketInfo)) {
                throw new TicketsNotAvailableException(
                        "Only " + movieInfo.get().getAvailableTickets() + " tickets left!!");
            }
            if (ticketInfo.getNumberOfTickets() == ticketInfo.getSeatNumber().size()) {
                if (!isSeatAvailable(movieInfo.get(), ticketInfo)) {
                    throw new SeatNotAvailableException(
                            "Please Select from Available Seats :" + movieInfo.get().getAvailableSeatsNumbers());
                }
            } else {
                // can also make new particular exception
                throw new SeatNotAvailableException("Number of Seats and Size if List of Seat Numbers does not match");
            }
            return ticketRepository.save(ticketInfo);
        }
        throw new MovieNotFoundException("movie not found");
    }

    public boolean isTicketAvailable(MovieInfo movieInfo, TicketInfo ticketInfo) {
        Integer have = movieInfo.getAvailableTickets();
        Integer want = ticketInfo.getNumberOfTickets();
        if (want > have) {
            return false;
        }
        var availableTickets = have - want;
        movieInfo.setAvailableTickets(availableTickets);
        if (availableTickets == 0) {
            movieInfo.setTicketStatus("Sold Out");
        }
        movieRepository.save(movieInfo);
        return true;
    }

    public List<MovieInfo> showAllMovies() {
        return movieRepository.findAll();
    }

    public Optional<List<MovieInfo>> showMovie(String movieName) {
        if (!isMoviePresent(movieName)) {
            throw new MovieNotFoundException("No movie found with name: " + movieName);
        }
        return movieRepository.findByMovie(movieName);
    }

    public String deleteMovie(String movieName, String theaterName) {
        CompositeId cid = new CompositeId(movieName, theaterName);
        if (!movieRepository.existsById(cid)) {
            throw new MovieNotFoundException("movie not found");
        }
        movieRepository.deleteById(cid);
        return "movie deleted successfully";
    }

    public UserInfo resetPassword(String username, String password) {
        Optional<UserInfo> user = userRepository.findByUsername(username);
        if (!isUserPresent(username)) {
            throw new UserNotFoundException("User not found with username: " + username);
        }
        user.get().setPassword(passwordEncoder.encode(password));
        user.get().setConfirmPassword(password);
        return userRepository.save(user.get());
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<UserInfo> user = userRepository.findByUsername(username);
        if (!isUserPresent(username)) {
            throw new UserNotFoundException("User not found with username: " + username);
        }
        return new org.springframework.security.core.userdetails.User(user.get().getUsername(),
                user.get().getPassword(),
                new ArrayList<>());
    }

    public boolean isUserPresent(String username) {
        if (userRepository.findByUsername(username).isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean isMoviePresent(String movieName) {
        Optional<List<MovieInfo>> temp = movieRepository.findByMovie(movieName);
        if (temp.get().size() == 0) {
            return false;
        }
        return true;
    }

    public boolean isMovieTheaterPresent(CompositeId cid) {
        if (movieRepository.existsById(cid)) {
            return true;
        }
        return false;
    }

    public boolean isSeatAvailable(MovieInfo movieInfo, TicketInfo ticketInfo) {
        Integer count = 0;
        List<Integer> availableSeats = movieInfo.getAvailableSeatsNumbers();
        for (Integer i : ticketInfo.getSeatNumber()) {
            if (availableSeats.contains(i)) {
                availableSeats.remove(i);
            } else {
                count += 1;
            }
        }
        if (count >= 1) {
            return false;
        }
        movieInfo.setAvailableSeatsNumbers(availableSeats);
        movieRepository.save(movieInfo);
        return true;
    }

    public boolean isAdmin(String loggedUser) {
        Optional<UserInfo> userInfo = userRepository.findById(loggedUser);
        if (userInfo.get().getRole().equals("admin")) {
            return true;
        }
        return false;
    }
}