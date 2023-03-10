package io.satra.iconnect.dto.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class RegisterRequestDTO {
    @NotBlank(message = "First name is required!")
    private String firstName;
    @NotBlank(message = "Last name is required!")
    private String lastName;
    @NotBlank(message = "Email is required!")
    @Email(message = "Email is invalid!")
    private String email;
    @NotBlank(message = "Password is required!")
    private String password;
    @NotBlank(message = "Mobile is required!")
    private String mobile;
}
