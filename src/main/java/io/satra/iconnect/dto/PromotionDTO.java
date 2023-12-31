package io.satra.iconnect.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.satra.iconnect.entity.enums.PromotionStatus;
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
    private PromotionStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Set<UserDTO> users;
    private VendorDTO vendor;
    private MerchantDTO merchant;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
}
