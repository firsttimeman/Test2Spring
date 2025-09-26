package test2.Test2Spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import test2.Test2Spring.repository.KeywordCountRepository;
import test2.Test2Spring.service.KeywordService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class KeywordServiceConcurrentTest {

    @Autowired KeywordService keywordService;
    @Autowired KeywordCountRepository repo;

    @BeforeEach
    void clean() {
        repo.deleteAll();
    }

    @Test
    void 동시성_테스트() throws Exception {

        String raw = "  스프링   부트 ";
        String normalized = "스프링 부트";
        int threads = 100;

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);


        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                try {
                    start.await(); // 동시에 시작
                    keywordService.increaseCount(raw);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown();
        boolean finished = done.await(10, TimeUnit.SECONDS);
        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);


        assertThat(finished).as("작업이 시간 내 모두 끝났는지").isTrue();
        var row = repo.findByKeyword(normalized).orElseThrow();
        assertThat(row.getCount()).isEqualTo(threads);
    }
}