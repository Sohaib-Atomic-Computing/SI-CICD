/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.satra.iconnect.user_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseDTO {

  private String message;
  private Boolean success;
  private Object data;
}
