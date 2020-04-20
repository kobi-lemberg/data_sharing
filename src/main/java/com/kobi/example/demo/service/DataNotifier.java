package com.kobi.example.demo.service;

import com.kobi.example.demo.events.DataEvent;
import com.kobi.example.demo.exception.DataNotifierException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static com.kobi.example.demo.utils.RestUtils.createBasicAuth;

public class DataNotifier implements ApplicationListener<DataEvent> {

    private final RestTemplate restTemplate;
    private final String url;
    private final String username;
    private final String password;

    @Autowired
    public DataNotifier(RestTemplate restTemplate, String url, String username,
                        String password) {
        this.restTemplate = restTemplate;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public void onApplicationEvent(DataEvent event) {
        HttpEntity<JSONObject> entity = new HttpEntity<>(event.getData(), createHeaders());
        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
        validateResponse(result);
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, createBasicAuth(username, password));
        return headers;
    }

    private void validateResponse(ResponseEntity<String> result) {
        if ( !result.getStatusCode().is2xxSuccessful() ){
            throw new DataNotifierException(String.format("Could not handle event due to bad response: {%s}", result));
        }
    }
}
