package test2.Test2Spring.dto;

import lombok.Data;

import java.util.List;

@Data
public class NaverBlogResponse {
    private String lastBuildDate;
    private int total;
    private int start;
    private int display;
    private List<Item> items;


    @Data
    public static class Item {
        private String title;        // HTML 태그/엔티티 포함 가능
        private String link;
        private String description;  // HTML 태그/엔티티 포함 가능
        private String bloggername;
        private String bloggerlink;
        private String postdate;     // yyyyMMdd
    }
}
