
import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  stages: [
    { duration: "30s", target: 20 },   // ramp-up
    { duration: "2m",  target: 50 },   // sabit yük
    { duration: "30s", target: 0 },    // ramp-down
  ],
  thresholds: {
    http_req_failed: ["rate<0.05"],
    http_req_duration: ["p(95)<3000"],
  },
};

const BASE = __ENV.BASE_URL || "http://localhost:8080";

export default function () {
  const p = http.get(`${BASE}/api/players`);
  check(p, { "players 2xx": (r) => r.status >= 200 && r.status < 300 });

  const s = http.get(`${BASE}/api/scouts`);
  check(s, { "scouts 2xx": (r) => r.status >= 200 && r.status < 300 });

  sleep(0.05);
}
