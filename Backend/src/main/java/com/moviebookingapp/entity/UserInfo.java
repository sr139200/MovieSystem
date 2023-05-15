package com.moviebookingapp.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "userdb")
public class UserInfo {

    @Size(min = 2, max = 15)
    @NotBlank(message = "Enter valid first name")
    private String firstName;

    @Size(min = 2, max = 15)
    @NotBlank(message = "Enter valid last name")
    private String lastName;

    @Email(message = "Enter valid email address")
    private String email;

    @Size(min = 10, max = 10)
    @NotBlank(message = "Enter valid contact")
    private String contact;

    @Id
    @NotBlank(message = "Username cannot be null")
    private String username;

    @Size(min = 2, max = 15)
    @NotBlank(message = "Enter valid password")
    private String password;

    @NotBlank(message = "Enter valid role")
    private String role;

    private String securityQuestion;
}
