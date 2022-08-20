package com.iconnect.backend.dtos;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Waqar 02/08/2020
 */
@Data
public class TokensDTO {

  @NotNull
  @NotBlank
  private String deviceToken;

}
