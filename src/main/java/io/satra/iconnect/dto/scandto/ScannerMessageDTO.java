package io.satra.iconnect.dto.scandto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.satra.iconnect.dto.UserDTO;
import lombok.Data;

/**
 * This class is used to hold the scanner decrypted message.
 * After decryption, the information inside the scanner message is converted to this class.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScannerMessageDTO {
    private String userId;
    private String timestampUTC;
}
