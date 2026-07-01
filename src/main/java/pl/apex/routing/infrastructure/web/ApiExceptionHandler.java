package pl.apex.routing.infrastructure.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.apex.routing.domain.exception.RouteNotFoundException;
import pl.apex.routing.domain.exception.RouteStateException;
import pl.apex.routing.domain.exception.RouteValidationException;

import java.util.Map;

/**
 * Tlumaczy wyjatki domeny kontekstu Route na kody HTTP:
 *  - zlamany inwariant / zle dane        -> 400
 *  - trasa nie istnieje                  -> 404
 *  - niedozwolona zmiana stanu           -> 409
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(RouteValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidation(RouteValidationException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(RouteNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(RouteNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(RouteStateException.class)
    public ResponseEntity<Map<String, String>> handleState(RouteStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
    }
}
