package test2.Test2Spring.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    RestClient kakaoRestClient(
            RestClient.Builder builder,
            @Value("${kakao.base-url}") String baseUrl,
            @Value("${kakao.rest-api-key}") String apiKey
    ) {
        return builder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + apiKey)
                .build();
    }
}
