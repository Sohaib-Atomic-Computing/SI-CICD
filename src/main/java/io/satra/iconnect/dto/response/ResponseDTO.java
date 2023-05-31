package io.satra.iconnect.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.time.ZonedDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO<T> {
    private String message;
    private Boolean success;
    private String error;
    private Integer status;
    private ZonedDateTime timestamp;
    private T data;
}
