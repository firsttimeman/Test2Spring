import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
    vus: 200,
    duration: "2m",
    thresholds: {
        http_req_failed: ["rate<0.01"],
    },
};

const BASE = __ENV.BASE_URL || "http://spring:8080";
const KEYWORDS = ["spring", "spring"]; // 일부러 동일 키워드만

export default function () {
    const q = KEYWORDS[0];
    const res = http.get(`${BASE}/api/loadtest/blog/search?query=${q}`);
    check(res, { "200": (r) => r.status === 200 });
    sleep(0.05);
}