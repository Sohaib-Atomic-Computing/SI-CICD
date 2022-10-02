package io.satra.iconnect.user_service.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtResponseDTO {

  private String type = "Bearer";
  private String accessToken;
  private String refreshToken;
  private LocalDateTime issueTime;
  private LocalDateTime expiryTime;
}
