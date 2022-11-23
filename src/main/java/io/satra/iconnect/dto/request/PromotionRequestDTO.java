package io.satra.iconnect.dto.request;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
public class PromotionRequestDTO {
    @NotBlank(message = "Name is required!")
    private String name;
    @NotBlank(message = "Description is required!")
    private String description;
    private Boolean isActive;
    @NotNull(message = "Start date is required!")
    private String startDate;
    @NotNull(message = "End date is required!")
    private String endDate;
    @NotBlank(message = "Vendor is required!")
    private String vendorId;
    private Set<String> userIds;
}
