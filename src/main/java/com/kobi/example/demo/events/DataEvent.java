package com.kobi.example.demo.events;

import lombok.Getter;
import org.json.simple.JSONObject;
import org.springframework.context.ApplicationEvent;

@Getter
public class DataEvent extends ApplicationEvent {
    JSONObject data;

    public DataEvent(Object source, JSONObject data) {
        super(source);
        this.data = data;
    }
}
