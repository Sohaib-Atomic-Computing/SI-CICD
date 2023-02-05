package io.satra.iconnect.dto.scandto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.satra.iconnect.dto.UserDTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScannerUserInfoDTO {
    private UserDTO user;
    private String timestampUTC;
}
