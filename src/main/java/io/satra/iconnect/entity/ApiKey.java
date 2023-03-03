package io.satra.iconnect.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "api_keys")
public class ApiKey extends BaseEntityAudit {
    @NotNull
    private String name;
    @NotNull
    @Column(name = "api_key", unique = true, nullable = false)
    private String key;
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    @Builder.Default
    private Boolean status = Boolean.TRUE;
    @JsonIgnore
    @JoinColumn(name = "applicationId", referencedColumnName = "id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Application application;
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
        ApiKey apiKey = (ApiKey) o;
        return id != null && id.equals(apiKey.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
