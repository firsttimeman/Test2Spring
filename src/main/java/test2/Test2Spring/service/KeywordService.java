package test2.Test2Spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test2.Test2Spring.domain.KeyWordCount;
import test2.Test2Spring.dto.PopularKeywordResponse;
import test2.Test2Spring.repository.KeywordCountRepository;


import java.util.List;

@Service
@RequiredArgsConstructor
public class KeywordService {
    private final KeywordCountRepository repository;

    @Transactional
    public void increaseCount(String keyword) {
       repository.findByKeyword(keyword)
               .ifPresentOrElse(
                       KeyWordCount::increase,
                       () -> repository.save(new KeyWordCount(keyword))
               );
    }

    @Transactional
    public List<PopularKeywordResponse> getTop10KeyWords() {

        return repository.findTopKeywords(PageRequest.of(0, 10)).stream()
                .map(k -> new PopularKeywordResponse(k.getKeyword(), k.getCount()))
                .toList();
    }
}
