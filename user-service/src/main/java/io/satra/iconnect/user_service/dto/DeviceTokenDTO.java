package io.satra.iconnect.user_service.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeviceTokenDTO {

  @NotNull
  @NotBlank
  private String token;
}
