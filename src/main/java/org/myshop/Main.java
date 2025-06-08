package org.myshop;

import org.myshop.configuration.SystemConfigurationProperties;
import org.myshop.http.HttpUtils;
import org.myshop.http.OrdersHttpServer;
import org.myshop.processor.BatchOrdersService;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Starting order ingestion API...");
        System.out.println("Building container dependencies...");
        Container container = new Container();
        container.setConfigurationProperties(new SystemConfigurationProperties());
        container.setHttpUtils(new HttpUtils());
        new BatchOrdersService(container);
        System.out.println("Initializing HTTP server...");
        OrdersHttpServer httpServer = new OrdersHttpServer(container);
        Integer port = container.getConfigurationProperties().getInteger("PORT", 8080);
        Integer httpThreads = container.getConfigurationProperties().getInteger("HTTP_THREADS", 10);
        httpServer.start(port, httpThreads);
    }
}