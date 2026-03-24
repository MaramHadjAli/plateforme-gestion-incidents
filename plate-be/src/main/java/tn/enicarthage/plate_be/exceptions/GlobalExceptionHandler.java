package tn.enicarthage.plate_be.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler pour la gestion des erreurs de validation
 * et autres exceptions personnalisées
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gère les erreurs de validation Bean Validation (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", "Erreur de validation");
        response.put("errors", errors);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Gère les exceptions PasswordResetException
     */
    @ExceptionHandler(PasswordResetException.class)
    public ResponseEntity<?> handlePasswordResetException(
            PasswordResetException ex,
            WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getMessage(),
            HttpStatus.BAD_REQUEST.value(),
            System.currentTimeMillis(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Gère les exceptions génériques RuntimeException
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(
            RuntimeException ex,
            WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            "Une erreur interne s'est produite",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            System.currentTimeMillis(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.internalServerError().body(errorResponse);
    }

    /**
     * Gère toutes les autres exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(
            Exception ex,
            WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            "Une erreur s'est produite: " + ex.getMessage(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            System.currentTimeMillis(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.internalServerError().body(errorResponse);
    }
}

