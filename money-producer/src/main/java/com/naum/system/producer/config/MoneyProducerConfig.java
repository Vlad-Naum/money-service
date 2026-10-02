package com.naum.system.producer.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(MoneyProducerProperties.class)
public class MoneyProducerConfig {

    @Bean
    public RestClient moneyServiceClient(RestClient.Builder builder, MoneyProducerProperties props) {;
        return builder.baseUrl(props.email().url()).build();
    }
}
