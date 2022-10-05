package io.satra.iconnect.vendor_service.dto;

import io.satra.iconnect.vendor_service.entity.Validator;
import lombok.Data;

@Data
public class ValidatorDTO {
    private String id;
    private String name;

    public Validator toEntity() {
        Validator validator = new Validator();
        validator.setId(id);
        validator.setName(name);

        return validator;
    }
}
