package net.openwebinars.springboot.validation.error;

import net.openwebinars.springboot.validation.error.model.impl.ApiErrorImpl;
import net.openwebinars.springboot.validation.error.model.impl.ApiValidationSubError;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalRestControllerAdvice extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex,
                                                             Object body,
                                                             @NonNull HttpHeaders headers,
                                                             @NonNull HttpStatusCode status,
                                                             @NonNull WebRequest request) {
        return buildApiError(ex.getMessage(), request, status);
    }

    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<?> handleNotFoundException(EntityNotFoundException exception,
                                                     WebRequest request) {
        return buildApiError(exception.getMessage(), request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException exception,
                                                                WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ApiErrorImpl.builder()
                                .status(HttpStatus.BAD_REQUEST)
                                .message("Constraint Validation error. Please check the sublist.")
                                .path(((ServletWebRequest) request).getRequest().getRequestURI())
                                .subErrors(exception.getConstraintViolations().stream()
                                        .map(v -> ApiValidationSubError.builder()
                                                .message(v.getMessage())
                                                .rejectedValue(v.getInvalidValue())
                                                .object(v.getRootBean().getClass().getSimpleName())
                                                .field( ((PathImpl)v.getPropertyPath()).getLeafNode().asString())
                                                .build())
                                        .collect(Collectors.toList())
                                )
                                .build()
                );
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {
        return buildApiErrorWithSubErrors("Validation error. Please check the sublist.",
                                            request,
                                            status,
                                            ex.getAllErrors());
    }

    private ResponseEntity<Object> buildApiError(String message,
                                                 WebRequest request,
                                                 HttpStatusCode status) {
        return ResponseEntity
                .status(status)
                .body(
                        ApiErrorImpl.builder()
                                .status(status)
                                .message(message)
                                .path(((ServletWebRequest) request).getRequest().getRequestURI())
                                .build()
                );
    }

    private ResponseEntity<Object> buildApiErrorWithSubErrors(String message,
                                                              WebRequest request,
                                                              HttpStatusCode status,
                                                              List<ObjectError> subErrors) {
        return ResponseEntity
                .status(status)
                .body(
                        ApiErrorImpl.builder()
                                .status(status)
                                .message(message)
                                .path(((ServletWebRequest) request).getRequest().getRequestURI())
                                .subErrors(subErrors.stream()
                                        .map(ApiValidationSubError::fromObjectError)
                                        .collect(Collectors.toList())
                                )
                                .build()
                );

    }

}
