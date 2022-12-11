package io.satra.iconnect.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class GenerateOTPDTO {

    @NotNull
    @NotBlank
    private String mobile;
}
