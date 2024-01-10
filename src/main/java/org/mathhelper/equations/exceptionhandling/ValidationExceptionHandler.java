package org.mathhelper.equations.exceptionhandling;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice(basePackages = "org.mathhelper.equations")
public class ValidationExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException e) {
        return ResponseEntity.unprocessableEntity()
                .body(e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).toArray());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        if (e.getMostSpecificCause().getMessage().contains("already exists")
                || e.getMostSpecificCause().getMessage().contains("уже существует")) {
            return ResponseEntity.badRequest().body("Equation already exists");
        }
        return ResponseEntity.badRequest().body(e.getMostSpecificCause().getMessage());
    }
}
