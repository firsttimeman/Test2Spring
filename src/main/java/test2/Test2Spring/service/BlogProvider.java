package test2.Test2Spring.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import test2.Test2Spring.dto.KakaoBlogResponse;
import test2.Test2Spring.dto.NaverBlogResponse;

import java.time.Duration;

@Slf4j
@Component
//@Profile("!loadtest")
@RequiredArgsConstructor
public class BlogProvider implements BlogSearchProvider {

    private final WebClient kakaoWebClient;
    private final WebClient naverWebClient;

    @Override
    @CircuitBreaker(name = "kakaoBlogSearch", fallbackMethod = "fallbackToNaver")
    public Mono<Object> search(String query, String sort, int page, int size) {
        return callKakao(query, sort, page, size).cast(Object.class);
    }

    private Mono<Object> fallbackToNaver(String query, String sort, int page, int size, Throwable t) {
        log.warn("Kakao search failed, fallback to Naver. cause={}", t.toString());
        return callNaver(query, sort, page, size).cast(Object.class);
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
                        .queryParam("sort", naverSort)
                        .queryParam("start", start)
                        .queryParam("display", size)
                        .build())
                .retrieve()
                .onStatus(s -> s.is4xxClientError() || s.is5xxServerError(),
                        resp -> resp.createException())
                .bodyToMono(NaverBlogResponse.class)
                .timeout(Duration.ofSeconds(2));
    }
}