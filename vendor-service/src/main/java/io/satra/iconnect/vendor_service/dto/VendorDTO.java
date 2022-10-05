package io.satra.iconnect.vendor_service.dto;

import io.satra.iconnect.vendor_service.entity.Vendor;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class VendorDTO {
    private String id;
    private String name;
    private Set<ValidatorDTO> validators;

    public Vendor toEntity() {
        Vendor vendor = new Vendor();
        vendor.setId(id);
        vendor.setName(name);

        if (validators != null) {
            vendor.setValidators(validators.stream().map(ValidatorDTO::toEntity).collect(Collectors.toSet()));
        }

        return vendor;
    }
}
