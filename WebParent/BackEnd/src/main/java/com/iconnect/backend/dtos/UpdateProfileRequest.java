package com.iconnect.backend.dtos;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Data
public class UpdateProfileRequest {

    private String fullname;

    @Size(min = 7, max = 35)
    private String password;

    private String dpUrl;


}
