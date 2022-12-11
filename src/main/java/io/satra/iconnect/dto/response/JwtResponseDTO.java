package io.satra.iconnect.dto.response;

import io.satra.iconnect.dto.UserDTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtResponseDTO {
    private UserDTO user;
    private String token;
    @Builder.Default
    private String type = "Bearer";
}
