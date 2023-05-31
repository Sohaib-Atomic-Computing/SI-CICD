package io.satra.iconnect.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MerchantDTO {
    private String id;
    private String name;
    private String logo;
    private String abbreviation;
    private String adminFirstName;
    private String adminLastName;
    private String adminEmail;
    private String mobile;
    private String firstAddress;
    private String secondAddress;
    private String city;
    private String state;
    private String country;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

}
