/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iconnect.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

/**
 *
 * @author smile
 */
@Entity
@Table
@Data
public class UserRefreshToken extends BaseEntity{


    @Column(nullable = false)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Users user;

    public UserRefreshToken() {
    }

    public UserRefreshToken(String token, Users user) {
        this.token = token;
        this.user = user;
    }

 

}
