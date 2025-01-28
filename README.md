# Projeto - Tolerância a Falhas

Autores:
Márcio Tenório Júnior\
Maria Eduarda Elói Pereira

## Como Executar?

A seguir, apresentamos duas formas de executar o projeto. No entanto, para garantir a implementação completa da nossa abordagem de tolerância a falhas, é necessário executar o projeto utilizando **Docker Compose**.

### 1. Utilizando Docker Compose

Na raiz do projeto, existe um arquivo docker-compose.yml que facilita a execução de todas as APIs. Para utilizá-lo, siga os passos abaixo:

1. Certifique-se de que o Docker e o Docker Compose estão instalados na sua máquina.
2. Navegue até a pasta raiz do projeto.
3. Execute o seguinte comando no terminal:
    ```
    docker compose up -d
    ```
4. O Docker Compose cuidará de subir todas as APIs e dependências necessárias.

A API principal, **ecommerce**, estará disponível em `http://localhost:8080`, com Swagger em `http://localhost:8080/swagger-ui/index.html`.

### 2. Executando Manualmente Cada API

Caso prefira ou precise rodar as APIs separadamente, você pode executá-las manualmente. O projeto contém as seguintes APIs Spring Boot localizadas em subpastas:
- ecommerce
- exchange
- fidelity
- store

**Passos para executar:**
1. Navegue até a subpasta da API que deseja executar:
    ```
    cd <nome-da-subpasta>
    ```
2. Execute o comando abaixo para compilar e iniciar a API:
    ```
    ./mvnw spring-boot:run
    ```

## Visão Geral

![overview](./fault-tolerance-trab1-overview.jpg)

## Estratégias Utilizadas

Comentários sobre as estratégias de tolerância a falhas utilizadas no serviço **ecommerce** chamando o 
exchange, store e fidelity para concluir o processo de compra.

### Estratégias Gerais

- **Timeout**: Todas as requisições realizadas pelo **ecommerce** possuem um tempo de espera (timeout) configurado de **1 segundo**. Isso garante que, caso um serviço solicitado esteja indisponível ou demore mais do que o esperado para responder, a requisição seja abortada rapidamente, evitando impactos no desempenho geral do sistema.
  
- **Restart**: Nos containers Docker, foi configurado o parâmetro `restart: always`. Isso significa que, caso algum container falhe ou seja interrompido por qualquer motivo, ele será reiniciado automaticamente. Esta estratégia ajuda a manter a disponibilidade do sistema, minimizando o tempo de inatividade.

- **Healthcheck**: Para garantir que cada serviço está funcionando corretamente, foi configurado um **healthcheck** em todos os containers do Docker Compose. Este healthcheck verifica a integridade do serviço e, caso o serviço não esteja saudável, ele será reiniciado conforme a configuração de restart mencionada.

### /product - Fail (Omission, 0.2, 0s)

Nessa rota temos a falha do tipo omissão que para a requisição atual, ela nunca é respondida.

Com base nesse conhecimento, optamos pela combinação de algumas estratégias:
- Retry - retentamos a requisição, pois a omissão pode não ocorrer no request seguinte.
- Fallback - trabalhamos com um plano "B" caso o retry não seja efetivo e continue 
não obtendo resposta.
- Hot Cache - no fallback temos um hot cache (cache inicializado com itens que aumentem o cache hit).

Assim minimizamos os problemas provenientes dessa comunicação, pois mesmo com a omissão e no pior 
dos casos teremos um dado do cache inconsistente. Porém, essa inconsistência não irá
para o banco, pois no **/sell** ele será barrado.

### /exchange - Fail (Crash, 0.1, _ )

Essa rota temos a falha do tipo crash, onde a instância atual do serviço irá parar de ser executada
e não responder o request atual.

Dada a natureza desse tipo de erro e da condição de que existiriam mais de uma instância desse
serviço, consideramos as seguintes estratégias:
- Load balancing - um round-robin nas instâncias para distribuição de carga e diminuir a necessidade
de configuração para novas instâncias.
- Cache - Caso todas as instâncias caiam trabalhamos com a última cotação registrada, pois
normalmente em pouco tempo (horas) o câmbio não poderia variar para gerar percas tão 
significativas em detrimento da concretização da venda.

