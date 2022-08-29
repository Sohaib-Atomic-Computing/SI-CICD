package com.iconnect.backend.dtos;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class GenerateOTPDTO {

    @NotNull
    @NotBlank
    private String phoneNumber;

}
