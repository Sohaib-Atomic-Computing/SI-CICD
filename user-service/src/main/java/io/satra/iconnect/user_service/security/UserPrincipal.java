package io.satra.iconnect.user_service.security;

import io.satra.iconnect.user_service.entity.User;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
public class UserPrincipal implements UserDetails {

  private final User user;

  @Override
  public String getUsername() {
    return user.getPhoneNumber();
  }

  @Override
  public String getPassword() {
    if (user.getOtpCode() == null || user.getOtpCode().equals("")) {
      return user.getPassword();
    }

    return user.getOtpCode();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.emptySet();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return user.getIsActive();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserPrincipal that = (UserPrincipal) o;
    return Objects.equals(user, that.user);
  }

  @Override
  public int hashCode() {
    return Objects.hash(user);
  }
}
