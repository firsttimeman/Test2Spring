package test2.Test2Spring.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "keyword_count", uniqueConstraints = @UniqueConstraint(columnNames = "keyword"))
@Getter
@NoArgsConstructor
public class KeyWordCount {

    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String keyword;

    @Column(nullable = false)
    private long count;

    public KeyWordCount(String keyword) {
        this.keyword = keyword;
        this.count = 1L;
    }

    public void increase() {
        this.count++;
    }
}
