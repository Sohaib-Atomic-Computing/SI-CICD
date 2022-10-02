package io.satra.iconnect.user_service.entity;

import io.satra.iconnect.user_service.dto.DeviceTokenDTO;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceToken extends BaseEntity {

  @NotNull
  @NotBlank
  private String token;

  @OneToOne
  private User user;

  public DeviceTokenDTO toDTO() {
    return DeviceTokenDTO.builder()
        .id(id)
        .token(token)
        .build();
  }
}
