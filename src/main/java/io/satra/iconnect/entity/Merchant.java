package io.satra.iconnect.entity;

import io.satra.iconnect.dto.MerchantDTO;
import io.satra.iconnect.entity.base.BaseEntityAudit;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "merchants")
public class Merchant extends BaseEntityAudit {
    @NotEmpty
    @Size(min = 3, max = 256)
    private String name;
    @Size(max = 255)
    private String logo;
    @Size(max = 50)
    private String abbreviation;
    @Size(max = 256)
    @NotEmpty
    private String adminFirstName;
    @Size(max = 256)
    @NotEmpty
    private String adminLastName;
    @Email
    private String adminEmail;
    @Size(max = 256)
    @NotEmpty
    private String mobile;
    @Size(max = 120)
    private String password;
    private String firstAddress;
    private String secondAddress;
    @Size(max = 256)
    @NotEmpty
    private String city;
    @Size(max = 256)
    private String state;
    @Size(max = 256)
    @NotEmpty
    private String country;
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    @Builder.Default
    private Boolean isActive = Boolean.TRUE;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))  return false;
        Merchant merchant = (Merchant) o;
        return id != null && Objects.equals(id, merchant.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public MerchantDTO toDTO() {
        return MerchantDTO.builder()
                .id(id)
                .name(name)
                .logo(logo)
                .abbreviation(abbreviation)
                .adminFirstName(adminFirstName)
                .adminLastName(adminLastName)
                .adminEmail(adminEmail)
                .mobile(mobile)
                .firstAddress(firstAddress)
                .secondAddress(secondAddress)
                .city(city)
                .state(state)
                .country(country)
                .isActive(isActive)
                .createdAt(createdAt)
                .lastModifiedAt(lastModifiedAt)
                .build();
    }

    public MerchantDTO toViewDTO() {
        return MerchantDTO.builder()
                .id(id)
                .name(name)
                .logo(logo)
                .abbreviation(abbreviation)
                .mobile(mobile)
                .firstAddress(firstAddress)
                .secondAddress(secondAddress)
                .city(city)
                .state(state)
                .country(country)
                .build();
    }
}
