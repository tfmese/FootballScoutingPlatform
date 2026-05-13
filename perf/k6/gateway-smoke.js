/**
 * Basit yük / duman testi (PDF: performans testleri).
 * Önkoşul: player-service veya tüm stack ayakta (varsayılan gateway:8080).
 *
 * Çalıştırma: k6 run perf/k6/gateway-smoke.js
 * Yük / kırılma: gateway-load.js, gateway-break.js (docs/TECHNICAL_REPORT.md).
 * veya: BASE_URL=http://localhost:8081 k6 run perf/k6/gateway-smoke.js  (doğrudan player-service)
 */
import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  vus: 3,
  duration: "15s",
  thresholds: {
    http_req_failed: ["rate<0.2"],
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
