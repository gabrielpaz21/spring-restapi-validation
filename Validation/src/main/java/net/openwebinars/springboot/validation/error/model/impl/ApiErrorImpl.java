package net.openwebinars.springboot.validation.error.model.impl;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.openwebinars.springboot.validation.error.model.ApiError;
import net.openwebinars.springboot.validation.error.model.ApiSubError;
import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor @AllArgsConstructor
public class ApiErrorImpl implements ApiError {

    private HttpStatusCode status;
    private String message;
    private String path;

    private int statusCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    @Builder.Default
    private LocalDateTime date = LocalDateTime.now();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ApiSubError> subErrors;

    public int getStatusCode() {
        return status != null ? status.value() : 0;
    }

}
