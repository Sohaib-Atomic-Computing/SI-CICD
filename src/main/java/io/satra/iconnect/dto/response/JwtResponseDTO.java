package io.satra.iconnect.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.satra.iconnect.dto.MerchantDTO;
import io.satra.iconnect.dto.UserDTO;
import io.satra.iconnect.dto.ValidatorDTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JwtResponseDTO {
    private UserDTO user;
    private ValidatorDTO validator;
    private MerchantDTO merchant;
    private String token;
    @Builder.Default
    private String type = "Bearer";
}
