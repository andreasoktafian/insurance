package andreas.insurance.exception.global;

import andreas.insurance.dto.response.BaseResponse;
import andreas.insurance.exception.custom.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
        BaseResponse<Void> response = BaseResponse.errorWithDetails(
                "NOT_FOUND",
                NOT_FOUND,
                "ResourceNotFound",
                ex.getMessage()
        );

        return new ResponseEntity<>(response, NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<BaseResponse<Void>> handleConflict(ConflictException ex) {
        BaseResponse<Void> response = BaseResponse.errorWithDetails(
                "CONFLICT",
                CONFLICT,
                "Conflict",
                ex.getMessage()
        );

        return new ResponseEntity<>(response, CONFLICT);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<BaseResponse<Void>> handleUnauthorized(UnauthorizedException ex) {
        BaseResponse<Void> response = BaseResponse.errorWithDetails(
                "UNAUTHORIZED",
                UNAUTHORIZED,
                "Unauthorized",
                ex.getMessage()
        );

        return new ResponseEntity<>(response, UNAUTHORIZED);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseResponse<Void>> handleBusiness(BusinessException ex) {
        BaseResponse<Void> response = BaseResponse.errorWithDetails(
                "BAD_REQUEST",
                BAD_REQUEST,
                "BusinessError",
                ex.getMessage()
        );

        return new ResponseEntity<>(response, BAD_REQUEST);
    }

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<BaseResponse<Void>> handleServer(ServerException ex) {
        log.error("ServerException occurred: {}", ex.getMessage(), ex);

        BaseResponse<Void> response = BaseResponse.errorWithDetails(
                "INTERNAL_SERVER_ERROR",
                INTERNAL_SERVER_ERROR,
                "ServerError",
                ex.getMessage()
        );

        return new ResponseEntity<>(response, INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        var errorMessage = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("Validation Error");

        log.warn("Constraint Violation: {}", errorMessage);

        BaseResponse<Void> response = BaseResponse.errorWithDetails(
                "BAD_REQUEST",
                BAD_REQUEST,
                "ConstraintViolation",
                errorMessage
        );

        return new ResponseEntity<>(response, BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        var errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + Objects.requireNonNullElse(error.getDefaultMessage(), "Validation Error"))
                .findFirst()
                .orElse("Validation Error");

        log.warn("Payload Validation Failed: {}", errorMessage);

        BaseResponse<Void> response = BaseResponse.errorWithDetails(
                "UNPROCESSABLE_ENTITY",
                UNPROCESSABLE_ENTITY,
                "MethodArgumentNotValid",
                errorMessage
        );

        return new ResponseEntity<>(response, UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<BaseResponse<Void>> handleMethodValidationException(HandlerMethodValidationException ex) {
        var errorMessage = ex.getAllErrors().stream()
                .map(error -> Objects.requireNonNullElse(error.getDefaultMessage(), "Validation Error"))
                .findFirst()
                .orElse("Validation Error");

        log.warn("Method Validation Failed: {}", errorMessage);

        BaseResponse<Void> response = BaseResponse.errorWithDetails(
                "BAD_REQUEST",
                BAD_REQUEST,
                "MethodValidationError",
                errorMessage
        );

        return new ResponseEntity<>(response, BAD_REQUEST);
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<BaseResponse<Void>> handleMissingPathVariableException(MissingPathVariableException ex) {
        var errorMessage = "Parameter '" + ex.getVariableName() + "' is required in the URL.";

        BaseResponse<Void> response = BaseResponse.errorWithDetails(
                "BAD_REQUEST",
                BAD_REQUEST,
                "MissingPathVariable",
                errorMessage
        );

        return new ResponseEntity<>(response, BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BaseResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        var errorMessage = "Parameter '" + ex.getName() + "' has an invalid or empty value.";

        BaseResponse<Void> response = BaseResponse.errorWithDetails(
                "BAD_REQUEST",
                BAD_REQUEST,
                "TypeMismatchError",
                errorMessage
        );

        return new ResponseEntity<>(response, BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<BaseResponse<Void>> handleMissingParam(MissingServletRequestParameterException ex) {
        var errorMessage = "Query parameter '" + ex.getParameterName() + "' is required.";

        BaseResponse<Void> response = BaseResponse.errorWithDetails(
                "BAD_REQUEST",
                BAD_REQUEST,
                "MissingQueryParameter",
                errorMessage
        );

        return new ResponseEntity<>(response, BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<BaseResponse<Void>> handleDataIntegrity(DataIntegrityViolationException ex) {
        BaseResponse<Void> response = BaseResponse.errorWithDetails(
                "CONFLICT",
                CONFLICT,
                "Conflict",
                "Data conflict: The record is currently in use and cannot be deleted or modified."
        );

        return new ResponseEntity<>(response, CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleUnexpected(Exception ex) {
        var errorMessage = "Internal Server Error";

        log.error("CRITICAL: {} : {}", errorMessage, ex.getMessage(), ex);

        BaseResponse<Void> response = BaseResponse.errorWithDetails(
                "INTERNAL_SERVER_ERROR",
                INTERNAL_SERVER_ERROR,
                "ExceptionError",
                errorMessage
        );

        return new ResponseEntity<>(response, INTERNAL_SERVER_ERROR);
    }

}
