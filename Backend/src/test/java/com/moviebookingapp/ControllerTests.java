package com.moviebookingapp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.moviebookingapp.controller.UserController;
import com.moviebookingapp.dto.CompositeId;
import com.moviebookingapp.dto.ForgotPasswordRequest;
import com.moviebookingapp.dto.MovieDto;
import com.moviebookingapp.entity.MovieInfo;
import com.moviebookingapp.entity.TicketInfo;
import com.moviebookingapp.entity.UserInfo;
import com.moviebookingapp.repository.MovieInfoRepository;
import com.moviebookingapp.repository.TicketInfoRepository;
import com.moviebookingapp.repository.UserInfoRepository;
import com.moviebookingapp.service.JwtService;
import com.moviebookingapp.service.UserService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class ControllerTests {
        @InjectMocks
        UserController userController;
        @Mock
        UserService userService;
        @Mock
        JwtService jwtService;
        @Mock
        AuthenticationManager authenticationManager;
        @MockBean
        private UserInfoRepository userRepo;
        @MockBean
        private MovieInfoRepository movieRepo;
        @MockBean
        private TicketInfoRepository ticketRepo;

        private MockMvc mockMvc;

        String token = "valid_token";
        Principal principal;
        String username = "swapnil13";
        private String movieName = "MovieName";
        private String theaterName = "TheaterName";
        private UserInfo user;
        private MovieInfo movie;
        private TicketInfo ticket;
        private CompositeId cid;
        private MovieDto movieDto;
        private Optional<List<MovieInfo>> movieList = Optional.empty();

        @Before
        public void setup() {

                mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
                principal = Mockito.mock(Principal.class);
                List<String> role = new ArrayList<>();
                role.add("user");
                List<Integer> availableSeatsNumbers = IntStream.rangeClosed(1, 100).boxed()
                                .collect(Collectors.toList());
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
        public void testSignup() throws Exception {
                String s = "{\"firstName\":\"swapnil\",\"lastName\":\"rathi\",\"email\":\"swapnil@gmail.com\",\"contact\":\"1234567890\",\"username\":\"swapnil14\",\"password\":\"rathi13\",\"confirmPassword\":\"rathi13\",\"role\":\"admin\"}";
                Mockito.when(userService.addUser(any(UserInfo.class))).thenReturn(user);
                mockMvc.perform(MockMvcRequestBuilders
                                .post("/moviebookingapp/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(s))
                                .andExpect(status().isOk())
                                .andDo(print());
        }

        @Test
        public void testLogin() throws Exception {
                String s = "{\"username\":\"swapnil13\",\"password\":\"rathi13\"}";
                Mockito.when(authenticationManager.authenticate(any(Authentication.class)))
                                .thenReturn(new UsernamePasswordAuthenticationToken("swapnil13", "rathi13"));
                Mockito.when(jwtService.generateToken(anyString())).thenReturn("token");
                mockMvc.perform(MockMvcRequestBuilders
                                .post("/moviebookingapp/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(s))
                                .andExpect(status().isOk())
                                .andDo(print());
        }

        @Test
        public void testdeleteMovie() throws Exception {
                mockMvc.perform(MockMvcRequestBuilders
                                .delete("/moviebookingapp/" + movieName + "/" + theaterName + "/delete"))
                                .andExpect(status().isOk())
                                .andDo(print());
        }

        @Test
        public void testBookTicket() throws Exception {
                String s = "{\"movieName\":\"pathan\",\"theaterName\":\"inox\",\"numberOfTickets\":1,\"seatNumber\":[1]}";
                Mockito.when(userService.isMovieTheaterPresent(any(CompositeId.class))).thenReturn(true);
                Mockito.when(principal.getName()).thenReturn("swapnil13");
                Mockito.when(userService.isTicketAvailable(any(MovieInfo.class),
                                any(TicketInfo.class)))
                                .thenReturn(true);

                Mockito.when(userService.bookTicket(any(TicketInfo.class))).thenReturn(ticket);

                mockMvc.perform(MockMvcRequestBuilders
                                .post("/moviebookingapp/bookTicket")
                                .principal(principal)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(s))
                                .andExpect(status().isOk())
                                .andDo(print());
        }

        @Test
        public void showMovie() throws Exception {
                Mockito.when(userService.showMovie(anyString())).thenReturn(movieList);
                Mockito.when(userService.isMoviePresent(movieName)).thenReturn(true);
                mockMvc.perform(MockMvcRequestBuilders
                                .get("/moviebookingapp/search/" + movieName))
                                .andExpect(status().isOk())
                                .andDo(print());
        }

        @Test
        public void testaddMovie() throws Exception {
                String s = "{\"movieName\":\"pathan\",\"theaterName\":\"inox\",\"availableTickets\":100}";
                Mockito.when(userService.addMovie(any(MovieDto.class), anyString())).thenReturn(movie);
                Mockito.when(userService.isMovieTheaterPresent(any(CompositeId.class))).thenReturn(false);
                Mockito.when(userService.isAdmin(anyString())).thenReturn(true);
                mockMvc.perform(MockMvcRequestBuilders
                                .post("/moviebookingapp/addMovie")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(s)
                                .header("Authorization", "Bearer " + token))
                                .andExpect(status().isOk())
                                .andDo(print());
        }

        // @Test
        // public void testResetPassword() throws Exception {
        // String s = "password";
        // Mockito.when(userService.resetPassword(any(ForgotPasswordRequest.class))).thenReturn(user);
        // Mockito.when(userService.isUserPresent(anyString())).thenReturn(true);

        // mockMvc.perform(MockMvcRequestBuilders
        // .post("/moviebookingapp/" + username + "/forgot")
        // .contentType(MediaType.ALL_VALUE)
        // .content(s)
        // .header("Authorization", "Bearer " + token))
        // .andExpect(status().isOk())
        // .andDo(print());
        // }
}
