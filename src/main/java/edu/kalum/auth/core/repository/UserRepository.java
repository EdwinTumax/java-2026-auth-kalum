package edu.kalum.auth.core.repository;

import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserRepository {
    private final MySQLPool client;

    public UserRepository(MySQLPool client) {
        this.client = client;
    }

    public Future<List<JsonObject>> findAll() {
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
                        user.put("applicationNumber",element.getString("application_number"));
                        users.add(user);
                    });
                    promise.complete(users);
                });
        return promise.future();
    }

    public Future<String> save(JsonObject body) {
        Promise promise = Promise.promise();
        String userId = UUID.randomUUID().toString();
        client.preparedQuery("""
              INSERT into users (user_id, username, first_name, last_name, email, phone_number, password, application_number) values (?,?,?,?,?,?,?,?)
        """).execute(Tuple.of(
                userId,
                body.getString("username"),
                body.getString("firstName"),
                body.getString("lastName"),
                body.getString("email"),
                body.getString("phoneNumber"),
                body.getString("password"), "0"), handlerResult -> {
            if(handlerResult.failed()) {
                promise.fail(handlerResult.cause());
                return;
            }
            List<String> roles =  new ArrayList<String>();
            roles.add("ROLE_USER");
            getRoleByName(roles).onComplete(responseRoles -> {
                if(responseRoles.succeeded()) {
                    if(responseRoles.result() != null && !responseRoles.result().isEmpty()) {
                        List<Tuple> batch = responseRoles.result().stream().map(roleId -> Tuple.of(userId,roleId))
                                .collect(Collectors.toList());
                        client.preparedQuery("insert into user_roles (user_id,role_id) values (?,?)")
                                .executeBatch(batch)
                                .onComplete(batchResponse -> {
                                    if(batchResponse.failed()) {
                                        promise.fail(batchResponse.cause());
                                        return;
                                    }
                                    promise.complete(userId);
                                });
                    } else {
                        promise.complete();
                    }
                }
            });
        });
        return promise.future();
    }

    public Future<Void> udpate(String userId, JsonObject body) {
        Promise<Void> promise = Promise.promise();
        client.preparedQuery("update users set username = ?, first_name = ?, last_name = ?, email = ?, phone_number = ?, application_number = ? where user_id = ?")
                .execute(Tuple.of(
                        body.getString("username")
                        ,body.getString("firstName")
                        ,body.getString("lastName")
                        ,body.getString("email")
                        ,body.getString("phoneNumber")
                        ,body.getString("applicationNumber")
                        ,userId), handlerResult -> {
                    if(handlerResult.failed()) {
                        promise.fail(handlerResult.cause());
                        return;
                    }
                    promise.complete();
                });
        return promise.future();
    }

    public Future<Void> delete(String userId) {
        Promise<Void> promise = Promise.promise();
        client.preparedQuery("delete from user_roles where user_id = ?")
                .execute(Tuple.of(userId), handlerResult -> {
                    if(handlerResult.failed()) {
                        promise.fail(handlerResult.cause());
                        return;
                    }
                    client.preparedQuery("delete from users where user_id = ?")
                            .execute(Tuple.of(userId), handlerRemoveResult -> {
                                if(handlerRemoveResult.failed()) {
                                    promise.fail(handlerRemoveResult.cause());
                                    return;
                                }
                                promise.complete();
                            });
                });
        return promise.future();
    }

    private Future<List<String>> getRoleByName(List<String> names) {
        List<Future> futures = names.stream().map(name -> {
            Promise<String> promise = Promise.promise();
            client.preparedQuery("select role_id from roles where name = ?").execute(Tuple.of(name), handlerResult -> {
                if(handlerResult.failed()) {
                    promise.fail(handlerResult.cause());
                    return;
                }
                RowSet<Row> rows = handlerResult.result();
                if(!rows.iterator().hasNext()) {
                    promise.complete(null);
                    return;
                }
                Row row = rows.iterator().next();
                promise.complete(row.getString("role_id"));
            });
            return promise.future();
        }).collect(Collectors.toList());
        Promise<List<String>> promiseResult = Promise.promise();
        CompositeFuture.all(futures).onComplete(promiseCompleted -> {
            if(promiseCompleted.failed()) {
                promiseResult.fail(promiseCompleted.cause());
                return;
            }
            List<String> rolesId = promiseCompleted
                    .result()
                    .list()
                    .stream()
                    .filter(Objects::nonNull)
                    .map(String.class::cast).collect(Collectors.toList());
            promiseResult.complete(rolesId);
        });
        return promiseResult.future();
    }

}
