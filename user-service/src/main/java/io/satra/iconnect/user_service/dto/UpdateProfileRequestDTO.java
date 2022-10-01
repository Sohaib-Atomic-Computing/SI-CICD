package io.satra.iconnect.user_service.dto;

import io.satra.iconnect.user_service.entity.enums.GenderType;
import lombok.Data;


@Data
public class UpdateProfileRequestDTO {

  private GenderType genderType;
  private String fristName;
  private String lastName;
  private String dpUrl;
}
