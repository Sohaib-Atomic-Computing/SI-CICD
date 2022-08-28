/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iconnect.backend.dtos;

import lombok.Data;

/**
 *
 * @author Waqar
 */
@Data
public class Response {

    public Response(String message, Boolean success, Object data) {
        this.message = message;
        this.success = success;
        this.data = data;
    }

    private String message;
    private Boolean success;
    private Object data;


     
}
