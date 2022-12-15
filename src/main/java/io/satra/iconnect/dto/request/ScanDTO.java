package io.satra.iconnect.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ScanDTO {
    @NotBlank(message = "Message is required!")
    private String message;
}
