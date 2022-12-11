package io.satra.iconnect.dto;

import io.satra.iconnect.entity.User;
import io.satra.iconnect.entity.enums.UserRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserDTO {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private Boolean isActive;
    private UserRole role;
    private String qrCode;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

    public User toEntity() {
        User user = new User();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setMobile(mobile);
        user.setIsActive(isActive);
        user.setRole(role);
        user.setQrCode(qrCode);
        user.setCreatedAt(createdAt);
        user.setLastModifiedAt(lastModifiedAt);

        return user;
    }
}
