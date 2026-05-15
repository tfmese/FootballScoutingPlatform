
import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  vus: 5,
  duration: "30s",
  thresholds: {
    http_req_failed: ["rate<0.05"],
    http_req_duration: ["p(95)<2000"],
  },
};

const BASE = __ENV.BASE_URL || "http://localhost:8080";

export default function () {
  const res = http.get(`${BASE}/api/players`);
  check(res, {
    "players list 2xx": (r) => r.status >= 200 && r.status < 300,
  });
  sleep(0.2);
}
