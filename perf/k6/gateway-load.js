/**
 * Yük testi: gateway üzerinden oyuncu ve keşif listelerine sürekli trafik.
 * PDF: "yük testleri".
 *
 * Önkoşul: docker compose --profile app (veya yerelde gateway + servisler).
 * Çalıştırma: k6 run perf/k6/gateway-load.js
 */
import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  stages: [
    { duration: "30s", target: 15 },
    { duration: "1m", target: 15 },
    { duration: "20s", target: 0 },
  ],
  thresholds: {
    http_req_failed: ["rate<0.1"],
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
