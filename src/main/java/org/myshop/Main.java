package org.myshop;

import org.myshop.configuration.SystemConfigurationProperties;
import org.myshop.http.HttpUtils;
import org.myshop.http.OrdersHttpServer;
import org.myshop.logger.*;
import org.myshop.processor.BatchOrdersService;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Logger logger = new SystemLogger();
        logger.info("Starting order ingestion API...");
        logger.info("Building container dependencies...");
        Container container = new Container();
        container.setConfigurationProperties(new SystemConfigurationProperties());
        container.setHttpUtils(new HttpUtils());
        container.setLogger(logger);
        new BatchOrdersService(container);
        logger.info("Initializing HTTP server...");
        OrdersHttpServer httpServer = new OrdersHttpServer(container);
        Integer port = container.getConfigurationProperties().getInteger("PORT", 8080);
        Integer httpThreads = container.getConfigurationProperties().getInteger("HTTP_THREADS", 10);
        httpServer.start(port, httpThreads);
    }
}