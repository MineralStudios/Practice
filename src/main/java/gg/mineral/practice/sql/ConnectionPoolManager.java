package gg.mineral.practice.sql;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ConnectionPoolManager {

    private HikariDataSource dataSource;

    private String hostname, port, database, username, password;

    private int minimumIdle, poolSize;
    private long connectionTimeout;
    private String testQuery;

    public ConnectionPoolManager(String host, String port, String database, String username, String password) {
        initialize(host, port, database, username, password);
        setupPool();
    }

    private void initialize(String host, String port, String database, String username, String password) {
        this.hostname = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        minimumIdle = 5;
        poolSize = 15;
        connectionTimeout = 15000; // millis
        testQuery = "show tables";
    }

    private void setupPool() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + hostname + ":" + port + "/" + database);
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setUsername(username);
        config.setPassword(password);
        config.setMinimumIdle(minimumIdle);
        config.setMaximumPoolSize(poolSize);
        config.setConnectionTimeout(connectionTimeout);
        config.setConnectionTestQuery(testQuery);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        dataSource = new HikariDataSource(config);
    }

    public void close(AutoCloseable... toClose) {
        for (AutoCloseable a : toClose) {
            if (a != null) {
                try {
                    a.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void disconnect() {
        if (dataSource != null && !dataSource.isClosed()) {
            try {
                dataSource.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}