package com.iconnect.backend.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

import java.util.Date;

@JsonInclude(Include.NON_NULL)
@Data
public class JwtResponse {

    private String token;
    private String type = "Bearer";
    private String refreshtoken;
    
    private Date issuetime;
    private Date expirytime;

    public JwtResponse() {
    }

    public JwtResponse(String token, Date issuetime, Date expirytime) {
        this.token = token;
        this.issuetime = issuetime;
        this.expirytime = expirytime;
    }

}
