package com.iconnect.backend.dtos;

import com.iconnect.backend.model.enums.GenderType;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Data
public class RegisterRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank(message = "Username can not be empty")
    @Size(min = 2, max = 20)
    private String username;
    
    @NotBlank(message = "Password can not be empty")
    @Size(min = 7, max = 35)
    private String password;

    /*@NotNull(message = "Date of Birth cannot be Empty")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;*/
    @NotNull(message = "Gender cannot be Empty")
    private GenderType gender;
}
