package test2.Test2Spring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import test2.Test2Spring.dto.KakaoBlogResponse;
import test2.Test2Spring.service.KeywordService;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/loadtest")
public class LoadTestController {

    private final KeywordService keywordService;

    @GetMapping("/blog/search")
    public Mono<Object> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "accuracy") String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        // 키워드 증가 테스트
        keywordService.increaseCount(query);

        // 가짜 응답 반환
        return Mono.just(fakeResponse(query, page, size));
    }

    private KakaoBlogResponse fakeResponse(String query, int page, int size) {

        KakaoBlogResponse resp = new KakaoBlogResponse();

        KakaoBlogResponse.Meta meta = new KakaoBlogResponse.Meta();
        meta.setTotalCount(10000);
        meta.setPageableCount(800);
        meta.setEnd(false);

        List<KakaoBlogResponse.Document> docs = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            KakaoBlogResponse.Document d = new KakaoBlogResponse.Document();
            d.setTitle("fake " + query);
            d.setContents("fake contents");
            d.setUrl("https://example.com/" + query);
            d.setBlogName("fake blog");
            d.setThumbnail("");
            d.setDateTime(OffsetDateTime.now());
            docs.add(d);
        }

        resp.setMeta(meta);
        resp.setDocuments(docs);

        return resp;
    }
}