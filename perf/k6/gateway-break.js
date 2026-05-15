
import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  stages: [
    { duration: "30s", target: 50  },   // ısınma
    { duration: "1m",  target: 100 },   // orta yük
    { duration: "1m",  target: 200 },   // yüksek yük
    { duration: "1m",  target: 300 },   // kırılma arayışı
    { duration: "30s", target: 0   },   // ramp-down
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
