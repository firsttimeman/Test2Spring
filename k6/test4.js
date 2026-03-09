import http from "k6/http";
import { check } from "k6";

export const options = { vus: 100, iterations: 5000 };

const BASE = __ENV.BASE_URL || "http://spring:8080";
const KEYWORD = "spring";

export default function () {
    const res = http.get(`${BASE}/api/loadtest/blog/search?query=${KEYWORD}`);
    const ok = check(res, { "200": (r) => r.status === 200 });

    if (!ok) {
        console.log(`FAIL status=${res.status} body=${(res.body || "").slice(0, 120)}`);
    }
}