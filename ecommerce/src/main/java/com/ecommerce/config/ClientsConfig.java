package com.ecommerce.config;

import com.ecommerce.client.ExchangeClient2;
import com.ecommerce.client.FidelityClient;
import com.ecommerce.client.FidelityClient2;
import com.ecommerce.client.StoreClient;
import com.ecommerce.client.StoreClient2;
import org.springframework.beans.factory.annotation.Value;
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
    StoreClient2 storeClient2() {
        return HttpServiceProxyFactory
            .builderFor(
                RestClientAdapter.create(
                    RestClient.builder()
                        .baseUrl(storeBaseUri)
                        .build()
                )
            )
            .build()
            .createClient(StoreClient2.class);
    }

    @Bean
    ExchangeClient2 exchangeClient2() {
        return HttpServiceProxyFactory
            .builderFor(
                RestClientAdapter.create(
                    RestClient.builder()
                        .baseUrl(exchangeBaseUri)
                        .build()
                    )
            )
            .build()
            .createClient(ExchangeClient2.class);
    }

    @Bean
    FidelityClient2 fidelityClient2() {
        return HttpServiceProxyFactory
            .builderFor(
                RestClientAdapter.create(
                    RestClient.builder()
                        .baseUrl(fidelityBaseUri)
                        .build()
                )
            )
            .build()
            .createClient(FidelityClient2.class);
    }

}
