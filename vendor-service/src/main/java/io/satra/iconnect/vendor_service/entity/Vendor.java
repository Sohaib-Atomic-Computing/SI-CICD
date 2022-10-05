package io.satra.iconnect.vendor_service.entity;

import io.satra.iconnect.vendor_service.dto.VendorDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Vendor {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    private String name;

    @OneToMany(fetch = FetchType.EAGER)
    private Set<Validator> validators;

    public VendorDTO toDTO() {
        VendorDTO vendorDTO = new VendorDTO();
        vendorDTO.setId(id);
        vendorDTO.setName(name);

        if (validators != null) {
            vendorDTO.setValidators(validators.stream().map(Validator::toDTO).collect(Collectors.toSet()));
        }

        return vendorDTO;
    }
}
