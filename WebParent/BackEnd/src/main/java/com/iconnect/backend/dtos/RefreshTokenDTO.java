package com.iconnect.backend.dtos;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author kalsumaykhi 20/11/2020
 */

@Data
public class RefreshTokenDTO {

  @NotBlank
  private String refreshToken;

}
