package com.moviebookingapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TicketsNotAvailableException extends RuntimeException {
    public TicketsNotAvailableException(String message) {
        super(message);
    }
}
