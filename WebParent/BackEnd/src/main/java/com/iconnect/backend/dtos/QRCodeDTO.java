package com.iconnect.backend.dtos;

import lombok.Data;
import org.joda.time.DateTime;

import javax.validation.constraints.NotBlank;



@Data
public class QRCodeDTO {

  private String uniqueID;
  private String randomID;
  private String timestamp;
  private String field1;
  private String field2;
  private Object payload;

}
