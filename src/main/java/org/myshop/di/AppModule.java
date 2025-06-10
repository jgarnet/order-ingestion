package org.myshop.di;

import dagger.Module;
import dagger.Provides;
import org.myshop.Constants;
import org.myshop.batch.BatchOrders;
import org.myshop.batch.BlockingQueueProvider;
import org.myshop.batch.QueueProvider;
import org.myshop.configuration.ConfigurationProperties;
import org.myshop.http.HttpUtils;
import org.myshop.logger.Logger;
import org.myshop.logger.SystemLogger;
import org.myshop.persistence.database.Database;
import org.myshop.persistence.database.HikariDatabase;
import org.myshop.persistence.database.StubDatabase;
import org.myshop.persistence.repository.MySqlOrdersRepository;
import org.myshop.persistence.repository.OrdersRepository;
import org.myshop.persistence.repository.StubOrdersRepository;

import javax.inject.Named;
import javax.inject.Singleton;

@Module
public class AppModule {
    private final ConfigurationProperties properties;

    public AppModule(ConfigurationProperties properties) {
        this.properties = properties;
    }

    @Provides
    @Singleton
    Logger provideLogger() {
        return new SystemLogger();
    }

    @Provides
    @Singleton
    ConfigurationProperties provideProperties() {
        return this.properties;
    }

    @Provides
    @Singleton
    Database provideDatabase(ConfigurationProperties properties) {
        if (properties.getBoolean("STUB_DB", true)) {
            return new StubDatabase();
        } else {
            return new HikariDatabase(properties);
        }
    }

    @Provides
    @Singleton
    HttpUtils provideHttpUtils() {
        return new HttpUtils();
    }

    @Provides
    @Singleton
    OrdersRepository provideOrdersRepository(ConfigurationProperties properties, Logger logger, Database database) {
        if (properties.getBoolean("STUB_DB", true)) {
            return new StubOrdersRepository(logger);
        } else {
            return new MySqlOrdersRepository(database);
        }
    }

    @Provides
    @Singleton
    @Named(Constants.BATCH_ORDERS_QUEUE)
    QueueProvider<BatchOrders> provideBatchOrdersQueue() {
        return new BlockingQueueProvider<>();
    }

    @Provides
    @Singleton
    @Named(Constants.ERROR_QUEUE)
    QueueProvider<BatchOrders> provideErrorQueue() {
        return new BlockingQueueProvider<>();
    }
}