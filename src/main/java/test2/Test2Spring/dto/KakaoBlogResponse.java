package test2.Test2Spring.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Data
public class KakaoBlogResponse {

    private Meta meta;
    private List<Document> documents;

    @Data
    public static class Meta {
        @JsonProperty("total_count")
        private int totalCount;

        @JsonProperty("pageable_count")
        private int pageableCount;

        @JsonProperty("is_end")
        private boolean end;
    }

    @Data
    public static class Document {
        private String title;
        private String contents;
        private String url;

        @JsonProperty("blogname")
        private String blogName;

        private String thumbnail;

        @JsonProperty("datetime")
        private OffsetDateTime dateTime;
    }
}
