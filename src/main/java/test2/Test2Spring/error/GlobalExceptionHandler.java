package test2.Test2Spring.error;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

//    private final ObjectMapper objectMapper;

//    @ExceptionHandler(ApiException.class)
//    public ResponseEntity<ErrorResponse> handleApiException(ApiException e) {
//        ErrorResponse response = ErrorResponse.builder()
//                .status(e.getStatus())
//                .error(e.getError())
//                .message(e.getMessage())
//                .timestamp(LocalDateTime.now())
//                .build();
//
//        return ResponseEntity.status(e.getStatus()).body(response);
//    }


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


//    @ExceptionHandler(ResourceAccessException.class)
//    public ResponseEntity<ErrorResponse> handleNetwork(ResourceAccessException e) {
//        String cause = (e.getMostSpecificCause() != null) ? String.valueOf(e.getMostSpecificCause())
//                : "NetworkError";
//
//        int status = cause.contains("Timeout") ? 504 : 502;
//
//        String msg;
//        if (cause.contains("UnknownHost") || cause.contains("UnresolvedAddress")) {
//            msg = "api 호스트를 찾을 수 없습니다.";
//        } else if (cause.contains("ConnectException")) {
//            msg = "api 연결할 수 없습니다.";
//        } else if (cause.contains("Timeout")) {
//            msg = "api 요청이 타임아웃되었습니다.";
//        } else {
//            msg = "api 네트워크 오류(타임아웃/연결 실패)";
//        }
//
//        ErrorResponse response = ErrorResponse.builder()
//                .status(status)
//                .error("UpstreamNetworkError")
//                .message(msg)
//                .details(List.of("cause=" + cause))
//                .timestamp(LocalDateTime.now())
//                .build();
//
//        return ResponseEntity.status(status).body(response);
//
//    }


//    @ExceptionHandler(RestClientResponseException.class)
//    public ResponseEntity<ErrorResponse> handleRestClient(RestClientResponseException e) {
//        String raw = e.getResponseBodyAsString();
//
//        String msg;
//        if (raw.trim().startsWith("{")) {
//
//            try {
//                JsonNode node = objectMapper.readTree(raw);
//                msg = node.has("message") ? node.get("message").asText() : raw;
//            } catch (Exception ex) {
//                msg = raw;
//            }
//        } else {
//            msg = raw;
//        }
//
//        ErrorResponse response = ErrorResponse.builder()
//                .status(e.getRawStatusCode())
//                .error("KakaoApiError")
//                .message(msg)
//                .details(List.of(raw))
//                .timestamp(LocalDateTime.now())
//                .build();
//
//        return ResponseEntity.status(e.getRawStatusCode()).body(response);
//    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ErrorResponse> handleWebClientResponse(WebClientResponseException e) {
        ErrorResponse res = ErrorResponse.builder()
                .status(e.getRawStatusCode())
                .error("UpstreamHttpError")
                .message(e.getResponseBodyAsString())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(e.getRawStatusCode()).body(res);
    }

    @ExceptionHandler(WebClientRequestException.class)
    public ResponseEntity<ErrorResponse> handleWebClientRequest(WebClientRequestException e) {
        ErrorResponse res = ErrorResponse.builder()
                .status(502)
                .error("UpstreamNetworkError")
                .message(e.getMostSpecificCause() == null ? e.getMessage() : e.getMostSpecificCause().toString())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(502).body(res);
    }



}
