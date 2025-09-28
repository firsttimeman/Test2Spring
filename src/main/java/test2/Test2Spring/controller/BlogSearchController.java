package test2.Test2Spring.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import test2.Test2Spring.dto.KakaoBlogResponse;
import test2.Test2Spring.dto.PopularKeywordResponse;
import test2.Test2Spring.service.BlogSearchService;
import test2.Test2Spring.service.KeywordService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class BlogSearchController {

    private final BlogSearchService blogSearchService;
    private final KeywordService keywordService;

//    @GetMapping("/api/blog/search")
//    public KakaoBlogResponse searchBlog(
//            @NotBlank @RequestParam String query,
//            @Pattern(regexp = "accuracy|recency") @RequestParam(defaultValue = "accuracy") String sort,
//            @Min(1) @Max(50) @RequestParam(defaultValue = "1") int page,
//            @Min(1) @Max(50) @RequestParam(defaultValue = "10") int size
//    ) {
//        return blogSearchService.searchBlog(query, sort, page, size);
//    }


    @GetMapping(value = "/api/blog/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Object> searchBlog(
            @NotBlank @RequestParam String query,
            @Pattern(regexp = "accuracy|recency") @RequestParam(defaultValue = "accuracy") String sort,
            @Min(1) @Max(50) @RequestParam(defaultValue = "1") int page,
            @Min(1) @Max(50) @RequestParam(defaultValue = "10") int size
    ) {
        return blogSearchService.searchBlog(query, sort, page, size);
    }



    @GetMapping("/api/keywords/popular")
    public List<PopularKeywordResponse> getPopularKeywords() {
        return keywordService.getTop10KeyWords();
    }
}
