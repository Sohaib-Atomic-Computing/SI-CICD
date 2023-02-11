package io.satra.iconnect.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ChangePasswordDTO {

    @NotBlank(message = "Old password is required!")
    private String oldPassword;
    @NotBlank(message = "New password is required!")
    private String newPassword;

}
