package test2.Test2Spring.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import test2.Test2Spring.domain.KeyWordCount;


import java.util.List;
import java.util.Optional;

public interface KeywordCountRepository extends JpaRepository<KeyWordCount, Long> {
    Optional<KeyWordCount> findByKeyword(String keyword);

    @Query("SELECT k from KeyWordCount k ORDER BY k.count desc")
    List<KeyWordCount> findTopKeywords(Pageable pageable);


    @Modifying
    @Transactional
    @Query("UPDATE KeyWordCount k SET k.count = k.count + 1 WHERE k.keyword = :keyword")
    int increment(@Param("keyword") String keyword);




}
