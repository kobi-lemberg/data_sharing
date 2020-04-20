package com.kobi.example.demo.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.mock;

@Configuration
@Primary
public class TestsConfig {

    @Getter
    @Value("${notifier.uri}")
    private String notifierUri;

    @Getter
    private RestTemplate mockedRestTemplate = mock(RestTemplate.class);

    @Bean
    public RestTemplate restTemplate(){
        return mockedRestTemplate;
    }
}
