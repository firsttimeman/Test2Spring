package test2.Test2Spring.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import test2.Test2Spring.dto.KakaoBlogResponse;

@Service
@RequiredArgsConstructor
public class BlogSearchService {

    private final KeywordService keywordService;
    private final RestClient kakaoRestClient;
    private final ObjectMapper objectMapper;

    public KakaoBlogResponse searchBlog(String query, String sort, int page, int size) {


            KakaoBlogResponse response = kakaoRestClient.get()
                    .uri(uri -> uri.path("/v2/search/blog")
                            .queryParam("query", query)
                            .queryParam("sort", sort)
                            .queryParam("page", page)
                            .queryParam("size", size)
                            .build())
                    .retrieve()
                    .body(KakaoBlogResponse.class);

            keywordService.increaseCount(query);

            return response;


    }
}
