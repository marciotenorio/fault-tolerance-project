package com.ecommerce.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.ecommerce.client.FidelityClient;
import com.ecommerce.client.StoreClient;

@Configuration
public class ClientsConfig {

    @Value("${application.store.base-uri}")
    private String storeBaseUri;

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

}
