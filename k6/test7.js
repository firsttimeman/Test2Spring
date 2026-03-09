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
const KEYWORDS = ["spring", "spring", "jpa"]; // spring 2배 경합

function pick(arr) {
    return arr[Math.floor(Math.random() * arr.length)];
}

export default function () {
    if (Math.random() < 0.95) {
        const q = pick(KEYWORDS);
        const res = http.get(
            `${BASE}/api/loadtest/blog/search?query=${encodeURIComponent(q)}&sort=accuracy&page=1&size=10`
        );

        const ok = check(res, { "search 200": (r) => r.status === 200 });
        if (!ok) {
            console.log(`SEARCH FAIL status=${res.status} body=${(res.body || "").slice(0, 160)}`);
        }
    } else {
        const res = http.get(`${BASE}/api/keywords/popular`);

        const ok = check(res, { "popular 200": (r) => r.status === 200 });
        if (!ok) {
            console.log(`POPULAR FAIL status=${res.status} body=${(res.body || "").slice(0, 160)}`);
        }
    }

    sleep(0.01);
}