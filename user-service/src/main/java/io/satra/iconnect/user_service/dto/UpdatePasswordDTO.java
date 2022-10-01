package io.satra.iconnect.user_service.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePasswordDTO {

  @Size(min = 7, max = 35)
  @NotBlank(message = "Current Password cannot be blank")
  private String currentPassword;
  @NotBlank(message = "New Password cannot be blank")
  private String newPassword;
}
