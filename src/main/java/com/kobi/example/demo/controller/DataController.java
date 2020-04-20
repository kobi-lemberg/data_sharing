package com.kobi.example.demo.controller;

import com.kobi.example.demo.service.DataService;
import io.swagger.annotations.*;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(value = "Data Store Controller", description = "Controller to retrieve and update in memory saved data.",
        authorizations = {@Authorization(value="basicAuth")}
)
@RestController
public class DataController {
    private final static String PATH = "/v1/data";
    private final static String TAG = "Data Store";

    private DataService dataService;

    @Autowired
    public DataController(DataService dataService) {
        this.dataService = dataService;
    }

    @ApiOperation(value = "Get stored data", tags = TAG, response = JSONObject.class)
    @ResponseBody
    @GetMapping(value = PATH)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Data has been returned"),
        @ApiResponse(code = 404, message = "Data not found"),
        @ApiResponse(code = 500, message = "Error occurred while fetching the data")
    })
    public ResponseEntity<JSONObject> getData() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(dataService.getLatestData());
    }

    @ApiOperation(value = "Create new data", tags = TAG)
    @PostMapping(value = PATH)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Data has been created"),
            @ApiResponse(code = 412, message = "Notifier field to notify"),
            @ApiResponse(code = 500, message = "Error occurred while creating the data")
    })
    public ResponseEntity create(@RequestBody JSONObject jsn) {
        dataService.create(jsn);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ApiOperation(value = "Update data", tags = TAG)
    @PutMapping(value = "/v1/update")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Data has been updated"),
            @ApiResponse(code = 500, message = "Error occurred while updating the data")
    })
    public ResponseEntity update(@RequestBody JSONObject jsn) {
        dataService.update(jsn );
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
