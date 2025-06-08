package org.myshop.configuration;

public class SystemConfigurationProperties implements ConfigurationProperties {
    @Override
    public Integer getInteger(String name, Integer defaultValue) {
        String value = this.getProperty(name);
        if (value == null) {
            return defaultValue;
        }
        return Integer.parseInt(value);
    }

    private String getProperty(String name) {
        String value = System.getenv(name);
        if (value == null) {
            value = System.getProperty(name);
        }
        return value;
    }
}
