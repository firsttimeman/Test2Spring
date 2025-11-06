package test2.Test2Spring.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import test2.Test2Spring.dto.KakaoBlogResponse;
import test2.Test2Spring.dto.NaverBlogResponse;



import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlogSearchService {

    private final KeywordService keywordService;
//    private final RestClient kakaoRestClient;
    private final WebClient kakaoWebClient;
    private final WebClient naverWebClient;




    @CircuitBreaker(name = "kakaoBlogSearch", fallbackMethod = "fallbackToNaver")
    public Mono<Object> searchBlog (String query, String sort, int page, int size) {
        keywordService.increaseCount(query);

        return callKakao(query, sort, page, size)
                .cast(Object.class);

    }

    /**
     * Kakao 호출 실패 또는 서킷 OPEN 상태일 때 호출되는 fallback 메서드.
     *
     * 시그니처 규칙:
     *  - 원래 메서드(searchBlog)의 파라미터 + 마지막에 Throwable
     *  - 리턴 타입은 원래 메서드와 동일 (Mono<Object>)
     */
    private Mono<Object> fallbackToNaver(String query,
                                         String sort,
                                         int page,
                                         int size,
                                         Throwable t) {

        // 원하면 여기서 로그도 찍을 수 있음:
         log.warn("Kakao search failed, fallback to Naver. cause={}", t.toString());
        return callNaver(query, sort, page, size)
                .cast(Object.class);
    }



    private Mono<KakaoBlogResponse> callKakao(String query, String sort, int page, int size) {
        return kakaoWebClient.get()
                .uri(uri -> uri.path("/v2/search/blog")
                        .queryParam("query", query)
                        .queryParam("sort", sort)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .retrieve()
                .onStatus(s -> s.is4xxClientError() || s.is5xxServerError(),
                        resp -> resp.createException())
                .bodyToMono(KakaoBlogResponse.class)
                .timeout(Duration.ofSeconds(2))
                .retryWhen(Retry.backoff(2, Duration.ofMillis(200))
                        .filter(ex -> ex instanceof WebClientRequestException));

    }


    private Mono<NaverBlogResponse> callNaver(String query, String sort, int page, int size) {

        int start = (page - 1) * size + 1;
        String naverSort = "recency".equals(sort) ? "date" : "sim";

        return naverWebClient.get()
                .uri(uri -> uri.path("/v1/search/blog.json")
                        .queryParam("query", query)
                        .queryParam("sort", naverSort)   // sim | date
                        .queryParam("start", start)       // 1~1000
                        .queryParam("display", size)      // 1~100
                        .build())
                .retrieve()
                .onStatus(s -> s.is4xxClientError() || s.is5xxServerError(),
                        resp -> resp.createException())
                .bodyToMono(NaverBlogResponse.class)
                .timeout(Duration.ofSeconds(2));
    }


}
