/**
 * Kırılma / stres testi: eşzamanlı sanal kullanıcı sayısı kademeli artırılır.
 * PDF: "kırılma testleri" — eşikler bilinçli gevşetilir; amaç limit gözlemi ve raporlama.
 *
 * Çalıştırma: k6 run perf/k6/gateway-break.js
 * Özet JSON: k6 run --summary-export=perf/k6/out-break-summary.json perf/k6/gateway-break.js
 */
import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  stages: [
    { duration: "20s", target: 30 },
    { duration: "40s", target: 80 },
    { duration: "40s", target: 120 },
    { duration: "20s", target: 0 },
  ],
  thresholds: {
    // Stres altında hata oranı artabilir; test "gözlem" niteliğindedir.
    http_req_failed: ["rate<0.5"],
  },
};

const BASE = __ENV.BASE_URL || "http://localhost:8080";

export default function () {
  const res = http.get(`${BASE}/api/players`);
  check(res, {
    status_ok: (r) => r.status === 200 || r.status === 429 || r.status === 503,
  });
  sleep(0.01);
}
