package io.satra.iconnect.dto;

import io.satra.iconnect.entity.Vendor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class VendorDTO {
    private String id;
    private String name;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime lastModifiedAt;
    private String lastModifiedBy;
}
