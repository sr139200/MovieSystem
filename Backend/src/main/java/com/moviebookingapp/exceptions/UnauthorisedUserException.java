package com.moviebookingapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorisedUserException extends RuntimeException {
    public UnauthorisedUserException(String message) {
        super(message);
    }
}