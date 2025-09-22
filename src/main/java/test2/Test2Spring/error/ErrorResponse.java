package test2.Test2Spring.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private final int status;
    private final String error;
    private final String message;
    private final List<String> details;
    private final LocalDateTime timestamp;
}
