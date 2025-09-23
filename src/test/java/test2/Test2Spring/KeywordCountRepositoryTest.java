package test2.Test2Spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import test2.Test2Spring.domain.KeyWordCount;
import test2.Test2Spring.repository.KeywordCountRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class KeywordCountRepositoryTest {

    @Autowired
    KeywordCountRepository repository;

    @Test
    void findTopKeyWord() {
        for(int i = 1; i <= 20; i++) {
            KeyWordCount k = new KeyWordCount("k" + i);
            for(int c = 1; c < i; c++) {
                k.increase();
            }
            repository.save(k);
        }

        List<KeyWordCount> top10 = repository.findTopKeywords(PageRequest.of(0, 10));

        assertThat(top10).hasSize(10);
        assertThat(top10.get(0).getKeyword()).isEqualTo("k20");

    }
}
