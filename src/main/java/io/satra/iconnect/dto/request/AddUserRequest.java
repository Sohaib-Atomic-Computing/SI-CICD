package io.satra.iconnect.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AddUserRequest {
    @NotBlank(message = "First name is required!")
    private String firstName;
    @NotBlank(message = "Last name is required!")
    private String lastName;
    private String email;
    private String mobile;
}
