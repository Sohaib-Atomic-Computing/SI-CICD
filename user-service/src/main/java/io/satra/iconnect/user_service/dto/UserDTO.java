package io.satra.iconnect.user_service.dto;

import io.satra.iconnect.user_service.entity.User;
import io.satra.iconnect.user_service.entity.enums.GenderType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {

  private String id;
  private LocalDateTime createdAt;
  private LocalDateTime lastModifiedAt;
  private String email;
  private String phoneNumber;
  private GenderType genderType;
  private String firstName;
  private String lastName;
  private String dpUrl;
  private String qrCode;
  private Boolean isActive;
  private Boolean isPhoneVerified;
  private Boolean isEmailVerified;

  public User toEntity() {
    User user = new User();
    user.setId(id);
    user.setCreatedAt(createdAt);
    user.setLastModifiedAt(lastModifiedAt);
    user.setEmail(email);
    user.setPhoneNumber(phoneNumber);
    user.setGenderType(genderType);
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setDpUrl(dpUrl);
    user.setQrCode(qrCode);
    user.setIsActive(isActive);
    user.setIsPhoneVerified(isPhoneVerified);
    user.setIsEmailVerified(isEmailVerified);

    return user;
  }
}
