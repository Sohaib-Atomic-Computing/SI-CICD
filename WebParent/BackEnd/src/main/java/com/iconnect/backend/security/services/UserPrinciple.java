package com.iconnect.backend.security.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iconnect.backend.dtos.QRCodeDTO;
import com.iconnect.backend.model.Users;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;

@Data
public class UserPrinciple implements UserDetails {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String email;

    private String phoneNumber;

    private String userUniqueId;

    private String dpUrl;

    private String fullName;


    @JsonIgnore
    private String OTPCode;

    private boolean isActive;

    @JsonIgnore
    private String password;
    @JsonIgnore
    private Collection<? extends GrantedAuthority> authorities;

    private String QRCode;

    public UserPrinciple(Long id,
            String email, String FullName, String password , String phoneNumber , String userUniqueId, String dpUrl , String OTPCode ,String QRCode ) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.userUniqueId = userUniqueId;
        this.dpUrl = dpUrl;
        this.OTPCode = OTPCode;
        this.QRCode = QRCode;
        this.fullName = FullName;
        //this.authorities = authorities;
        
    }

    public static UserPrinciple build(Users user) {
        return new UserPrinciple(user.getId(), user.getEmail(), user.getFullName(),
               user.getPassword(),user.getPhoneNumber(),user.getUserUniqueId(), user.getDpUrl() , user.getOTPCode(), user.getQRCode());
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return phoneNumber;
    }

    @Override
    public String getPassword() {
        if (OTPCode == null || OTPCode.equals("") )
        return  password;
        else
        return OTPCode;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
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
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserPrinciple user = (UserPrinciple) o;
        return Objects.equals(id, user.id);
    }
}
