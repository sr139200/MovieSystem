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

public class ForgotPasswordRequest {
    @NotBlank(message = "Enter Valid UserName")
    private String userName;

    @NotBlank(message = "Enter Valid Password")
    private String newPassword;

    @NotBlank(message = "Enter Security Answer")
    private String securityAnswer;
}
