package org.myshop.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class HttpUtils {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public <T> String toJson(T entity) {
        return this.gson.toJson(entity);
    }

    public <T> T fromJson(String json, Type type) {
        return this.gson.fromJson(json, type);
    }

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

    public <T> void writeResponse(HttpExchange exchange, int statusCode, T entity) throws IOException {
        String response = entity instanceof String ? (String) entity : this.toJson(entity);
        exchange.sendResponseHeaders(statusCode, response != null ? response.length() : -1);
        if (response != null) {
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}
