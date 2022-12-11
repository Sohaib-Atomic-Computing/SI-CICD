package io.satra.iconnect.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.satra.iconnect.dto.VendorDTO;
import io.satra.iconnect.entity.base.BaseEntityAudit;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "vendors")
public class Vendor extends BaseEntityAudit {

    @NotNull
    private String name;
    @JsonIgnore
    @JoinColumn(name = "createdBy", referencedColumnName = "id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User createdBy;
    @JsonIgnore
    @JoinColumn(name = "lastModifiedBy", referencedColumnName = "id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User lastModifiedBy;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vendor vendor = (Vendor) o;
        return id != null && id.equals(vendor.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public VendorDTO toDTO() {
        return VendorDTO.builder()
                .id(id)
                .name(name)
                .createdAt(createdAt)
                .createdBy(createdBy != null ? createdBy.getFirstName() + ' ' + createdBy.getLastName() : null)
                .lastModifiedAt(lastModifiedAt)
                .lastModifiedBy(lastModifiedBy != null ? lastModifiedBy.getFirstName() + ' ' + lastModifiedBy.getLastName() : null)
                .build();
    }
}
