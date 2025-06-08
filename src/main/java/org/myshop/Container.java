package org.myshop;

import org.myshop.configuration.ConfigurationProperties;
import org.myshop.http.HttpUtils;
import org.myshop.logger.Logger;
import org.myshop.processor.BatchOrdersService;

public class Container {
    private BatchOrdersService batchOrdersService;
    private HttpUtils httpUtils;
    private ConfigurationProperties configurationProperties;
    private Logger logger;

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
}
