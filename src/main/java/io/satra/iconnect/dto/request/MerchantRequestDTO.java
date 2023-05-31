package io.satra.iconnect.dto.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
public class MerchantRequestDTO {
    @NotEmpty
    @Size(min = 3)
    private String name;
    @Size(max = 50)
    private String abbreviation;
    @NotEmpty
    private String adminFirstName;
    @NotEmpty
    private String adminLastName;
    @NotEmpty
    private String adminEmail;
    @NotEmpty
    private String password;
    @NotEmpty
    private String mobile;
    private String firstAddress;
    private String secondAddress;
    @NotEmpty
    private String city;
    private String state;
    @NotEmpty
    private String country;
    private Boolean isActive;
}
