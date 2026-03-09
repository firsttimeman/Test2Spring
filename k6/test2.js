import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
    stages: [
        { duration: "30s", target: 50 },
        { duration: "2m", target: 200 },
        { duration: "30s", target: 0 },
    ],
    thresholds: {
        http_req_failed: ["rate<0.01"],
        http_req_duration: ["p(95)<500"], // 필요하면 조정
    },
};

const BASE = __ENV.BASE_URL || "http://spring:8080";

// 키워드 풀 (충돌/경합을 보고 싶으면 개수를 줄이고, 분산을 보고 싶으면 늘려)
const KEYWORDS = [
    "spring", "jpa", "docker", "redis", "k6",
    "mysql", "grafana", "prometheus", "aws", "jwt",
];

function pick(arr) {
    return arr[Math.floor(Math.random() * arr.length)];
}

export default function () {
    // 80% 검색, 20% 인기키워드 조회
    if (Math.random() < 0.8) {
        const q = pick(KEYWORDS);
        const url = `${BASE}/api/loadtest/blog/search?query=${encodeURIComponent(q)}&sort=accuracy&page=1&size=10`;

        const res = http.get(url);
        check(res, {
            "search 200": (r) => r.status === 200,
        });
    } else {
        const res = http.get(`${BASE}/api/keywords/popular`);
        check(res, {
            "popular 200": (r) => r.status === 200,
        });
    }

    sleep(0.1);
}