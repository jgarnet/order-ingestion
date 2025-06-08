package org.myshop;

import org.myshop.configuration.ConfigurationProperties;
import org.myshop.http.HttpUtils;
import org.myshop.logger.Logger;
import org.myshop.persistence.database.Database;
import org.myshop.persistence.repository.OrdersRepository;
import org.myshop.processor.BatchOrdersService;

public class Container {
    private BatchOrdersService batchOrdersService;
    private HttpUtils httpUtils;
    private ConfigurationProperties configurationProperties;
    private Logger logger;
    private Database database;
    private OrdersRepository ordersRepository;

    public BatchOrdersService getBatchOrdersService() {
        return batchOrdersService;
    }

    public void setBatchOrdersService(BatchOrdersService batchOrdersService) {
        this.batchOrdersService = batchOrdersService;
    }

    public HttpUtils getHttpUtils() {
        return httpUtils;
    }

    public void setHttpUtils(HttpUtils httpUtils) {
        this.httpUtils = httpUtils;
    }

    public ConfigurationProperties getConfigurationProperties() {
        return configurationProperties;
    }

    public void setConfigurationProperties(ConfigurationProperties configurationProperties) {
        this.configurationProperties = configurationProperties;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public OrdersRepository getOrdersRepository() {
        return ordersRepository;
    }

    public void setOrdersRepository(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }
}
