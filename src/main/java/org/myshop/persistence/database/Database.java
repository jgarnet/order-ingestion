package org.myshop.persistence.database;

import javax.sql.DataSource;

public interface Database {
    DataSource getDataSource();
}
