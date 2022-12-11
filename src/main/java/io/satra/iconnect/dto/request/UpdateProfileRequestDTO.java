package io.satra.iconnect.dto.request;

import io.satra.iconnect.entity.enums.UserRole;
import lombok.Data;

@Data
public class UpdateProfileRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private Boolean isActive;
    private UserRole role;
}