Desta forma os containers se comportam como uma unidade aumentando a disponibilidade e o cache
é a segurança caso o container chamado não consiga responder.

### /sell - Fail (Error, 0.1, 5s)

Nessa chamada identificamos que o erro simulado ultrapassa o limite fornecido de 1s
como espera para completar o processo de compra. Assim, caso o erro ocorra é inviável qualquer 
outra abordagem.

Nesse caso optamos por usar apenas o **fail fast** para não degradar possíveis downstreams e 
aumentar a responsividade do upstream.

### /bonus - Fail (Time=2s, 0.1, 30s)

Nessa chamada como comentamos em sala, não é um fluxo essencial para a venda. Assim em caso de falha
é possível acontecer um processamento assíncrono dessa contagem de pontos para o cliente sem prejuízo
de negócio.

Aqui isolamos a chamada para o fidelity e caso não fosse possível executar a adição dos pontos
chamando o serviço utilizamos:
- Log - para salvar no ecommerce o bonus a ser creditado em caso de falha

Dessa forma cumprimos o tempo máximo de espera de 1s em caso de problema no fidelity e
controlamos retentivas até de fato conseguir concretizar o bônus.

## Onde E Como Foram Implementadas?

A implementação foi realizada utilizando **Java 23**, **Spring Boot**, **banco em memória (H2)** e **Resilience4j**. A seguir, detalhamos os principais pontos de implementação:

