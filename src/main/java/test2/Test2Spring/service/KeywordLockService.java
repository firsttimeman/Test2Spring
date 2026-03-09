package test2.Test2Spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test2.Test2Spring.domain.KeyWordCount;
import test2.Test2Spring.redis.DistributeLock;
import test2.Test2Spring.repository.KeywordCountRepository;

@Service
@RequiredArgsConstructor
public class KeywordLockService {

    private final KeywordCountRepository keywordCountRepository;

    @DistributeLock(key = "'keyword:' + #keyword")
    @Transactional
    public void increaseCountWithLock(String keyword) {
        keywordCountRepository.findByKeyword(keyword)
                .ifPresentOrElse(
                        KeyWordCount::increase,
                        () -> keywordCountRepository.save(new KeyWordCount(keyword))
                );
    }
}
