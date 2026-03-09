import http from "k6/http";
import { check } from "k6";

export const options = {
    scenarios: {
        burst: {
            executor: "constant-arrival-rate",
            rate: 2000,          // 초당 2000 요청 (환경에 맞게 올/내려)
            timeUnit: "1s",
            duration: "30s",
            preAllocatedVUs: 200,
            maxVUs: 500,
        },
    },
    thresholds: {
        http_req_failed: ["rate<0.01"],
    },
};

const BASE = __ENV.BASE_URL || "http://spring:8080";
const KEYWORD = "spring";

export default function () {
    const res = http.get(`${BASE}/api/loadtest/blog/search?query=${encodeURIComponent(KEYWORD)}&sort=accuracy&page=1&size=10`);
    check(res, { "200": (r) => r.status === 200 });
}