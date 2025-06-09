package org.myshop;

import org.myshop.configuration.ConfigurationProperties;
import org.myshop.configuration.SystemConfigurationProperties;
import org.myshop.di.AppComponent;
import org.myshop.di.AppModule;
import org.myshop.di.DaggerAppComponent;
import org.myshop.http.OrdersHttpServer;
import org.myshop.logger.Logger;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        // initialize DI container
        ConfigurationProperties properties = new SystemConfigurationProperties();
        AppComponent appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(properties))
                .build();
        Logger logger = appComponent.logger();
        logger.info("Starting order ingestion API...");
        // start http server
        logger.info("Initializing HTTP server...");
        OrdersHttpServer httpServer = appComponent.ordersHttpServer();
        Integer port = properties.getInteger("PORT", 8080);
        Integer httpThreads = properties.getInteger("HTTP_THREADS", 10);
        httpServer.start(port, httpThreads);
    }
}