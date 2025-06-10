package org.myshop.http;

import com.sun.net.httpserver.HttpServer;
import org.myshop.logger.Logger;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Singleton
public class OrdersHttpServer {
    private HttpServer server;
    private final Logger logger;
    private final Provider<BatchOrdersHandler> handlerProvider;
    private final Provider<ErrorStoreHandler> eqHandlerProvider;

    @Inject
    public OrdersHttpServer(Logger logger, Provider<BatchOrdersHandler> handlerProvider, Provider<ErrorStoreHandler> eqHandlerProvider) {
        this.logger = logger;
        this.handlerProvider = handlerProvider;
        this.eqHandlerProvider = eqHandlerProvider;
    }

    public void start(int port, int threadPoolSize) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);

        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize, new ThreadFactory() {
            private int count = 1;

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("HTTP-" + count++);
                return t;
            }
        });
        this.server.setExecutor(executor);

        this.server.createContext("/batch-orders", this.handlerProvider.get());
        this.server.createContext("/eq", this.eqHandlerProvider.get());

        this.server.start();
        this.logger.info("Server started on port " + port + " with " + threadPoolSize + " threads");
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }
}
