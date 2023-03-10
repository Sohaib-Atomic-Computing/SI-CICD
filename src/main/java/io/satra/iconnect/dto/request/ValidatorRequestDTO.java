package io.satra.iconnect.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ValidatorRequestDTO {
    @NotBlank(message = "Name is required!")
    private String name;
    @NotNull(message = "Vendor is required!")
    private String vendorId;
}
