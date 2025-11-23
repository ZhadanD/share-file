package share.file.repositories;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import share.file.utils.PropertyUtil;

public abstract class AbstractRepository<T> {
    protected Connection connection;

    protected String DB_URL;
    protected String DB_USER;
    protected String DB_PASSWORD;

    public AbstractRepository() {
        PropertyUtil propertyUtil = new PropertyUtil();

        Properties properties = propertyUtil.loadProperties();

        DB_URL = properties.getProperty("db.url");
        DB_USER = properties.getProperty("db.username");
        DB_PASSWORD = properties.getProperty("db.password");

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (Exception e) {
            throw new RuntimeException("Failed connection", e);
        }
    }

    protected abstract void createTableIfNotExists();
    
    protected abstract T save(T entity);
}
