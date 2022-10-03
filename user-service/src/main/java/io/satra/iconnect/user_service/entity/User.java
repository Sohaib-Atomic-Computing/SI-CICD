package io.satra.iconnect.user_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.satra.iconnect.user_service.dto.UserDTO;
import io.satra.iconnect.user_service.entity.enums.GenderType;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "id",
        "email",
        "phoneNumber"
    })
}
)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntityAudit {

  @NotNull
  @Email
  private String email;

  @NotNull
  private String phoneNumber;

  @NotNull
  private GenderType genderType;

  @NotNull
  private String firstName;

  @NotNull
  private String lastName;

  @NotBlank
  @JsonIgnore
  private String password;

  @JsonIgnore
  private String otpCode;

  @JsonIgnore
  private String resetToken;

  @JsonIgnore
  @OneToOne
  private DeviceToken deviceToken;

  private String dpUrl;

  private String qrCode;

  @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
  @Builder.Default
  private Boolean isActive = Boolean.FALSE;
  @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
  @Builder.Default
  private Boolean isPhoneVerified = Boolean.FALSE;
  @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
  @Builder.Default
  private Boolean isEmailVerified = Boolean.FALSE;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    User user = (User) o;
    return id != null && Objects.equals(id, user.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  public UserDTO toDTO() {
    return UserDTO.builder()
        .id(id)
        .createdAt(createdAt)
        .lastModifiedAt(lastModifiedAt)
        .email(email)
        .phoneNumber(phoneNumber)
        .genderType(genderType)
        .firstName(firstName)
        .lastName(lastName)
        .dpUrl(dpUrl)
        .qrCode(qrCode)
        .isActive(isActive)
        .isEmailVerified(isEmailVerified)
        .isPhoneVerified(isPhoneVerified)
        .build();
  }
}
