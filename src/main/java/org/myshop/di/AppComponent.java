package org.myshop.di;

import dagger.Component;
import org.myshop.configuration.ConfigurationProperties;
import org.myshop.http.HttpUtils;
import org.myshop.http.OrdersHttpServer;
import org.myshop.logger.Logger;
import org.myshop.persistence.database.Database;
import org.myshop.persistence.repository.OrdersRepository;
import org.myshop.batch.Queues;

import javax.inject.Singleton;

@Component(modules = AppModule.class)
@Singleton
public interface AppComponent {
    HttpUtils httpUtils();
    ConfigurationProperties properties();
    Logger logger();
    Database database();
    OrdersRepository ordersRepository();
    OrdersHttpServer ordersHttpServer();
    Queues queues();
}