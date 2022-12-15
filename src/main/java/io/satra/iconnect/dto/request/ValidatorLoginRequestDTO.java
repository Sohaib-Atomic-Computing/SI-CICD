package io.satra.iconnect.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ValidatorLoginRequestDTO {
    @NotBlank(message = "User ID is required!")
    private String userId;
    @NotBlank(message = "Validator Key is required!")
    private String validatorKey;
}
