package io.satra.iconnect.user_service.controller.exception_handler;

import io.satra.iconnect.user_service.dto.ResponseDTO;
import io.satra.iconnect.user_service.exception.generic.BadRequestException;
import io.satra.iconnect.user_service.exception.generic.EntityNotFoundException;
import io.satra.iconnect.user_service.exception.generic.ForbiddenRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalRestExceptionHandler {

  @ExceptionHandler(EntityNotFoundException.class)
  protected ResponseEntity<ResponseDTO> handleEntityNotFound(EntityNotFoundException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorResponseDTO(exception));
  }

  @ExceptionHandler(ForbiddenRequestException.class)
  protected ResponseEntity<ResponseDTO> handleForbidden(ForbiddenRequestException exception) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createErrorResponseDTO(exception));
  }

  @ExceptionHandler(BadRequestException.class)
  protected ResponseEntity<ResponseDTO> handleBadRequest(BadRequestException exception) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponseDTO(exception));
  }

  @ExceptionHandler(value = Exception.class)
  public ResponseEntity<ResponseDTO> handleGenericException(Exception exception) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponseDTO(exception));
  }

  private ResponseDTO createErrorResponseDTO(Exception exception) {
    return ResponseDTO.builder()
        .message(exception.getMessage())
        .success(Boolean.FALSE)
        .build();
  }
}

