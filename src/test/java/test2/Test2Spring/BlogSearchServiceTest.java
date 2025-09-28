//package test2.Test2Spring;
//
//import okhttp3.mockwebserver.MockResponse;
//import okhttp3.mockwebserver.MockWebServer;
//import okhttp3.mockwebserver.SocketPolicy;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//@ActiveProfiles("test")
//@AutoConfigureMockMvc
//public class BlogSearchServiceTest {
//
//    @Autowired
//    MockMvc mvc;
//
//    static MockWebServer server;
//
//    @BeforeAll
//    static void startServer() throws Exception {
//        server = new MockWebServer();
//        server.start();
//    }
//
//    @AfterAll
//    static void stopServer() throws Exception {
//        server.shutdown();
//    }
//
//    @DynamicPropertySource
//    static void props(DynamicPropertyRegistry r) {
//        r.add("kakao.base-url", () -> server.url("/").toString().replaceAll("/$", ""));
//        r.add("kakao.rest-api-key", () -> "dummy-key");
//    }
//
//    @Test
//    void 카카오_API_200정상() throws Exception {
//
//        String body = """
//        {
//          "meta": {"total_count":2,"pageable_count":2,"is_end":true},
//          "documents": [
//            {"title":"t1","contents":"c1","url":"http://u1","blogname":"b1","thumbnail":"","datetime":"2025-09-20T16:00:00Z"},
//            {"title":"t2","contents":"c2","url":"http://u2","blogname":"b2","thumbnail":"","datetime":"2025-09-20T17:00:00Z"}
//          ]
//        }""";
//        server.enqueue(new MockResponse().setResponseCode(200)
//                .setBody(body)
//                .setHeader("Content-Type", "application/json"));
//
//
//        mvc.perform(get("/api/blog/search")
//                        .param("query", "spring")
//                        .param("sort", "accuracy")
//                        .param("page", "1")
//                        .param("size", "10"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.meta.total_count").value(2))
//                .andExpect(jsonPath("$.documents[0].title").value("t1"));
//    }
//
//
//    @Test
//    void 카카오_API_401_GlobalHandler() throws Exception {
//
//        server.enqueue(new MockResponse().setResponseCode(401)
//                .setBody("{\"errorType\":\"AccessDeniedError\",\"message\":\"wrong appkey\"}")
//                .setHeader("Content-Type", "application/json"));
//
//
//        mvc.perform(get("/api/blog/search")
//                        .param("query", "spring")
//                        .param("sort", "accuracy")
//                        .param("page", "1")
//                        .param("size", "10"))
//                .andExpect(status().isUnauthorized())
//                .andExpect(jsonPath("$.error").value("KakaoApiError"))
//                .andExpect(jsonPath("$.message").value("wrong appkey"));
//    }
//
//
//
//}
