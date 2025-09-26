package test2.Test2Spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import test2.Test2Spring.domain.KeyWordCount;
import test2.Test2Spring.dto.PopularKeywordResponse;
import test2.Test2Spring.repository.KeywordCountRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
public class KeywordService {
    private final KeywordCountRepository repository;



    public void increaseCount(String raw) {
        String keyword = normalize(raw);
        if (keyword.isEmpty()) return;


        if (repository.increment(keyword) > 0) return;

        try {
            insertKeywordIfAbsent(keyword); // count=1 로 INSERT
            return;
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
        }


        repository.increment(keyword);
    }

    @Transactional
    public void insertKeywordIfAbsent(String keyword) {
        KeyWordCount count = new KeyWordCount(keyword);
        count.increase();
        repository.saveAndFlush(count);
    }






    @Transactional
    public List<PopularKeywordResponse> getTop10KeyWords() {

        return repository.findTopKeywords(PageRequest.of(0, 10)).stream()
                .map(k -> new PopularKeywordResponse(k.getKeyword(), k.getCount()))
                .toList();
    }

    private String normalize(String raw) {
        if (raw == null) return "";
        return raw.trim().replaceAll("\\s+", " ").toLowerCase();
    }
}
