package net.openwebinars.springboot.validation.error.model;

import net.openwebinars.springboot.validation.error.model.impl.ApiErrorImpl;
import net.openwebinars.springboot.validation.error.model.impl.ApiValidationSubError;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.validation.ObjectError;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface ApiError {

    HttpStatusCode getStatus();
    int getStatusCode();
    String getMessage();
    String getPath();
    LocalDateTime getDate();
    List<ApiSubError> getSubErrors();


    static ApiError fromErrorAttributes(Map<String, Object> defaultErrorAttributesMap) {

        int statusCode = (Integer) defaultErrorAttributesMap.get("status");


        ApiErrorImpl result =
                ApiErrorImpl.builder()
                        .status(HttpStatus.valueOf(statusCode))
                        .statusCode(statusCode)
                        .message((String) defaultErrorAttributesMap.getOrDefault("message", "No message available"))
                        .path((String) defaultErrorAttributesMap.getOrDefault("path", "No path available"))
                        .build();

        if (defaultErrorAttributesMap.containsKey("errors")) {

            List<ObjectError> errors = (List<ObjectError>) defaultErrorAttributesMap.get("errors");

            List<ApiSubError> subErrors = errors.stream()
                    .map(ApiValidationSubError::fromObjectError)
                    .collect(Collectors.toList());

            result.setSubErrors(subErrors);

        }

        return result;
    }

}