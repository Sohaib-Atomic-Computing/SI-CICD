package io.satra.iconnect.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class VendorRequestDTO {
    @NotBlank(message = "Name is required!")
    private String name;
}
