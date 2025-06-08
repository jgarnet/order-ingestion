package org.myshop;

import org.myshop.configuration.ConfigurationProperties;
import org.myshop.http.HttpUtils;
import org.myshop.processor.BatchOrdersService;

public class Container {
    private BatchOrdersService batchOrdersService;
    private HttpUtils httpUtils;
    private ConfigurationProperties configurationProperties;

    public BatchOrdersService getBatchOrderService() {
        return batchOrdersService;
    }

    public HttpUtils getHttpUtils() {
        return httpUtils;
    }

    public ConfigurationProperties getConfigurationProperties() {
        return configurationProperties;
    }

    public Container setBatchOrderService(BatchOrdersService batchOrdersService) {
        this.batchOrdersService = batchOrdersService;
        return this;
    }

    public Container setHttpUtils(HttpUtils httpUtils) {
        this.httpUtils = httpUtils;
        return this;
    }

    public Container setConfigurationProperties(ConfigurationProperties configurationProperties) {
        this.configurationProperties = configurationProperties;
        return this;
    }
}
