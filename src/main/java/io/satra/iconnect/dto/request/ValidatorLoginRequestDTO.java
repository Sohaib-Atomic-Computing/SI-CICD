package io.satra.iconnect.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ValidatorLoginRequestDTO {
    @NotBlank(message = "Customer ID is required!")
    private String customerId;
    @NotBlank(message = "Validator Key is required!")
    private String validatorKey;
}
