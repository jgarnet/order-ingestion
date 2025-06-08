package org.myshop;

import org.myshop.configuration.SystemConfigurationProperties;
import org.myshop.http.HttpUtils;
import org.myshop.http.OrdersHttpServer;
import org.myshop.logger.Logger;
import org.myshop.logger.SystemLogger;
import org.myshop.persistence.database.HikariDatabase;
import org.myshop.persistence.database.StubDatabase;
import org.myshop.persistence.repository.MySqlOrdersRepository;
import org.myshop.persistence.repository.StubOrdersRepository;
import org.myshop.processor.BatchOrdersService;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        // initialize container
        Logger logger = new SystemLogger();
        logger.info("Starting order ingestion API...");
        logger.info("Building container dependencies...");
        Container container = new Container();
        container.setLogger(logger);
        container.setConfigurationProperties(new SystemConfigurationProperties());
        container.setHttpUtils(new HttpUtils());
        // initialize objects that self-register onto container due to co-dependence
        // initialize database layer first to ensure processing layer can access
        if (container.getConfigurationProperties().getBoolean("STUB_DB", true)) {
            new StubDatabase(container);
            new StubOrdersRepository(container);
        } else {
            new HikariDatabase(container);
            new MySqlOrdersRepository(container);
        }
        new BatchOrdersService(container);
        // start http server
        logger.info("Initializing HTTP server...");
        OrdersHttpServer httpServer = new OrdersHttpServer(container);
        Integer port = container.getConfigurationProperties().getInteger("PORT", 8080);
        Integer httpThreads = container.getConfigurationProperties().getInteger("HTTP_THREADS", 10);
        httpServer.start(port, httpThreads);
    }
}