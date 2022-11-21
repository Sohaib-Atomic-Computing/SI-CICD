package io.satra.iconnect.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginRequestDTO {
    @NotBlank(message = "Email or mobile cannot be blank")
    private String emailOrMobile;
    @NotBlank(message = "Password cannot be blank")
    private String password;
}
