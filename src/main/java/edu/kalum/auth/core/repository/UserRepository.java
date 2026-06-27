package edu.kalum.auth.core.repository;

import edu.kalum.auth.core.services.JwtService;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserRepository {
    private final MySQLPool client;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LoggerFactory.getLogger(UserRepository.class);
    private JwtService jwtService;

    public UserRepository(MySQLPool client, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.client = client;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public Future<List<JsonObject>> findAll() {
        Promise<List<JsonObject>> promise = Promise.promise();
        client.query("select user_id, username, first_name, last_name, email, phone_number, password, application_number from users")
                .execute(handlerResult -> {
                    if(handlerResult.failed()){
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

    public Future<JsonObject> findByUsernameAndPassword(JsonObject body) {
        Promise<JsonObject> promise = Promise.promise();
        String query = """
                select
                    u.user_id,
                	u.username,
                	u.first_name,
                	u.last_name,
                	u.email,
                	u.phone_number,
                	u.application_number,
                	u.password,
                	COALESCE(JSON_ARRAYAGG(JSON_OBJECT('roleId',r.role_id,'name',r.name)), JSON_ARRAY()) as roles
                from users u
                inner join user_roles ur on u.user_id = ur.user_id
                inner join roles r on ur.role_id = r.role_id where u.username = ?
                group by u.user_id
                order by u.username
                """;
        client.preparedQuery(query).execute(Tuple.of(body.getString("username")), handlerResult -> {
            if(handlerResult.failed() || handlerResult.result().size() == 0) {
                promise.fail("Credenciales incorrectas, valide su username y password");
                return;
            }
            Row row = handlerResult.result().iterator().next();
            String passwordHash = row.getString("password");
            if(!passwordEncoder.matches(body.getString("password"), passwordHash)) {
                promise.fail("Credential failed");
                return;
            }
            JsonObject user = new JsonObject();
            user.put("userId",row.getString("user_id"));
            user.put("username", row.getString("username"));
            user.put("firstName", row.getString("first_name"));
            user.put("lastName", row.getString("last_name"));
            user.put("email", row.getString("email"));
            user.put("phoneNumber", row.getString("phone_number"));
            user.put("applicationNumber", row.getString("application_number"));
            Object roles = row.getValue("roles");
            String rolesAsString = ((JsonArray)roles).stream().map( r -> ((JsonObject)r).getString("name"))
                        .collect(Collectors.joining(","));
            user.put("roles", rolesAsString);
            promise.complete(jwtService.generateToken(user));
        });
        return  promise.future();
    }

    public Future<Void> addRoleToUser(String userId, String roleId) {
        Promise<Void> promise = Promise.promise();
        client.preparedQuery("insert into user_roles (user_id,role_id) values (?,?)")
                .execute(Tuple.of(userId,roleId), handlerResult -> {
                    if(handlerResult.failed()) {
                        promise.fail(handlerResult.cause());
                        return;
                    }
                    promise.complete();
                });
        return promise.future();
    }

    public Future<JsonObject> createUserWithToken(JsonObject body) {
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
                passwordEncoder.encode(body.getString("password")), "0"), handlerResult -> {
            if(handlerResult.failed()) {
                promise.fail(handlerResult.cause());
                return;
            }
            String roleDefault = "ROLE_USER";
            List<String> roles =  new ArrayList<String>();
            roles.add(roleDefault);
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
                                    body.put("applicationNumber","0");
                                    body.put("roles",roleDefault);
                                    body.remove("password");
                                    promise.complete(jwtService.generateToken(body));
                                });
                    } else {
                        promise.complete();
                    }
                }
            });
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
                passwordEncoder.encode(body.getString("password")), "0"), handlerResult -> {
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
