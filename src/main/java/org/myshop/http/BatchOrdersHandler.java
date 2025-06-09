package org.myshop.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.myshop.order.Order;
import org.myshop.batch.BatchOrdersService;

import javax.inject.Inject;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;

public class BatchOrdersHandler implements HttpHandler {
    private final BatchOrdersService batchOrdersService;
    private final HttpUtils httpUtils;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    private final Type ordersListType = new TypeToken<List<Order>>(){}.getType();

    @Inject
    public BatchOrdersHandler(BatchOrdersService batchOrdersService, HttpUtils httpUtils) {
        this.batchOrdersService = batchOrdersService;
        this.httpUtils = httpUtils;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String response = "Accepted";
        int statusCode = 202;

        try {
            String requestBody = this.httpUtils.getRequestBody(exchange);
            List<Order> orders = this.gson.fromJson(requestBody, this.ordersListType);

            List<String> errors = this.batchOrdersService.validate(orders);
            if (errors.isEmpty()) {
                this.batchOrdersService.ingestOrders(orders);
            } else {
                response = this.gson.toJson(errors);
                statusCode = 400;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = "Internal Server Error";
            statusCode = 500;
        }

        exchange.sendResponseHeaders(statusCode, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
