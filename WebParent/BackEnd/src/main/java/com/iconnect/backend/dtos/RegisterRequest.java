package com.iconnect.backend.dtos;

import com.iconnect.backend.model.enums.GenderType;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Data
public class RegisterRequest {

    @NotBlank (message = "Email can not be empty")
    @Email
    private String email;

    @NotBlank (message = " Full Name can not be empty")
    private String fullname;
    
    @NotBlank(message = "Password can not be empty")
    @Size(min = 7, max = 35)
    private String password;

    @NotBlank(message = "Phone Number can not be empty")
    private String phoneNumber;

    private String dpUrl;

    /*@NotNull(message = "Date of Birth cannot be Empty")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;*/

}
