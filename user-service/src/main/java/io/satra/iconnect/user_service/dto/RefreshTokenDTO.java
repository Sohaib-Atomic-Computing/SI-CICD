package io.satra.iconnect.user_service.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenDTO {

  @NotBlank
  private String refreshToken;
}
