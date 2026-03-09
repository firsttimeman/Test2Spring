package test2.Test2Spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BlogSearchService {

    private final KeywordService keywordService;
    private final BlogSearchProvider provider;

    public Mono<Object> searchBlog(String query, String sort, int page, int size) {
        keywordService.increaseCount(query);
        return provider.search(query, sort, page, size);
    }
}