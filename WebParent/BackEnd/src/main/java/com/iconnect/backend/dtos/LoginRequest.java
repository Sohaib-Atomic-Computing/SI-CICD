package com.iconnect.backend.dtos;

import com.iconnect.backend.model.enums.ServiceType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
public class LoginRequest {

    @NotBlank  (message =  "Phone Number or Email cannot be blank")
    private String phoneNumberOrEmail;
    @NotBlank (message =  "Password or Code cannot be blank")
    private String passwordOrCode;
    @NotNull(message = "Service Type cannot be Empty")
    private ServiceType serviceType;

   
}