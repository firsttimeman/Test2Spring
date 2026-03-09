import http from "k6/http";
import { sleep } from "k6";

export const options = {
    vus: 50,          // 동시에 50명 접속
    duration: "30s",  // 30초 동안 테스트
};

export default function () {

    // 랜덤으로 검색어 선택
    const queries = ["spring", "java", "redis", "mysql", "docker"];
    const query = queries[Math.floor(Math.random() * queries.length)];

    // 랜덤으로 API 선택
    const r = Math.random();

    if (r < 0.8) {
        // 80%는 검색 API
        http.get(`http://spring:8080/api/blog/search?query=${query}`);
    } else {
        // 20%는 인기 키워드 API
        http.get("http://spring:8080/api/keywords/popular");
    }

    sleep(1); // 1초 쉬기
}