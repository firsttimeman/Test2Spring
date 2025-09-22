package test2.Test2Spring.error;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException e) {
        ErrorResponse response = ErrorResponse.builder()
                .status(e.getStatus())
                .error(e.getError())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(e.getStatus()).body(response);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ConstraintViolationException e) {
        List<String> details = e.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + " " + v.getMessage())
                .toList();

        ErrorResponse response = ErrorResponse.builder()
                .status(400)
                .error("Validation Error")
                .message(e.getMessage())
                .details(details)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissing(MissingServletRequestParameterException e) {

        ErrorResponse response = ErrorResponse.builder()
                .status(400)
                .error("MissingParameter")
                .message(e.getMessage())
                .details(List.of("missing parameter: " + e.getParameterName()))
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorResponse response = ErrorResponse.builder()
                .status(500)
                .error("InternalServerError")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.internalServerError().body(response);
    }


    @ExceptionHandler(ResourceAccessException.class) // 정리가 필요함 처음해보는 예외처리
    public ResponseEntity<ErrorResponse> handleNetwork(ResourceAccessException e) {
        String cause = (e.getMostSpecificCause() != null) ? String.valueOf(e.getMostSpecificCause())
                : "NetworkError";

        int status = cause.contains("Timeout") ? 504 : 502;

        String msg;
        if (cause.contains("UnknownHost") || cause.contains("UnresolvedAddress")) {
            msg = "업스트림 호스트를 찾을 수 없습니다.";
        } else if (cause.contains("ConnectException")) {
            msg = "업스트림에 연결할 수 없습니다.";
        } else if (cause.contains("Timeout")) {
            msg = "업스트림 요청이 타임아웃되었습니다.";
        } else {
            msg = "업스트림 네트워크 오류(타임아웃/연결 실패)";
        }

        ErrorResponse response = ErrorResponse.builder()
                .status(status)
                .error("UpstreamNetworkError")
                .message(msg)
                .details(List.of("cause=" + cause))
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(status).body(response);

    }




}
