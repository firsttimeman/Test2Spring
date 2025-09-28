package test2.Test2Spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import test2.Test2Spring.controller.BlogSearchController;
import test2.Test2Spring.dto.KakaoBlogResponse;
import test2.Test2Spring.error.GlobalExceptionHandler;
import test2.Test2Spring.service.BlogSearchService;
import test2.Test2Spring.service.KeywordService;

import java.net.ConnectException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;

@WebFluxTest(controllers = BlogSearchController.class)
@Import(GlobalExceptionHandler.class) // 예외 매핑을 같이 검증하고 싶다면
public class BlogSearchControllerWebClientTest {

    @Autowired
    WebTestClient webTestClient;

    @MockitoBean
    BlogSearchService blogSearchService;

    @MockitoBean
    KeywordService keywordService;

    @Test
    void 카카오_API_200정상_JSON반환() {
        KakaoBlogResponse.Meta meta = new KakaoBlogResponse.Meta();
        meta.setTotalCount(2);
        meta.setPageableCount(2);
        meta.setEnd(true);

        KakaoBlogResponse.Document doc1 = new KakaoBlogResponse.Document();
        doc1.setTitle("t1");
        doc1.setContents("c1");
        doc1.setUrl("http://u1");
        doc1.setBlogName("b1");
        doc1.setThumbnail("");
        doc1.setDateTime(OffsetDateTime.parse("2025-09-20T16:00:00Z"));

        KakaoBlogResponse.Document doc2 = new KakaoBlogResponse.Document();
        doc2.setTitle("t2");
        doc2.setContents("c2");
        doc2.setUrl("http://u2");
        doc2.setBlogName("b2");
        doc2.setThumbnail("");
        doc2.setDateTime(OffsetDateTime.parse("2025-09-20T17:00:00Z"));

        KakaoBlogResponse dto = new KakaoBlogResponse();
        dto.setMeta(meta);
        dto.setDocuments(List.of(doc1, doc2));

        given(blogSearchService.searchBlog("spring", "accuracy", 1, 10))
                .willReturn(Mono.just(dto));

        webTestClient.get().uri(uri -> uri.path("/api/blog/search")
                        .queryParam("query", "spring")
                        .queryParam("sort", "accuracy")
                        .queryParam("page", "1")
                        .queryParam("size", "10")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                // ✅ Kakao 응답 스키마에 맞게 total_count 확인
                .jsonPath("$.meta.total_count").isEqualTo(2)
                .jsonPath("$.documents[0].title").isEqualTo("t1");
    }
    @Test
    void sort_유효성_오류_400() {
        webTestClient.get().uri(uri -> uri.path("/api/blog/search")
                        .queryParam("query", "spring")
                        .queryParam("sort", "wrong")
                        .queryParam("page", "1")
                        .queryParam("size", "10")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Validation Error");
    }

    @Test
    void 업스트림_HTTP_오류_500으로_매핑() {
        var ex = WebClientResponseException.create(
                500, "Internal Server Error", HttpHeaders.EMPTY, "{\"msg\":\"n down\"}".getBytes(), StandardCharsets.UTF_8);
        given(blogSearchService.searchBlog("spring", "accuracy", 1, 10))
                .willReturn(Mono.error(ex));

        webTestClient.get().uri(uri -> uri.path("/api/blog/search")
                        .queryParam("query", "spring")
                        .queryParam("sort", "accuracy")
                        .queryParam("page", "1")
                        .queryParam("size", "10")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(500)
                .expectBody()
                .jsonPath("$.error").isEqualTo("UpstreamHttpError")
                .jsonPath("$.message").isEqualTo("{\"msg\":\"n down\"}");
    }

    @Test
    void 업스트림_네트워크_오류_502로_매핑() {
        given(blogSearchService.searchBlog("spring", "recency", 2, 5))
                .willReturn(Mono.error(new WebClientRequestException(
                        new ConnectException("connection refused"), HttpMethod.GET, URI.create("http://dummy"), HttpHeaders.EMPTY)));

        webTestClient.get().uri(uri -> uri.path("/api/blog/search")
                        .queryParam("query", "spring")
                        .queryParam("sort", "recency")
                        .queryParam("page", "2")
                        .queryParam("size", "5")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(502)
                .expectBody()
                .jsonPath("$.error").isEqualTo("UpstreamNetworkError");
    }
}
