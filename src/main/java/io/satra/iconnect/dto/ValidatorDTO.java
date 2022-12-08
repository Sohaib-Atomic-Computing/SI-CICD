package io.satra.iconnect.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ValidatorDTO {
    private String id;
    private String name;
    private String key;
    private VendorDTO vendor;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime lastModifiedAt;
    private String lastModifiedBy;
}
