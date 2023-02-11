package io.satra.iconnect.dto.scandto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * This class is used to get the scanner encrypted message from the API.
 */
@Data
public class ScanDTO {
    @NotBlank(message = "Message is required!")
    private String message;
}
