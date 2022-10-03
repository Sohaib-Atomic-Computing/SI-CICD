package io.satra.iconnect.user_service.dto;

import io.satra.iconnect.user_service.entity.enums.GenderType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;


@Data
public class RegisterRequestDTO {

  @NotBlank(message = "Email cannot be empty")
  @Email
  private String email;

  @NotBlank(message = "Phone Number cannot be empty")
  private String phoneNumber;

  @NotNull(message = "Gender Type cannot be null")
  private GenderType genderType;

  @NotBlank(message = "Firstname cannot be empty")
  private String firstName;

  @NotBlank(message = "Lastname cannot be empty")
  private String lastName;

  @NotBlank(message = "Password cannot be empty")
  @Size(min = 7, max = 35)
  private String password;

  private String dpUrl;
}
