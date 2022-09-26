package com.iconnect.backend.dtos;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UpdatePasswordDTO {
    @Size(min = 7, max = 35)
    @NotBlank(message =  "Current Password cannot be blank")
    private String currentpassword;
    @NotBlank(message =  "New Password cannot be blank")
    private String newpassword;


}
