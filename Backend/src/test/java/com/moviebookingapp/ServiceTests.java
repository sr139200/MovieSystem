package com.moviebookingapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.moviebookingapp.dto.CompositeId;
import com.moviebookingapp.dto.MovieDto;
import com.moviebookingapp.entity.MovieInfo;
import com.moviebookingapp.entity.TicketInfo;
import com.moviebookingapp.entity.UserInfo;
import com.moviebookingapp.repository.MovieInfoRepository;
import com.moviebookingapp.repository.TicketInfoRepository;
import com.moviebookingapp.repository.UserInfoRepository;
import com.moviebookingapp.service.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class ServiceTests {

    private UserInfo user;
    private MovieInfo movie;
    private TicketInfo ticket;
    private CompositeId cid;
    private MovieDto movieDto;
    private Optional<List<MovieInfo>> movieList = Optional.empty();

    @MockBean
    private UserInfoRepository userRepo;
    @MockBean
    private MovieInfoRepository movieRepo;
    @MockBean
    private TicketInfoRepository ticketRepo;

    @Autowired
    private UserService userService;

    private UserService mockService = mock(UserService.class);

    @BeforeEach
    public void setUp() {
        List<String> role = new ArrayList<>();
        role.add("user");
        List<Integer> availableSeatsNumbers = IntStream.rangeClosed(1, 100).boxed().collect(Collectors.toList());
        List<Integer> seatNumber = new ArrayList<>();
        seatNumber.add(1);
        cid = new CompositeId("MovieName", "TheaterName");
        user = new UserInfo("swapnil", "rathi", "swapnil@gmail.com", "123456790", "swapnil13", "rathi13",
                "admin", "cricket");
        movie = new MovieInfo(cid, 100, availableSeatsNumbers, "Book now");
        movieDto = new MovieDto(cid.getMovieName(), cid.getTheaterName(), 100);
        ticket = new TicketInfo("id", "swapnil13", "MovieName", "TheaterName", 1, seatNumber);
        List<MovieInfo> list = movieList.orElse(new ArrayList<MovieInfo>());
        list.add(movie);
    }

    @Test
    public void testAddUser() {
        when(userRepo.save(any(UserInfo.class))).thenReturn(user);
        UserInfo user1;
        try {
            user1 = userService.addUser(user);
            assertEquals(user.getUsername(), user1.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAddMovie() {
        when(movieRepo.save(any(MovieInfo.class))).thenReturn(movie);
        when(mockService.isAdmin(anyString())).thenReturn(true);
       
        when(userService.isMovieTheaterPresent(cid)).thenReturn(false);
        
        MovieInfo temp;
        try {
            temp = userService.addMovie(movieDto,anyString());
            assertEquals(movie.getMovieTheater(), temp.getMovieTheater());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBookTicket() {
        when(ticketRepo.save(any(TicketInfo.class))).thenReturn(ticket);
        when(userService.isTicketAvailable(movie,ticket)).thenReturn(true);
       when(mockService.isSeatAvailable(movie,ticket)).thenReturn(true);
        TicketInfo temp;
        try {
            temp = userService.bookTicket(ticket);
            assertEquals(ticket.getId(), temp.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testShowMovie() {
        when(movieRepo.findByMovie(anyString())).thenReturn(movieList);
        when(mockService.isMoviePresent("MovieName")).thenReturn(true);
        Optional<List<MovieInfo>> temp;
        try {
            temp = userService.showMovie("MovieName");
            assertEquals(movieList.get().get(0), temp.get().get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeleteMovie() {
        when(movieRepo.existsById(any(CompositeId.class))).thenReturn(true);
        try{
            userService.deleteMovie("MovieName", "TheaterName");
            verify(movieRepo, times(1)).deleteById(any(CompositeId.class));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // @Test
    // public void testResetPassword(){
    // when(userRepo.findByUsername("swapnil13")).thenReturn(Optional.ofNullable(user));
    // when(mockService.isUserPresent("swapnil")).thenReturn(true);
    // when(userRepo.save(any(UserInfo.class))).thenReturn(user);
    // try {
    // userService.resetPassword("swapnil13","rathi");
    // verify(userRepo, times(1)).save(any(UserInfo.class));
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }

}
