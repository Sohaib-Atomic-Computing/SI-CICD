package io.satra.iconnect.user_service.dto;

import io.satra.iconnect.user_service.entity.enums.ServiceType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;


@Data
public class LoginRequestDTO {

  @NotBlank(message = "Phone Number or Email cannot be blank")
  private String emailOrPhoneNumber;
  @NotBlank(message = "Password or Code cannot be blank")
  private String passwordOrCode;
  @NotNull(message = "Service Type cannot be empty")
  private ServiceType serviceType;
}
