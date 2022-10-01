package io.satra.iconnect.user_service.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GenerateOTPDTO {

  @NotNull
  @NotBlank
  private String phoneNumber;
}
