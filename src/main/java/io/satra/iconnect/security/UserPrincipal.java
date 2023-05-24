package io.satra.iconnect.security;

import io.satra.iconnect.entity.Merchant;
import io.satra.iconnect.entity.User;
import io.satra.iconnect.entity.Validator;
import io.satra.iconnect.entity.enums.UserRole;
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
    private transient Merchant merchant;
    private Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(User user, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    public UserPrincipal(Validator validator, Collection<? extends GrantedAuthority> authorities) {
        this.validator = validator;
        this.authorities = authorities;
    }

    public UserPrincipal(Merchant merchant, Collection<? extends GrantedAuthority> authorities) {
        this.merchant = merchant;
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

    public static UserPrincipal build(Merchant merchant) {
        List<UserRole> roles = new ArrayList<>();
        roles.add(UserRole.ROLE_MERCHANT);

        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.toString()))
                .collect(Collectors.toList());

        return new UserPrincipal(
                merchant,
                authorities);
    }

    @Override
    public String getUsername() {
        if (user != null) {
            return user.getMobile() != null ? user.getMobile() : user.getEmail();
        } else if (validator != null) {
            return validator.getName();
        } else if (merchant != null) {
            return merchant.getAdminEmail();
        } else {
            return null;
        }
    }

    @Override
    public String getPassword() {
        if (user != null) {
            if (user.getOtpCode() == null || user.getOtpCode().isEmpty()) {
                return user.getPassword();
            }
            return user.getOtpCode();
        } else if (validator != null) {
            return validator.getEncodedKey();
        } else if (merchant != null) {
            return merchant.getPassword();
        } else {
            return null;
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
        } else if (validator != null) {
            return validator.getIsActive();
        } else if (merchant != null) {
            return merchant.getIsActive();
        } else {
            return false;
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
        } else if (validator != null) {
            return Objects.equals(validator, that.validator);
        } else if (merchant != null) {
            return Objects.equals(merchant, that.merchant);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        if (user != null) {
            return Objects.hash(user);
        } else if (validator != null) {
            return Objects.hash(validator);
        } else if (merchant != null) {
            return Objects.hash(merchant);
        } else {
            // return 0 to indicate that this object is not hashable
            return 0;
        }
    }
}
