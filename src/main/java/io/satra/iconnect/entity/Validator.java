package io.satra.iconnect.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.satra.iconnect.dto.ValidatorDTO;
import io.satra.iconnect.entity.base.BaseEntityAudit;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "validators")
public class Validator extends BaseEntityAudit {
    @NotNull
    private String name;
    @NotNull
    private String validatorKey;
    @NotNull
    private String encodedKey;
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    @Builder.Default
    private Boolean isActive = Boolean.TRUE;
    @JsonIgnore
    @JoinColumn(name = "createdBy", referencedColumnName = "id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User createdBy;
    @JsonIgnore
    @JoinColumn(name = "lastModifiedBy", referencedColumnName = "id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User lastModifiedBy;
    @JsonIgnore
    @JoinColumn(name = "vendorId", referencedColumnName = "id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Vendor vendor;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Validator validator = (Validator) o;
        return id != null && id.equals(validator.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public ValidatorDTO toDTO() {
        return ValidatorDTO.builder()
                .id(id)
                .name(name)
                .validatorKey(validatorKey)
                .vendor(vendor != null ? vendor.toDTO() : null)
                .createdAt(createdAt)
                .createdBy(createdBy != null ? createdBy.getFirstName() + ' ' + createdBy.getLastName() : null)
                .lastModifiedAt(lastModifiedAt)
                .lastModifiedBy(lastModifiedBy != null ? lastModifiedBy.getFirstName() + ' ' + lastModifiedBy.getLastName() : null)
                .build();
    }
}
