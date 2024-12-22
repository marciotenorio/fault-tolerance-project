# Projeto - Tolerância a Falhas

Autores:
Márcio Tenório Júnior\
Maria Eduarda Elói Pereira

## Como Executar?
//TODO

## Estratégias Utilizadas

Comentários sobre as estratégias de tolerância a falhas utilizadas no serviço **ecommerce** chamando o 
exchange, store e fidelity para concluir o processo de compra.

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

Utilizamos Java 23, Spring Boot e Resilience4j.

- [/product](ecommerce/src/main/java/com/ecommerce/service/ProductService.java) - 
Aqui foi simulado um Hot Cache com um array, mas em um cenário real poderia usar um redis 
com a mesma ideia de ser inicializado com os produtos mais pesquisados.
- [/exchange](ecommerce/src/main/java/com/ecommerce/service/BuyService.java#51) 
- [/sell](ecommerce/src/main/java/com/ecommerce/service/BuyService.java#60)
- [/bonus](ecommerce/src/main/java/com/ecommerce/service/BuyService.java#63)

## Limitações
//TODO

## Abordagens Alternativas
//TODO

## Vídeo
//TODO