package com.kobi.example.demo.service;

import com.kobi.example.demo.exception.DataNotFoundException;
import org.json.simple.JSONObject;

public interface DataService {
    /**
     * @return the last key-value json object that were sent.
     * @throws DataNotFoundException in case that there is no data
     */
    JSONObject getLatestData();

    /**
     * Store new data in the system
     * @param data json object
     */
    void create(JSONObject data);

    /**
     * Update new data in the system
     * @param data json object
     */
    void update(JSONObject data);

}
