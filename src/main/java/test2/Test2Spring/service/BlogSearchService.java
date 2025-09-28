package test2.Test2Spring.service;

import lombok.RequiredArgsConstructor;
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
public class BlogSearchService {

    private final KeywordService keywordService;
//    private final RestClient kakaoRestClient;
    private final WebClient kakaoWebClient;
    private final WebClient naverWebClient;


//    public KakaoBlogResponse searchBlog(String query, String sort, int page, int size) {
//
//
//            KakaoBlogResponse response = kakaoRestClient.get()
//                    .uri(uri -> uri.path("/v2/search/blog")
//                            .queryParam("query", query)
//                            .queryParam("sort", sort)
//                            .queryParam("page", page)
//                            .queryParam("size", size)
//                            .build())
//                    .retrieve()
//                    .body(KakaoBlogResponse.class);
//
//            keywordService.increaseCount(query);
//
//            return response;
//
//
//    }

    public Mono<Object> searchBlog (String query, String sort, int page, int size) {
        keywordService.increaseCount(query);

        return callKakao(query, sort, page, size)
                .cast(Object.class)
                .onErrorResume(ex -> callNaver(query, sort, page, size).cast(Object.class)); // 에러로 보내겠다느건가? 그리고 왜
        //오브젝트로 보내지? 어디가 받을지 몰라서 카카오나? 네이버?

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
                // 4xx/5xx 를 예외로 변환 (메시지는 그대로 담아줌)
                .onStatus(s -> s.is4xxClientError() || s.is5xxServerError(),
                        resp -> resp.createException()) // 처음보는데?
                .bodyToMono(KakaoBlogResponse.class) // 이게 뭐자?????
                // 네트워크 지연 방지용 타임아웃
                .timeout(Duration.ofSeconds(2))
                // 일시적 네트워크 에러 재시도 (최대 2회, 지수 백오프)
                .retryWhen(Retry.backoff(2, Duration.ofMillis(200))
                        .filter(ex -> ex instanceof WebClientRequestException)); // 처음보는데 설명좀

    }


    private Mono<NaverBlogResponse> callNaver(String query, String sort, int page, int size) {

        int start = (page - 1) * size + 1;            // 1-based
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
