package io.satra.iconnect.security;

import io.satra.iconnect.entity.User;
import io.satra.iconnect.entity.Validator;
import io.satra.iconnect.entity.enums.UserRole;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Data
@Slf4j
public class UserPrincipal implements UserDetails {

    private transient User user;
    private transient Validator validator;
    private Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(User user, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    public UserPrincipal(Validator validator, Collection<? extends GrantedAuthority> authorities) {
        this.validator = validator;
        this.authorities = authorities;
    }

    public static UserPrincipal build(User user) {
        List<UserRole> roles = new ArrayList<>();
        roles.add(user.getRole());

        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.toString()))
                .collect(Collectors.toList());

        return new UserPrincipal(
                user,
                authorities);
    }

    public static UserPrincipal build(Validator validator) {
        List<UserRole> roles = new ArrayList<>();
        roles.add(UserRole.ROLE_VALIDATOR);

        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.toString()))
                .collect(Collectors.toList());

        return new UserPrincipal(
                validator,
                authorities);
    }

    @Override
    public String getUsername() {
        if (user != null) {
            return user.getMobile() != null ? user.getMobile() : user.getEmail();
        } else {
            return validator.getName();
        }
    }

    @Override
    public String getPassword() {
        if (user != null) {
            if (user.getOtpCode() == null || user.getOtpCode().isEmpty()) {
                log.info("User password: {}", user.getPassword());
                return user.getPassword();
            }

            log.info("User OTP: {}", user.getOtpCode());
            return user.getOtpCode();
        } else {
            return validator.getEncodedKey();
        }
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
        if (user != null) {
            return user.getIsActive();
        } else {
            return validator.getIsActive();
        }
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
        if (user != null) {
            return Objects.equals(user, that.user);
        } else {
            return Objects.equals(validator, that.validator);
        }
    }

    @Override
    public int hashCode() {
        if (user != null) {
            return Objects.hash(user);
        } else {
            return Objects.hash(validator);
        }
    }
}