- [/product](ecommerce/src/main/java/com/ecommerce/service/ProductService.java) - 
Aqui foi simulado um Hot Cache com um array, mas em um cenário real poderia usar um redis 
com a mesma ideia de ser inicializado com os produtos mais pesquisados.
- [/exchange](ecommerce/src/main/java/com/ecommerce/service/BuyService.java#L63-L64) - Aqui foi implementada a tentativa de **obter o cache local** do último valor de conversão do câmbio.
- [/sell](ecommerce/src/main/java/com/ecommerce/service/BuyService.java#L70-L71) - Aqui foi utilizado o **Circuit Breaker** para gerenciar falhas na requisição de venda. Caso ocorra um erro, o sistema falha rapidamente, evitando que requisições subsequentes sobrecarreguem o sistema.
- [/bonus](ecommerce/src/main/java/com/ecommerce/jobs/RetryRequestBonusJob.java) - Aqui foi criada uma **tarefa agendada** para processar as requisições de aplicação do bônus que falharam.

## Limitações

Apesar das estratégias de tolerância a falhas implementadas, existem algumas limitações que precisam ser consideradas para garantir um funcionamento adequado do sistema:

- **Inconsistência de Dados no Cache**: O produto pode ficar defasado se a requisição para obter o produto por ID no serviço **store** falhar e o sistema utilizar os dados do cache por um longo período. Isso pode gerar inconsistência entre os dados em cache e o banco de dados real. O mesmo problema ocorre no serviço **exchange**, onde a conversão de câmbio pode ser baseada em dados desatualizados se a requisição à API de câmbio falhar e o cache for utilizado. Também, é necessário implementar uma estratégia de invalidação ou expiração do cache para evitar manter os dados em cache (desatualizados) indefinidamente.

- **Acúmulo de Processamento no Fidelity**: O **serviço de fidelity** pode acumular um grande volume de processamento durante o intervalo de 30 segundos de downtime, caso uma requisição falhe e precise ser reprocessada. Para evitar sobrecarga, seria necessário ajustar a frequência ou lógica de retry, ou considerar dividir o processamento de maneira mais eficiente.

- **Cuidado com o Retry**: A estratégia de **retry** deve ser usada com cautela para evitar sobrecarregar o serviço de destino. Muitos retries seguidos em caso de falhas podem resultar em um estresse significativo para o serviço chamado, especialmente se ele estiver sobrecarregado ou instável.

- **Falhas no Fail Fast do /sell**: O endpoint **/sell** utiliza a abordagem de **fail fast**, o que pode resultar em muitas falhas se o serviço de destino estiver temporariamente indisponível. Embora isso ajude a evitar a sobrecarga, é necessário equilibrar essa estratégia para não afetar a experiência do usuário, implementando uma redundância com mecanismo de retry ou fallback para a instância disponível.

- **Problemas com o Load Balancing**: O **load balancing** está configurado com a estratégia **round-robin**, o que pode fazer com que o tráfego seja direcionado para instâncias que estão inativas ou falharam. Para mitigar isso, seria necessário configurar um **healthcheck** adequado no load balancer para garantir que apenas instâncias saudáveis recebam requisições.

## Abordagens Alternativas

- **Cache Distribuído em vez de Hot Cache Simulado**: 
  - **Solução Atual**: Utilizamos um array para simular um **Hot Cache** de produtos. 
  - **Abordagem Alternativa**: Em vez de usar um cache simples na memória, poderia ser adotado um **cache distribuído**, como o **Redis**, para armazenar os produtos mais acessados. Isso garantiria alta disponibilidade e escalabilidade, já que o Redis pode ser configurado para persistir dados entre reinicializações e ser compartilhado entre múltiplos serviços.

- **Utilização de Filas para Processamento Assíncrono no Fidelity**:
  - **Solução Atual**: Temos uma tarefa agendada para executar aplicações de bônus que falharam, e pode acumular bastante carga durante o downtime do **fidelity**. 
  - **Abordagem Alternativa**: Uma alternativa seria utilizar **filas de mensagens** (como RabbitMQ ou Kafka) para realizar o processamento de forma desacoplada.

- **Configuração de Load Balancer com Healthcheck Avançado**:
  - **Solução Atual**: O balanceador de carga utiliza **round-robin** para distribuir o tráfego.
  - **Abordagem Alternativa**: Uma abordagem mais robusta seria configurar o **load balancer** com um **healthcheck avançado**, que verifica a disponibilidade das instâncias, a performance e a carga das mesmas. Isso garantiria que o tráfego fosse direcionado apenas para instâncias que estivessem ativas e operando dentro dos parâmetros de desempenho esperados.

## Vídeo

[Vídeo no Youtube](https://youtu.be/5FnmNiePDaQ)


## Testes - Desempenho e Disponibilidade

### Descrição Sobre o Sistema

- [Visão Geral da arquitetura](#visão-geral)
- [Tecnologias utilizadas](#onde-e-como-foram-implementadas)

#### Ambiente de Execução

Os testes foram executados localmente em um computador pessoal com as seguintes especificações técnicas:

- **Modelo:** Samsung Book
- **Processador:** Intel Core i5-1135G7 (11ª geração)
- **Memória RAM:** 16 GB
- **Sistema Operacional:** Windows 11 Home

Além disso, todo o projeto foi executado em um ambiente Docker. Todas as dependências e serviços do sistema foram gerenciados utilizando Docker Compose e Docker Desktop.

### Planejamento dos Testes

As métricas no geral foram obtidas utilizando a ferramenta [k6](https://k6.io/) e com base em cenários 
que achamos relevantes com relação a arquitetura existente.

#### Teste de Disponibilidade

- Requisições por segundo: taxa de requisições respondidas (sucesso ou falha) dentro de um segundo.
  - Ex: 10/s
- Duração de requisição (P95%): Indica que 95% das requisições são mais rápidas do que o valor fornecido.
  - 500ms

#### Teste de Disponibilidade

- Tempo médio entre cada falha: média feita entre todas as falhas obtidas no teste.
  - 10/s

#### Metodologia de Testes

O teste simulou requisições POST para a rota ``buy`` do **e-commerce**, representando operações de compra realizadas por usuários no sistema.

##### Stages Configurados

Os testes de carga foram planejados utilizando stages no k6. A configuração dos stages foi realizada da seguinte forma:

- Stage 1 - Ramp-up:

    **Duração:** 30 segundos.

    **Objetivo:** Aumentar gradualmente o número de usuários virtuais (Virtual Users - VUs) de 0 até o valor-alvo configurado (target).

- Stage 2 - Stable:

    **Duração:** 1 minuto.

    **Objetivo:** Manter o número de VUs estável no valor-alvo configurado, simulando um cenário contínuo de carga.

Esses stages foram executados variando o número VUs (target) em: 10 VUs, 100 VUs e 1000 VUs.

##### Simulação do Comportamento do Usuário

**Think Time:**
Durante os testes, foi simulada uma pausa entre as requisições para refletir o comportamento real dos usuários. O tempo de espera (*think time*) foi configurado para variar aleatoriamente entre 1 segundo e 4 segundos.

### Cenários

#### Cenário A: "Happy Path" - tudo funcionamento perfeitamente desde o início

<table>
  <legend>Desempenho: Cenário A - "Happy Path"</legend>
  <tr>
    <th></th>
    <th colspan="4">Tolerância a Falhas Inativa</th>
    <th colspan="4">Tolerância a Falhas Ativa</th>
  </tr>
  <tr>
    <td><strong>VUs</strong></td>
    <td><strong>Req/s</strong></td>
    <td><strong>Tempo Médio</strong></td>
    <td><strong>P(95)</strong></td>
    <td><strong>Taxa de Erro (%)</strong></td>
    <td><strong>Req/s</strong></td>
    <td><strong>Tempo Médio</strong></td>
    <td><strong>P(95)</strong></td>
    <td><strong>Taxa de Erro (%)</strong></td>
  </tr>
  <tr>
    <td>10</td>
    <td>3.11</td>
    <td>182.95ms</td>
    <td>1s</td>
    <td>96</td>
    <td>2.52</td>
    <td>760.59ms</td>
    <td>2.51s</td>
    <td>56</td>
  </tr>
  <tr>
    <td>100</td>
    <td>25.29</td>
    <td>738.58ms</td>
    <td>1.8s</td>
    <td>99</td>
    <td>20.59</td>
    <td>1.41s</td>
    <td>3.37s</td>
    <td>92</td>
  </tr>
  <tr>
    <td>1000</td>
    <td>170.07</td>
    <td>2.21s</td>
    <td>2.84s</td>
    <td>100</td>
    <td>75.02</td>
    <td>7.91s</td>
    <td>10.48s</td>
    <td>96</td>
  </tr>
</table>
<br/>
<table>
  <legend>Disponibilidade: Cenário A - "Happy Path"</legend>
  <tr>
    <th></th>
    <th colspan="2">Tolerância a Falhas Inativa</th>
    <th colspan="2">Tolerância a Falhas Ativa</th>
  </tr>
  <tr>
    <td><strong>VUs</strong></td>
    <td><strong>Tempo Médio entre Falhas</strong></td>
    <td><strong>P(95)</strong></td>
    <td><strong>Tempo Médio entre Falhas</strong></td>
    <td><strong>P(95)</strong></td>
  </tr>
  <tr>
    <td>10</td>
    <td>2.71s</td>
    <td>4.45s</td>
    <td>5.68s</td>
    <td>14.14s</td>
  </tr>
  <tr>
    <td>100</td>
    <td>3.22s</td>
    <td>4.93s</td>
    <td>4.17s</td>
    <td>7.7s</td>
  </tr>
  <tr>
    <td>1000</td>
    <td>4.77s</td>
    <td>6.18s</td>
    <td>11.22s</td>
    <td>13.77s</td>
  </tr>
</table>
<br/>

#### Interpretação Dos Resultados

- A tolerância a falhas ativa resulta em uma redução significativa na taxa de erro em comparação com a tolerância a falhas inativa. No entanto, o desempenho do sistema ainda se deteriora à medida que o número de VUs aumenta, o que é provavelmente causado pelo tempo adicional necessário para os mecanismos de tolerância a falhas, como retries e outras tentativas de recuperação. Esses processos adicionam latência, o que impacta a performance global do sistema sob carga elevada. Um observação importante é que foi feito um _tradeoff_ entre sucesso das requisições e tempo médio de resposta, devido aos mecanismos de tolerância empregados como: retry, circuit breaker, etc.

- Mesmo com a tolerância a falhas ativa, o sistema não consegue manter uma performance estável sob carga elevada. Observamos que os tempos de resposta e as taxas de erro aumentam substancialmente à medida que o número de VUs cresce. Esse comportamento pode ser atribuído à indisponibilidade do serviço Store na rota sell, que apresenta uma probabilidade de falha de 10% e é acionado duas vezes nesse caso de uso, sendo uma API crítica para a conclusão do mesmo. Além disso, o mecanismo de tolerância a falhas adotado (apenas o circuit breaker) não é suficiente para evitar falhas em grande escala, especialmente com o alto volume de requisições paralelas. Isso resulta em uma maior probabilidade de erro, comprometendo a estabilidade e a performance do sistema sob carga.

- o ponto de vista do tempo médio entre falhas, a tolerância a falhas ativa resulta em uma melhoria na disponibilidade em relação à tolerância a falhas inativa, uma vez que proporciona intervalos mais longos entre as falhas. 
  
#### Cenário B: ``Exchange`` com somente uma instância e o serviço ``Fidelity`` offline

<table>
  <legend>Desempenho: Cenário B - "1 instância Exchange e Fidelity offline"</legend>
  <tr>
    <th></th>
    <th colspan="4">Tolerância a Falhas Inativa</th>
    <th colspan="4">Tolerância a Falhas Ativa</th>
  </tr>
  <tr>
    <td><strong>VUs</strong></td>
    <td><strong>Req/s</strong></td>
    <td><strong>Tempo Médio</strong></td>
    <td><strong>P(95)</strong></td>
    <td><strong>Taxa de Erro (%)</strong></td>
    <td><strong>Req/s</strong></td>
    <td><strong>Tempo Médio</strong></td>
    <td><strong>P(95)</strong></td>
    <td><strong>Taxa de Erro (%)</strong></td>
  </tr>
  <tr>
    <td>10</td>
    <td>2.88</td>
    <td>409.42ms</td>
    <td>1s</td>
    <td>100</td>
    <td>2.31</td>
    <td>1.07s</td>
    <td>4.85s</td>
    <td>78</td>
  </tr>
  <tr>
    <td>100</td>
    <td>24.39</td>
    <td>821.56ms</td>
    <td>1.8s</td>
    <td>100</td>
    <td>18.45</td>
    <td>1.85s</td>
    <td>4.15s</td>
    <td>91</td>
  </tr>
  <tr>
    <td>1000</td>
    <td>168.49</td>
    <td>2.22s</td>
    <td>2.84s</td>
    <td>100</td>
    <td>70.80</td>
    <td>8.31s</td>
    <td>10.78s</td>
    <td>97</td>
  </tr>
</table>
<br/>
<table>
  <legend>Disponibilidade: Cenário B - "1 instância Exchange e Fidelity offline"</legend>
  <tr>
    <th></th>
    <th colspan="2">Tolerância a Falhas Inativa</th>
    <th colspan="2">Tolerância a Falhas Ativa</th>
  </tr>
  <tr>
    <td><strong>VUs</strong></td>
    <td><strong>Tempo Médio entre Falhas</strong></td>
    <td><strong>P(95)</strong></td>
    <td><strong>Tempo Médio entre Falhas</strong></td>
    <td><strong>P(95)</strong></td>
  </tr>
  <tr>
    <td>10</td>
    <td>2.84s</td>
    <td>4.52s</td>
    <td>4.3s</td>
    <td>10.43s</td>
  </tr>
  <tr>
    <td>100</td>
    <td>3.33s</td>
    <td>4.87s</td>
    <td>4.78s</td>
    <td>8.7s</td>
  </tr>
  <tr>
    <td>1000</td>
    <td>4.79s</td>
    <td>6.2s</td>
    <td>11.58s</td>
    <td>14.13s</td>
  </tr>
</table>
<br/>

### Considerações Finais

#### Desafios
O maior desafio foi lidar com os gargalos, criando estratégias que combinadas aumentam a chance de cumprir o caso de uso com um bom tempo de resposta e sem falhas. Percebemos que muitas estratégias devem ser combinadas (aplicação e infraestrutura) para alcançar o seu potencial máximo e não a estratégia em si sobrecarregue ou a próprio sistema.

#### Possíveis Melhorias
Tolerância a nível de infraestrutura com Failover, estratégias de restart automático dos containers
Limites para tempo de requisições combinado, contribuindo para fail fast.

#### Conclusão
Estratégias de tolerância a falhas e testes (integração, carga, etc) são estratégias essenciais para entregar software de qualidade que atenda de fato as necessidades do cliente.

Tolerância ajuda para uma melhora geral de disponibilidade e desempenho. Já os testes corroboram ou dão indícios se as estratégias são corretas, se complementam ou são mutualmente exclusivas.

Abordagens de healthcheck, watchdog, etc entre outras são cruciais para manter as estratégias de tolerância a falhas consistentes a nível de aplicação e contribuir para sucesso nas requisições dos serviços providos pelo sistema.
