package com.kobi.example.demo.configuration;

import com.kobi.example.demo.service.DataNotifier;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Configuration
public class ApplicationConfig {

    @Autowired
    private SecurityConfig securityConfig;

    @Bean
    public AtomicReference<JSONObject> atomicReference(){
        return new AtomicReference<>();
    }

    @Bean
    public DataNotifier dataNotifier(@Value("${notifier.uri}") String uri,
                                     RestTemplate restTemplate)
    {
        log.info("Notifier URI is {}", uri);
        return new DataNotifier(restTemplate, uri, securityConfig.getUsername(),
                securityConfig.getPassword());
    }

    @Bean
    @Profile("!mock")
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
