package test2.Test2Spring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import test2.Test2Spring.dto.KakaoBlogResponse;
import test2.Test2Spring.dto.PopularKeywordResponse;
import test2.Test2Spring.service.BlogSearchService;
import test2.Test2Spring.service.KeywordService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BlogSearchController {

    private final BlogSearchService blogSearchService;
    private final KeywordService keywordService;

    @GetMapping("/api/blog/search")
    public KakaoBlogResponse searchBlog(
            @RequestParam String query,
            @RequestParam(defaultValue = "accuracy") String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return blogSearchService.searchBlog(query, sort, page, size);
    }

    @GetMapping("/api/keywords/popular")
    public List<PopularKeywordResponse> getPopularKeywords() {
        return keywordService.getTop10KeyWords();
    }
}
