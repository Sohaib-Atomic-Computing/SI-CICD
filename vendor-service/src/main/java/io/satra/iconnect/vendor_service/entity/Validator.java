package io.satra.iconnect.vendor_service.entity;

import io.satra.iconnect.vendor_service.dto.ValidatorDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Validator {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    private String name;

    public ValidatorDTO toDTO() {
        ValidatorDTO validatorDTO = new ValidatorDTO();
        validatorDTO.setId(id);
        validatorDTO.setName(name);

        return validatorDTO;
    }
}
