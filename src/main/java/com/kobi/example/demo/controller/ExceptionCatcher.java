package com.kobi.example.demo.controller;

import com.kobi.example.demo.exception.DataNotFoundException;
import com.kobi.example.demo.exception.DataNotifierException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionCatcher {

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity handleException(DataNotFoundException e) {
        log.error("Data not found", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Data not found");
    }

    @ExceptionHandler(DataNotifierException.class)
    public ResponseEntity handleException(DataNotifierException e) {
        log.error("Exception", e);
        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("Failed to notify");
    }

}
