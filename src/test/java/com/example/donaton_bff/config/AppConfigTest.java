package com.example.donaton_bff.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AppConfigTest {

    @Test
    void deberiaCrearBeansDeRestClient() {
        AppConfig config = new AppConfig();

        RestClient.Builder builder = config.restClientBuilder();
        RestClient.Builder loadBalancedBuilder = config.loadBalancedRestClientBuilder();
        RestClient restClient = config.restClient(loadBalancedBuilder);

        assertNotNull(builder);
        assertNotNull(loadBalancedBuilder);
        assertNotNull(restClient);
    }
}
