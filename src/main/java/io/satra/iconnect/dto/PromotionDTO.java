package io.satra.iconnect.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PromotionDTO {
    private String id;
    private String name;
    private String description;
    private Boolean isActive;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Set<UserDTO> users;
    private VendorDTO vendor;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime lastModifiedAt;
    private String lastModifiedBy;
}
