package test2.Test2Spring.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "keyword_count",
        uniqueConstraints = @UniqueConstraint(name = "uk_keyword", columnNames = "keyword"),
        indexes = @Index(name = "idx_keyword", columnList = "keyword")
)
@Getter
@NoArgsConstructor
public class KeyWordCount {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String keyword;

    @Column(nullable = false)
    private long count;

    public KeyWordCount(String keyword) {
        this.keyword = keyword;
        this.count = 0L;
    }

    public void increase() {
        this.count++;
    }


}
