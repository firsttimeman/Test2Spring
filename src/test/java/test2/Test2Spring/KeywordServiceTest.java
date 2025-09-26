package test2.Test2Spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import test2.Test2Spring.repository.KeywordCountRepository;
import test2.Test2Spring.service.KeywordService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class KeywordServiceTest {

    @Autowired
    KeywordService service;
    @Autowired
    KeywordCountRepository repo;

    @BeforeEach
    void clean() { repo.deleteAll(); }

    @Test
    void 키워드_서비스_테스트() {
        service.increaseCount("spring");
        var row = repo.findByKeyword("spring").orElseThrow();
        assertThat(row.getCount()).isEqualTo(1);

        service.increaseCount("spring");
        row = repo.findByKeyword("spring").orElseThrow();
        assertThat(row.getCount()).isEqualTo(2);
    }


}
