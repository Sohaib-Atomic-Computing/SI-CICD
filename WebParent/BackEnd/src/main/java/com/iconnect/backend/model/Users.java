package com.iconnect.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "email"
        })})
@Data
public class Users extends BaseEntityAudit {

  @NotNull
  @Email
  private String email;

  @NotNull
  @Size(min = 2, max = 20)
  private String username;

  @NotNull
  @JsonIgnore
  private String userUniqueName;

  @NotBlank
  @JsonIgnore
  private String password;

 /* @NotNull
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate dob;*/

  @JsonIgnore
  private String resetToken;

  @JsonIgnore
  @OneToOne
  private Tokens deviceToken;

  private boolean isActive = false;


}
