package io.satra.iconnect.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.satra.iconnect.dto.PromotionDTO;
import io.satra.iconnect.entity.base.BaseEntityAudit;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "promotions")
public class Promotion extends BaseEntityAudit {

    @NotNull
    private String name;
    @NotNull
    private String description;
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    @Builder.Default
    private Boolean isActive = Boolean.TRUE;
    @NotNull
    private LocalDateTime startDate;
    @NotNull
    private LocalDateTime endDate;
    @ManyToMany
    @JoinTable(
            name = "promotion_user",
            joinColumns = @JoinColumn(name = "promotion_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users;
    @JsonIgnore
    @JoinColumn(name = "vendorId", referencedColumnName = "id")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Vendor vendor;
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
        Promotion promotion = (Promotion) o;
        return id != null && id.equals(promotion.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public PromotionDTO toDTO() {
        return PromotionDTO.builder()
                .id(id)
                .name(name)
                .description(description)
                .isActive(isActive)
                .startDate(startDate)
                .endDate(endDate)
                .users(users != null ? users.stream().map(User::toDTO).collect(Collectors.toSet()) : null)
                .vendor(vendor != null ? vendor.toDTO() : null)
                .createdAt(createdAt)
                .createdBy(createdBy != null ? createdBy.getFirstName() + ' ' + createdBy.getLastName() : null)
                .lastModifiedAt(lastModifiedAt)
                .lastModifiedBy(lastModifiedBy != null ? lastModifiedBy.getFirstName() + ' ' + lastModifiedBy.getLastName() : null)
                .build();
    }

    public PromotionDTO toScannerResponseDTO() {
        return PromotionDTO.builder()
                .id(id)
                .name(name)
                .description(description)
                .isActive(isActive)
                .startDate(startDate)
                .endDate(endDate)
                .vendor(vendor != null ? vendor.toDTO() : null)
                .build();
    }
}
