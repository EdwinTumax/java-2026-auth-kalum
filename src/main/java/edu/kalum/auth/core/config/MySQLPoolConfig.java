package edu.kalum.auth.core.config;

import io.vertx.core.Vertx;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;

public class MySQLPoolConfig {
    public static MySQLPool createPool(Vertx vertx) {
        MySQLConnectOptions connectOptions = new MySQLConnectOptions()
                .setHost("localhost")
                .setPort(3306)
                .setDatabase("kalum_dev_2026_auth")
                .setUser("kalum_user_dev")
                .setPassword("K@alum.dev");
        PoolOptions poolOptions = new PoolOptions().setMaxSize(10);
        return MySQLPool.pool(vertx,connectOptions,poolOptions);
    }
}
