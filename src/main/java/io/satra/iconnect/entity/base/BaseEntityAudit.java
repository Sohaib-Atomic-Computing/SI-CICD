package io.satra.iconnect.entity.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntityAudit extends BaseEntity {

    @CreatedDate
    protected LocalDateTime createdAt;
    @LastModifiedDate
    protected LocalDateTime lastModifiedAt;
}
