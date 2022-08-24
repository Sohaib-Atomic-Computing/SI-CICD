package com.iconnect.backend.dtos;

import lombok.Data;

import javax.validation.constraints.NotBlank;


@Data
public class LoginRequest {

    @NotBlank
    private String phoneNumberOrEmail;
    @NotBlank
    private String passwordOrCode;


   
}