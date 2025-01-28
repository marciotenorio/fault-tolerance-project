package com.ecommerce.config;

import com.ecommerce.client.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.Duration;

@Configuration
public class ClientsConfig {

    @Value("${application.store.base-uri}")
    private String storeBaseUri;

    @Value("${application.exchange.base-uri}")
    private String exchangeBaseUri;

    @Value("${application.fidelity.base-uri}")
    private String fidelityBaseUri;

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(Duration.ofSeconds(1));
        clientHttpRequestFactory.setReadTimeout(Duration.ofSeconds(1));
        clientHttpRequestFactory.setConnectionRequestTimeout(Duration.ofSeconds(1));
        return clientHttpRequestFactory;
    }

    RestClient restClient(String baseUrl) {
        return RestClient.builder()
                .requestFactory(getClientHttpRequestFactory())
                .baseUrl(baseUrl)
                .build();
    }

    @LoadBalanced
    @Bean
    RestClient.Builder restClientLbBuilder() {
        return RestClient.builder();
    }

    @Bean
    StoreClient storeClient() {
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient(storeBaseUri)))
                .build()
                .createClient(StoreClient.class);
    }

    @Bean
    FidelityClient fidelityClient() {
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient(fidelityBaseUri)))
                .build()
                .createClient(FidelityClient.class);
    }

    @Bean
    ExchangeClient exchangeClient() {
        RestClient restClient = restClientLbBuilder()
                .requestFactory(getClientHttpRequestFactory())
                .baseUrl("http://exchange")
                .build();

        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build()
                .createClient(ExchangeClient.class);
    }

    @Bean
    StoreClient2 storeClient2() {
        return HttpServiceProxyFactory
            .builderFor(RestClientAdapter.create(restClient(storeBaseUri)))
            .build()
            .createClient(StoreClient2.class);
    }

    @Bean
    ExchangeClient2 exchangeClient2() {
        return HttpServiceProxyFactory
            .builderFor(RestClientAdapter.create(restClient(exchangeBaseUri)))
            .build()
            .createClient(ExchangeClient2.class);
    }

    @Bean
    FidelityClient2 fidelityClient2() {
        return HttpServiceProxyFactory
            .builderFor(RestClientAdapter.create(restClient(fidelityBaseUri)))
            .build()
            .createClient(FidelityClient2.class);
    }

}
