package com.iconnect.backend.dtos;

import lombok.Data;

import javax.validation.constraints.NotBlank;


@Data
public class LoginRequest {

    @NotBlank
    private String usernameOrEmail;

    @NotBlank
   // @Size(min = 7, max = 35)
    private String password;

   
}