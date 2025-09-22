package test2.Test2Spring.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import test2.Test2Spring.dto.KakaoBlogResponse;
import test2.Test2Spring.error.ApiException;

@Service
@RequiredArgsConstructor
public class BlogSearchService {

    private final KeywordService keywordService;
    private final RestClient kakaoRestClient;
    private final ObjectMapper objectMapper;

    public KakaoBlogResponse searchBlog(String query, String sort, int page, int size) {

        try {
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
        }catch (RestClientResponseException e) { // 처음보는 RestClientResponseException
            String raw = e.getResponseBodyAsString();
            String cleanMessage = "카카오 api 오류";

            try {
                JsonNode node = objectMapper.readTree(raw);
                if(node.has("message")) {
                    cleanMessage = node.get("message").asText();
                }
            } catch (Exception ignored) {
                cleanMessage = raw;
            }

            throw new ApiException(e.getRawStatusCode(), "KakaoApiError", cleanMessage);
        }

    }
}
