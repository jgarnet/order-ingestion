package org.myshop.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.myshop.Constants;
import org.myshop.batch.BatchOrders;
import org.myshop.queue.ErrorStore;
import org.myshop.queue.QueueProvider;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ErrorStoreHandler implements HttpHandler {
    private static final String UUID_REGEX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}";
    private static final Pattern UUID_PATTERN = Pattern.compile(UUID_REGEX);
    private static final Pattern ACTION = Pattern.compile("^\\/eq\\/" + UUID_REGEX + "\\/?$");
    private static final Pattern QUEUE = Pattern.compile("^\\/eq\\/?$");
    private final ErrorStore<BatchOrders, UUID> errorStore;
    private final QueueProvider<BatchOrders> queue;
    private final HttpUtils httpUtils;

    @Inject
    public ErrorStoreHandler(@Named(Constants.ERROR_STORE) ErrorStore<BatchOrders, UUID> errorStore,
                             @Named(Constants.BATCH_ORDERS_QUEUE) QueueProvider<BatchOrders> queue,
                             HttpUtils httpUtils) {
        this.errorStore = errorStore;
        this.queue = queue;
        this.httpUtils = httpUtils;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String url = exchange.getRequestURI().getPath();
            String requestMethod = exchange.getRequestMethod().toUpperCase(Locale.ROOT);
            if (QUEUE.matcher(url).matches()) {
                if ("DELETE".equals(requestMethod)) {
                    this.errorStore.clear();
                    this.httpUtils.writeResponse(exchange, 200, "Queue Cleared");
                    return;
                }
                this.httpUtils.writeResponse(exchange, 405, "Method Not Allowed");
                return;
            } else if (ACTION.matcher(url).matches()) {
                Matcher matcher = UUID_PATTERN.matcher(url);
                if (matcher.find()) {
                    String uuidString = matcher.group();
                    UUID id = UUID.fromString(uuidString);
                    if (!this.errorStore.has(id)) {
                        this.httpUtils.writeResponse(exchange, 404, "Not Found");
                        return;
                    }
                    if ("GET".equals(requestMethod)) {
                        BatchOrders item = this.errorStore.peek(id);
                        this.httpUtils.writeResponse(exchange, 200, item);
                        return;
                    } else if ("PUT".equals(requestMethod)) {
                        BatchOrders item = this.errorStore.take(id);
                        item.requeue();
                        this.queue.put(item);
                        this.httpUtils.writeResponse(exchange, 200, "Item Re-queued");
                        return;
                    } else if ("DELETE".equals(requestMethod)) {
                        this.errorStore.remove(id);
                        this.httpUtils.writeResponse(exchange, 200, "Deleted");
                        return;
                    }
                    this.httpUtils.writeResponse(exchange, 405, "Method Not Allowed");
                    return;
                }
            }
            this.httpUtils.writeResponse(exchange, 404, "Not Found");
        } catch (Exception e) {
            this.httpUtils.writeResponse(exchange, 500, "Internal Server Error");
        }
    }
}