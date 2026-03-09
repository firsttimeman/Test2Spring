import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
    stages: [
        { duration: "20s", target: 50 },
        { duration: "2m", target: 200 },
        { duration: "20s", target: 0 },
    ],
    thresholds: {
        http_req_failed: ["rate<0.01"],
        http_req_duration: ["p(95)<800"],
    },
};

const BASE = __ENV.BASE_URL || "http://spring:8080";
const KEYWORD = "spring";

export default function () {
    // ✅ 95% 검색(=카운트 증가), 5% 인기조회
    if (Math.random() < 0.95) {
        const url = `${BASE}/api/loadtest/blog/search?query=${encodeURIComponent(KEYWORD)}&sort=accuracy&page=1&size=10`;
        const res = http.get(url);
        check(res, { "search 200": (r) => r.status === 200 });
    } else {
        const res = http.get(`${BASE}/api/keywords/popular`);
        check(res, { "popular 200": (r) => r.status === 200 });
    }

    // ✅ sleep 줄이면 더 격렬하게 붙음
    sleep(0.01);
}