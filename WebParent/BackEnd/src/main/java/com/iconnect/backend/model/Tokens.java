package com.iconnect.backend.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Waqar 02/08/2020
 */
@Entity
public class Tokens {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @NotNull
  @NotBlank
  private String token;

  @OneToOne
  private Users user;

  public Tokens(String token) {
    this.token = token;
  }

  public Tokens() {
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public Users getUser() {
    return user;
  }

  public void setUser(Users user) {
    this.user = user;
  }

  public Long getId() {
    return id;
  }
}
