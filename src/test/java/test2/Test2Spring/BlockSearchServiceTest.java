package test2.Test2Spring;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import test2.Test2Spring.dto.KakaoBlogResponse;
import test2.Test2Spring.dto.NaverBlogResponse;
import test2.Test2Spring.service.BlogSearchService;
import org.junit.jupiter.api.AfterEach;
import java.time.Duration;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class BlockSearchServiceTest {

    @Autowired
    BlogSearchService blogSearchService;

    static MockWebServer kakaoServer;
    static MockWebServer naverServer;

    @BeforeAll
    static void startServers() throws Exception {
        kakaoServer = new MockWebServer();
        naverServer = new MockWebServer();
        kakaoServer.start();
        naverServer.start();
    }

    @AfterAll
    static void stopServers() throws Exception {
        kakaoServer.shutdown();
        naverServer.shutdown();
    }

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("kakao.base-url", () -> kakaoServer.url("/").toString().replaceAll("/$", ""));
        r.add("kakao.rest-api-key", () -> "dummy-kakao-key");
        r.add("naver.base-url", () -> naverServer.url("/").toString().replaceAll("/$", ""));
        r.add("naver.client-id", () -> "dummy-naver-id");
        r.add("naver.client-secret", () -> "dummy-naver-secret");
    }

    @Test
    void kakao_200() {

        int kakaoBefore = kakaoServer.getRequestCount();
        int naverBefore = naverServer.getRequestCount();

        kakaoServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("""
                    {
                      "meta": {"total_count":2,"pageable_count":2,"is_end":true},
                      "documents": [
                        {"title":"t1","contents":"c1","url":"http://u1","blogname":"b1","thumbnail":"","datetime":"2025-09-20T16:00:00Z"},
                        {"title":"t2","contents":"c2","url":"http://u2","blogname":"b2","thumbnail":"","datetime":"2025-09-20T17:00:00Z"}
                      ]
                    }
                """)
                .addHeader("Content-Type", "application/json"));


        Object obj = blogSearchService.searchBlog("spring", "accuracy", 1, 10)
                .block(Duration.ofSeconds(3));



        assertThat(obj).isInstanceOf(KakaoBlogResponse.class);
        KakaoBlogResponse res = (KakaoBlogResponse) obj;
        assertThat(res.getMeta().getTotalCount()).isEqualTo(2);
        assertThat(res.getDocuments()).hasSize(2);
        assertThat(res.getDocuments().get(0).getTitle()).isEqualTo("t1");

        int kakaoDelta = kakaoServer.getRequestCount() - kakaoBefore;
        int naverDelta = naverServer.getRequestCount() - naverBefore;
        assertThat(kakaoDelta).isEqualTo(1);
        assertThat(naverDelta).isEqualTo(0);



    }


    @Test
    void naver_200() throws Exception {

        int kakaoBefore = kakaoServer.getRequestCount();
        int naverBefore = naverServer.getRequestCount();


        kakaoServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("{\"msg\":\"server error\"}")
                .addHeader("Content-Type", "application/json"));

        naverServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("""
            {
              "lastBuildDate":"Sat, 27 Sep 2025 12:00:00 +0900",
              "total": 2, "start": 1, "display": 2,
              "items": [
                {"title":"n1","link":"http://n1","description":"d1","bloggername":"b1","bloggerlink":"http://bl1","postdate":"20250920"},
                {"title":"n2","link":"http://n2","description":"d2","bloggername":"b2","bloggerlink":"http://bl2","postdate":"20250921"}
              ]
            }
            """)
                .addHeader("Content-Type", "application/json"));


        Object obj = blogSearchService.searchBlog("spring", "accuracy", 1, 10)
                .block(Duration.ofSeconds(3));



        assertThat(obj).isInstanceOf(NaverBlogResponse.class);
        NaverBlogResponse res = (NaverBlogResponse) obj;
        assertThat(res.getTotal()).isEqualTo(2);
        assertThat(res.getItems()).hasSize(2);
        assertThat(res.getItems().get(0).getTitle()).isEqualTo("n1");



        int kakaoDelta = kakaoServer.getRequestCount() - kakaoBefore;
        int naverDelta = naverServer.getRequestCount() - naverBefore;
        assertThat(kakaoDelta).isEqualTo(1); // 카카오 1번(실패)
        assertThat(naverDelta).isEqualTo(1); // 네이버 1번(성공)


    }

    @Test
    void kakao와_naver_모두_실패하면_block에서_예외() {

        int kakaoBefore = kakaoServer.getRequestCount();
        int naverBefore = naverServer.getRequestCount();


        kakaoServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("{\"msg\":\"k down\"}")
                .addHeader("Content-Type","application/json"));

        naverServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("{\"msg\":\"n down\"}")
                .addHeader("Content-Type","application/json"));


        assertThatThrownBy(() ->
                blogSearchService.searchBlog("spring", "accuracy", 1, 10)
                        .block(Duration.ofSeconds(3))
        )
                .isInstanceOf(WebClientResponseException.class);


        int kakaoDelta = kakaoServer.getRequestCount() - kakaoBefore;
        int naverDelta = naverServer.getRequestCount() - naverBefore;
        assertThat(kakaoDelta).isEqualTo(1);
        assertThat(naverDelta).isEqualTo(1);
    }


}
