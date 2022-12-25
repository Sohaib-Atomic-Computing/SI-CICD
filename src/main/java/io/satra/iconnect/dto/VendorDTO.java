package io.satra.iconnect.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.satra.iconnect.entity.Vendor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VendorDTO {
    private String id;
    private String name;
    private String logo;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime lastModifiedAt;
    private String lastModifiedBy;
}
