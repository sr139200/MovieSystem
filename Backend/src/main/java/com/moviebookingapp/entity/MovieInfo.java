package com.moviebookingapp.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.moviebookingapp.dto.CompositeId;

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
@Document(collection = "moviedb")
public class MovieInfo {

    @Id
    private CompositeId movieTheater;

    @Size(min = 100, max = 100)
    @NotBlank(message = "Cannot be blank")
    private Integer availableTickets;

    private List<Integer> availableSeatsNumbers = IntStream.rangeClosed(1, 100).boxed().collect(Collectors.toList());

    @NotBlank(message = "Enter valid ticket status")
    private String ticketStatus = "Book now";

}
