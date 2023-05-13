package com.moviebookingapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompositeId {

    @NotBlank(message = "Enter Valid MovieName")
    private String movieName;

    @NotBlank(message = "Enter Valid TheaterName")
    private String theaterName;
}
