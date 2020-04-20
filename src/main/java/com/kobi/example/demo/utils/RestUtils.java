package com.kobi.example.demo.utils;

import lombok.NonNull;
import java.nio.charset.Charset;

import static org.apache.tomcat.util.codec.binary.Base64.encodeBase64;

public class RestUtils {

    public static String createBasicAuth(@NonNull String username,@NonNull String password) {
        return String.format("Basic %s",
                new String(
                        encodeBase64(String.format("%s:%s", username, password)
                        .getBytes(Charset.forName("US-ASCII")))
                )
        );
    }
}
