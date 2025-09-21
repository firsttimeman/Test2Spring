package test2.Test2Spring.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import test2.Test2Spring.domain.KeyWordCount;


import java.util.List;
import java.util.Optional;

public interface KeywordCountRepository extends JpaRepository<KeyWordCount, Long> {
    Optional<KeyWordCount> findByKeyword(String keyword);

    @Query("SELECT k from KeyWordCount k ORDER BY k.count desc")
    List<KeyWordCount> findTopKeywords(Pageable pageable);

}
