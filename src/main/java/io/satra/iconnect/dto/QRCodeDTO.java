package io.satra.iconnect.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QRCodeDTO {

    private String uniqueID;
    private String randomID;
    private String timestamp;
    private String field1;
    private String field2;
    private Object payload;
}
