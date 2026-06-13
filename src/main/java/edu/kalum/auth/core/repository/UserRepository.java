package edu.kalum.auth.core.repository;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private final MySQLPool client;

    public UserRepository(MySQLPool client) {
        this.client = client;
    }

    public Future<List<JsonObject>> findAll() {
        System.out.println("Repository user");
        Promise<List<JsonObject>> promise = Promise.promise();
        client.query("select user_id, username, first_name, last_name, email, phone_number, password, application_number from users")
                .execute(handlerResult -> {
                    if(handlerResult.failed()){
                        System.out.println("Error query");
                        promise.fail(handlerResult.cause());
                        return;
                    }
                    System.out.println(handlerResult.result().size());
                    RowSet<Row> rows = handlerResult.result();
                    List<JsonObject> users = new ArrayList<>();
                    rows.forEach(element -> {
                        JsonObject user = new JsonObject();
                        user.put("userId",element.getString("user_id"));
                        user.put("username", element.getString("username"));
                        user.put("firstName", element.getString("first_name"));
                        user.put("last_name", element.getString("last_name"));
                        user.put("email", element.getString("email"));
                        user.put("phoneNumber", element.getString("phone_number"));
                        user.put("password", element.getString("password"));
                        user.put("applicationNumber","application_number");
                        users.add(user);
                    });
                    promise.complete(users);
                });
        return promise.future();
    }

}
