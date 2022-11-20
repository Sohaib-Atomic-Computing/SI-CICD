package io.satra.iconnect.entity;

import io.satra.iconnect.dto.UserDTO;
import io.satra.iconnect.entity.base.BaseEntityAudit;
import io.satra.iconnect.entity.enums.UserRole;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User extends BaseEntityAudit {

    @NotNull
    @Size(max = 20)
    private String firstName;
    @NotNull
    @Size(max = 20)
    private String lastName;
    @NotNull
    @Email
    private String email;
    @NotBlank
    @Size(max = 120)
    private String password;
    @NotNull
    private String mobile;
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    @Builder.Default
    private Boolean isActive = Boolean.TRUE;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    private String qrCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))  return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public UserDTO toDTO() {
        return UserDTO.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .mobile(mobile)
                .isActive(isActive)
                .role(role)
                .qrCode(qrCode)
                .createdAt(createdAt)
                .lastModifiedAt(lastModifiedAt)
                .build();
    }
}
