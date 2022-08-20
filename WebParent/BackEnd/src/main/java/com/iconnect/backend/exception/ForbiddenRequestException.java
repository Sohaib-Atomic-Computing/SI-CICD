/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iconnect.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author Waqar
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenRequestException extends RuntimeException {
    public ForbiddenRequestException() {
        super();
    }

    public ForbiddenRequestException(String message) {
        super(message);
    }


    public ForbiddenRequestException(String message, Throwable cause) {
        super(message, cause);
    }
  
    
}
