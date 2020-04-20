package com.kobi.example.demo.utils;

import org.junit.Test;

import static com.kobi.example.demo.utils.RestUtils.createBasicAuth;
import static org.junit.Assert.*;

public class RestUtilsTest {

    private static final String CREDENTIALS = "CREDENTIALS";

    @Test
    public void createBasicAuth_CredentialsNotBlank_tokenGenerated() {
        assertEquals("Basic Q1JFREVOVElBTFM6Q1JFREVOVElBTFM=",
                createBasicAuth(CREDENTIALS, CREDENTIALS));
    }

    @Test(expected = NullPointerException.class)
    public void createBasicAuth_nullUserName_ExceptionIsThrown() {
        createBasicAuth(null, CREDENTIALS);
    }

    @Test(expected = NullPointerException.class)
    public void createBasicAuth_nullPassword_ExceptionIsThrown() {
        createBasicAuth(CREDENTIALS, null);
    }
}