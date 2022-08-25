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
                "email","userUniqueId" ,"phoneNumber"
       })})
@Data
public class Users extends BaseEntityAudit {

  @NotNull
  @Email
  private String email;
  @NotNull
  private String fullName;

  @NotBlank
  @JsonIgnore
  private String password;

  @JsonIgnore
  private String OTPCode;

  @JsonIgnore
  private String resetToken;

  @JsonIgnore
  @OneToOne
  private Tokens deviceToken;

  private String dpUrl;

  @NotNull
  private String phoneNumber;

  @NotNull
  private String userUniqueId;

  private String QRCode;

  private boolean isActive = false;


}
