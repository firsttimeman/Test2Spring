package test2.Test2Spring;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.Map;

@RestController
@RequestMapping("/dev")
@RequiredArgsConstructor
public class DevCheckController {

    private final RestClient kakaoRestClient;

    @GetMapping("/kakao-check")
    public ResponseEntity<?> kakaoCheck(@RequestParam String query) {
        try {
            Map<?,?> body = kakaoRestClient.get()
                    .uri(uri -> uri.path("/v2/search/blog")
                            .queryParam("query", query)
                            .queryParam("size", 1)
                            .build())
                    .retrieve()
                    .body(Map.class);

            return ResponseEntity.ok(Map.of(
                    "ok", true,
                    "source", "KAKAO",
                    "meta", body.get("meta")
            ));
        } catch (Exception e) {
            return ResponseEntity.status(502).body(Map.of(
                    "ok", false,
                    "error", e.getMessage()
            ));
        }
    }


    @GetMapping("/kakao-check2")
    public ResponseEntity<?> kakaoCheck(@RequestParam String query,
                                        @RequestParam(defaultValue = "1") int size) {
        Map<?, ?> body = kakaoRestClient.get()
                .uri(uri -> uri.path("/v2/search/blog")
                        .queryParam("query", query)
                        .queryParam("size", size)   // 몇 개 볼지
                        .build())
                .retrieve()
                .body(Map.class);

        // 카카오 JSON 그대로 반환 (meta + documents 모두 포함)
        return ResponseEntity.ok(body);
    }
}
