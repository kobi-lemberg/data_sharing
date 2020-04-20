package com.kobi.example.demo.service;

import com.kobi.example.demo.events.DataEvent;
import com.kobi.example.demo.exception.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class DataServiceImpl implements DataService   {

    private final ApplicationEventPublisher publisher;
    private final AtomicReference<JSONObject> data;

    @Autowired
    public DataServiceImpl(ApplicationEventPublisher publisher,
                           AtomicReference<JSONObject> data) {
        this.publisher = publisher;
        this.data = data;
    }

    @Override
    public JSONObject getLatestData() {
        return Optional.ofNullable(data.get())
                .orElseThrow(() -> new DataNotFoundException("Please store data first"));
    }

    @Override
    public synchronized void create(JSONObject data) {
        publisher.publishEvent(new DataEvent(this, data));
        this.data.set(data);
    }

    @Override
    public void update(JSONObject data) {
        this.data.set(data);
    }
}
