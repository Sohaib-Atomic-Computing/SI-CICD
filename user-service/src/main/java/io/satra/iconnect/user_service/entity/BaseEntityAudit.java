/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.satra.iconnect.user_service.entity;

import java.time.ZonedDateTime;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;


@MappedSuperclass
@Getter
@Setter
@Builder
public abstract class BaseEntityAudit extends BaseEntity {

  @Temporal(TemporalType.TIME)
  @CreatedDate
  protected ZonedDateTime createdAt;

  @Temporal(TemporalType.TIME)
  @LastModifiedDate
  protected ZonedDateTime lastModifiedAt;
}

