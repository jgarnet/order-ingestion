package org.myshop.persistence;

import javax.sql.DataSource;

public interface Database {
    DataSource getDataSource();
}
