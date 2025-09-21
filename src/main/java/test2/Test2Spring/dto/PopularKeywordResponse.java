package test2.Test2Spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PopularKeywordResponse {
    private String keyword;
    private long count;
}
