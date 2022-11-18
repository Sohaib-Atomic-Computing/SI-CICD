package io.satra.iconnect.user_service.dto.response;

import io.satra.iconnect.user_service.dto.UserDTO;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponseDTO {

    private UserDTO user;
    private String token;
    private String type = "Bearer";

}
