import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Counter } from 'k6/metrics';

const params = {
  product: 1,
  user: 1,
  ft: true
};
const url = `http://localhost:8080/buy?product=${params.product}&user=${params.user}&ft=${params.ft}`

// Criação de métricas customizadas
const timeBetweenFailures = new Trend('time_between_failures', true); // Média do tempo entre falhas
const failures = new Counter('failures'); // Contador de falhas

let lastFailureTime = null; // Timestamp da última falha

export const options = {
  /**
   * Configura a quantidade de VUs durante a execução.
   */
  stages: [
    { duration: '30s', target: 10 },  // ramp up
    { duration: '1m', target: 10 }, // stable
  ]
};

export default function () {
  let res = http.post(url);
  check(res, {
    'is status 200': (r) => r.status === 200,
    'is service unavailable (Store or Exchange)': (r) => r.status === 503,
  });

  if (res.status !== 200) {
    // Registra falhas
    failures.add(1);

    const now = Date.now(); // Timestamp atual
    if (lastFailureTime !== null) {
      const timeDiff = now - lastFailureTime; // Tempo desde a última falha
      timeBetweenFailures.add(timeDiff); // Registra o tempo entre falhas na métrica customizada
    }
    lastFailureTime = now; // Atualiza o timestamp da última falha
  }

  // Simular tempo de espera do usuário antes da próxima requisição (think time)
  sleep(Math.random() * 3 + 1); // Pausa entre 1s e 4s
}

// custom metric to availability: MTTF = avg(time between failures)
// Plot dashboard
//K6_WEB_DASHBOARD_OPEN=true K6_WEB_DASHBOARD=true K6_WEB_DASHBOARD_PERIOD=1s K6_WEB_DASHBOARD_EXPORT=html-report.html k6 run load-tests.ts