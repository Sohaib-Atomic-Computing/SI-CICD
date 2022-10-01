package io.satra.iconnect.user_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.LocalDateTime;
import lombok.Data;

@JsonInclude(Include.NON_NULL)
@Data
public class JwtResponseDTO {

  private String type = "Bearer";
  private String accessToken;
  private String refreshToken;
  private LocalDateTime issueTime;
  private LocalDateTime expiryTime;
}
