package com.moviebookingapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class MovieDto {
    @NotBlank(message = "Enter Valid MovieName")
    private String movieName;

    @NotBlank(message = "Enter Valid TheaterName")
    private String theaterName;

    @NotBlank(message = "Enter avaialble tickets")
    private Integer availableTickets;
}
