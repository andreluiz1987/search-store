package com.company.searchstore.exception;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestController
@RestControllerAdvice
@Slf4j
public class GlobalControllerExceptionHandler {

  @ExceptionHandler(BindException.class)
  public Map<String, String> handleBindException(BindException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
        errors.put(error.getField(), error.getDefaultMessage()));
    return errors;
  }
}
