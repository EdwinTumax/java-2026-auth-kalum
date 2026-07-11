package edu.kalum.auth.core.config;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;

public class MySQLPoolConfig {
    public static MySQLPool createPool(Vertx vertx, JsonObject config) {
        MySQLConnectOptions connectOptions = new MySQLConnectOptions()
                .setHost(config.getString("host"))
                .setPort(config.getInteger("port"))
                .setDatabase(config.getString("dataBase"))
                .setUser(config.getString("user"))
                .setPassword(config.getString("password"));
        PoolOptions poolOptions = new PoolOptions().setMaxSize(config.getInteger("poolMaxSize"));
        return MySQLPool.pool(vertx,connectOptions,poolOptions);
    }
}
