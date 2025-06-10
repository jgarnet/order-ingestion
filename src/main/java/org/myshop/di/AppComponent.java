package org.myshop.di;

import dagger.Component;
import org.myshop.http.OrdersHttpServer;
import org.myshop.logger.Logger;

import javax.inject.Singleton;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    Logger logger();
    OrdersHttpServer ordersHttpServer();
}