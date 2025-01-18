import http from 'k6/http';

const params = {
    product: 1,
    user: 1,
    ft: true
};
const url = `http://localhost:8080/buy?product=${params.product}&user=${params.user}&ft=${params.ft}`

export const options = {
    /**
     * Um agente que carrega e executa seu código paralelamente.
     */
    vus: 3,
    /**
     * Sempre que seu script é executado (por um VU) conta como uma iteração.
     * Ele carrega os N VUs e executa até chegar a quantidade definida de iterações.
     */
    // iterations: 10000,
    // duration: '1000s',
    /**
     * Configura a quantidade de VUs durante a execução.
     */
    stages: [
        { duration: '5s', target: 10 },
        { duration: '10s', target: 100 },
    ]
};

export default function () {
    http.get(url);
}

// custom metric to availability: MTTF = avg(time between failures)
// Plot dashboard
//K6_WEB_DASHBOARD_OPEN=true K6_WEB_DASHBOARD=true K6_WEB_DASHBOARD_PERIOD=1s K6_WEB_DASHBOARD_EXPORT=html-report.html k6 run load-tests.ts

// import http from 'k6/http'

// export const options = {
//     scenarios: {
//         contacts: {
//             executor: 'ramping-arrival-rate',
//             timeUnit: '1s',
//             preAllocatedVUs: 10,
//             maxVUs: 200,
//             stages: [
//                 { target: 5, duration: '2s' },
//                 { target: 15, duration: '10s' },
//                 { target: 20, duration: '5s' },
//                 { target: 0, duration: '10s' },
//             ],
//         },
//     },
//     thresholds: {
//         http_req_duration: ['p(95)<60000'], //units in miliseconds 60000ms = 1m
//         http_req_failed: ['rate<0.01'], // http errors should be less than 1%
//         checks: ['rate>0.99'],
//     },
// }

// export default function () {
//     http.get('https://httpbin.org')
// }