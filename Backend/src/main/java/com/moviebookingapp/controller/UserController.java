package com.moviebookingapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import com.moviebookingapp.dto.AuthRequest;
import com.moviebookingapp.dto.ForgotPasswordRequest;
import com.moviebookingapp.dto.MovieDto;
import com.moviebookingapp.entity.MovieInfo;
import com.moviebookingapp.entity.TicketInfo;
import com.moviebookingapp.entity.UserInfo;
import com.moviebookingapp.exceptions.IncorrectSecurityAnswer;
import com.moviebookingapp.exceptions.UserNotFoundException;
import com.moviebookingapp.service.JwtService;
import com.moviebookingapp.service.UserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.security.Principal;
import java.util.List;
import jakarta.validation.Valid;

@RestController
@SecurityRequirement(name = "moviebookingappapi")
@RequestMapping("/moviebookingapp")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserInfo userInfo) {
        try {
            return ResponseEntity.ok(userService.addUser(userInfo));
        } catch (Exception ex) {
            return ResponseEntity.status(403).body(ex.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            return ResponseEntity.ok(jwtService.generateToken(authRequest.getUsername()));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body("Wrong Password");
        } catch (InternalAuthenticationServiceException ex) {
            return ResponseEntity.status(401).body(ex.getMessage());
        }
    }

    @RequestMapping(value = "/forgot", method = RequestMethod.POST)
    public ResponseEntity<?> resetPassword(
            @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        try {
            userService.resetPassword(forgotPasswordRequest);
            return ResponseEntity.ok("Password Updated Successfuly!");
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        } catch (IncorrectSecurityAnswer ex) {
            return ResponseEntity.status(403).body(ex.getMessage());
        }
    }

    @PostMapping("/addMovie")
    public ResponseEntity<?> addMovie(@RequestBody MovieDto movieDto,
            @RequestHeader("Authorization") String authorizationHeader) {
        String loggedUser = jwtService.extractUsername(authorizationHeader.substring(7));
        try {
            return ResponseEntity.ok(userService.addMovie(movieDto, loggedUser));
        } catch (Exception ex) {
            return ResponseEntity.status(403).body(ex.getMessage());
        }
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserBuUsername(@PathVariable(name = "username") String username) {
        try {
            return ResponseEntity.ok(userService.getUser(username));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @GetMapping("/all")
    public List<MovieInfo> showAllMovies() {
        return userService.showAllMovies();
    }

    @GetMapping("/search/{movieName}")
    public ResponseEntity<?> showMovie(@PathVariable(name = "movieName") String movieName) {
        try {
            return ResponseEntity.ok(userService.showMovie(movieName));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PostMapping("/bookTicket")
    public ResponseEntity<?> bookTicket(@Valid @RequestBody TicketInfo ticketInfo, Principal principal) {
        ticketInfo.setUsername(principal.getName());
        try {
            return ResponseEntity.ok(userService.bookTicket(ticketInfo));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{movieName}/{theaterName}/delete")
    public ResponseEntity<?> deleteMovie(@PathVariable(name = "movieName") String movieName,
            @PathVariable(name = "theaterName") String theaterName) {
        try {
            return ResponseEntity.ok(userService.deleteMovie(movieName, theaterName));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

}
