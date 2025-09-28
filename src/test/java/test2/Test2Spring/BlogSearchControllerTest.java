//package test2.Test2Spring;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//import test2.Test2Spring.controller.BlogSearchController;
//import test2.Test2Spring.dto.KakaoBlogResponse;
//import test2.Test2Spring.error.GlobalExceptionHandler;
//import test2.Test2Spring.service.BlogSearchService;
//import test2.Test2Spring.service.KeywordService;
//
//import java.time.OffsetDateTime;
//import java.util.List;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(BlogSearchController.class)
//@Import(GlobalExceptionHandler.class)
//
//public class BlogSearchControllerTest {
//
//    @Autowired
//    MockMvc mockMvc;
//
//    @Autowired
//    ObjectMapper objectMapper;
//
//    @MockitoBean
//    BlogSearchService blogSearchService;
//
//    @MockitoBean
//    KeywordService keywordService;
//
//    @Test
//    void 검색성공_200() throws Exception {
//        KakaoBlogResponse response = new KakaoBlogResponse();
//        KakaoBlogResponse.Meta meta = new KakaoBlogResponse.Meta();
//        meta.setTotalCount(100);
//        meta.setEnd(false);
//        meta.setPageableCount(50);
//        response.setMeta(meta);
//
//        KakaoBlogResponse.Document d = new KakaoBlogResponse.Document();
//        d.setTitle("t");
//        d.setContents("c");
//        d.setUrl("http://u");
//        d.setBlogName("b");
//        d.setThumbnail("");
//        d.setDateTime(OffsetDateTime.parse("2025-09-20T16:00:00Z"));
//        response.setDocuments(List.of(d));
//
//        Mockito.when(blogSearchService.searchBlog("스프링", "accuracy", 1, 10))
//                .thenReturn(response);
//
//        mockMvc.perform(get("/api/blog/search")
//                        .param("query", "스프링")
//                        .param("sort", "accuracy")
//                        .param("page", "1")
//                        .param("size", "10"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.meta.total_count").value(100))
//                .andExpect(jsonPath("$.documents[0].blogname").value("b"));
//
//    }
//
//    @Test
//    void 검증실패_잘못된값_400() throws Exception {
//        mockMvc.perform(get("/api/blog/search")
//                .param("query", "spring")
//                .param("sort", "wrong")
//                .param("page", "1")
//                .param("size", "10"))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error").value("Validation Error"));
//    }
//
//
//    @Test
//    void 검증실패_값입력_누락() throws Exception {
//        mockMvc.perform(get("/api/blog/search")
//                .param("sort", "accuracy")
//                .param("page", "1")
//                .param("size", "10"))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error").value("MissingParameter"));
//    }
//
//
//}
