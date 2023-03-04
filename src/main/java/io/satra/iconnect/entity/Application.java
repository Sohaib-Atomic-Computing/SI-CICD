package io.satra.iconnect.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.satra.iconnect.entity.base.BaseEntityAudit;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="applications")
public class Application extends BaseEntityAudit {
    @NotNull
    private String name;
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    @Builder.Default
    private Boolean isActive = Boolean.TRUE;
    @OneToMany(mappedBy = "application", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ApiKey> apiKeys;
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
        Application application = (Application) o;
        return id != null && id.equals(application.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }



}
