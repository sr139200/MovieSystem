package com.moviebookingapp.entity;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nonapi.io.github.classgraph.json.Id;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "ticketdb")
public class TicketInfo {

    // @NotBlank(message = "Username cannot be null")
    @Id
    private String id;

    private String username;

    @Size(max = 15, min = 2, message = "{user.name.invalid}")
    @NotBlank(message = "Enter valid movie name")
    private String movieName;

    @Size(min = 2, max = 15, message = "{theatre.name.invalid}")
    @NotBlank(message = "Enter valid theatre name")
    private String theaterName;

    // @Size(min = 1, max = 100, message = "{numberOfTickets.invalid}")
    @NotNull(message = "Enter valid number")
    private Integer numberOfTickets;

    @NotNull(message = "Enter valid number")
    private List<Integer> seatNumber;

}
