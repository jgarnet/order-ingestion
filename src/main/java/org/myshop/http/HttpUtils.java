package org.myshop.http;

import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class HttpUtils {
    public String getRequestBody(HttpExchange exchange) throws IOException {
        StringBuilder json = new StringBuilder();
        try (InputStream body = exchange.getRequestBody()) {
            InputStreamReader reader = new InputStreamReader(body, StandardCharsets.UTF_8);
            BufferedReader buffered = new BufferedReader(reader);

            String line;
            while ((line = buffered.readLine()) != null) {
                json.append(line);
            }
        }
        return json.toString();
    }
}
