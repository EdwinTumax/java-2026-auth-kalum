package edu.kalum.auth.core.repository;

import edu.kalum.auth.core.dtos.RoleCreateDTO;
import edu.kalum.auth.core.model.Role;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RoleRepository {
    private final MySQLPool client;

    public RoleRepository(MySQLPool client) {
        this.client = client;
    }

    public Future<List<JsonObject>> findAll() {
        Promise<List<JsonObject>> promise = Promise.promise();
        client.query("select role_id, name from roles order by name")
                .execute(handlerResult -> {
                    if(handlerResult.failed()) {
                        promise.fail(handlerResult.cause());
                        return;
                    }
                    RowSet<Row> rows = handlerResult.result();
                    List<JsonObject> roles = new ArrayList<>();
                    rows.forEach(element -> {
                        JsonObject role = new JsonObject();
                        role.put("roleId",element.getString("role_id"));
                        role.put("name",element.getString("name"));
                        roles.add(role);
                    });
                    promise.complete(roles);
                });
        return promise.future();
    }

    public Future<Role> findByName(String name) {
        Promise<Role> promise = Promise.promise();
        client.preparedQuery("select role_id, name from roles where name = ?")
                .execute(Tuple.of(name), asyncResult -> {
                    if(asyncResult.failed()) {
                        promise.fail(asyncResult.cause());
                        return;
                    }
                    RowSet<Row> rows = asyncResult.result();
                    if(!rows.iterator().hasNext()) {
                        promise.complete(null);
                        return;
                    }
                    Row row = rows.iterator().next();
                    Role role = new Role();
                    role.setRoleId(row.getString("role_id"));
                    role.setName(row.getString("name"));
                    promise.complete(role);
                });
        return promise.future();
    }

    public Future<Role> findyId(String id) {
        Promise<Role> promise = Promise.promise();
        client.preparedQuery("select role_id, name from roles where role_id = ?")
                .execute(Tuple.of(id), asyncResult -> {
                    if(asyncResult.failed()) {
                        promise.fail(asyncResult.cause());
                        return;
                    }
                    RowSet<Row> rows = asyncResult.result();
                    if(!rows.iterator().hasNext()) {
                        promise.complete(null);
                        return;
                    }
                    Row row = rows.iterator().next();
                    Role role = new Role();
                    role.setRoleId(row.getString("role_id"));
                    role.setName(row.getString("name"));
                    promise.complete(role);
                });
        return promise.future();
    }

    public Future<String> save(RoleCreateDTO role) {
        Promise<String> promise = Promise.promise();
        String id = UUID.randomUUID().toString();
        client.preparedQuery("insert into roles (role_id, name) values (?,?)")
                .execute(Tuple.of(id, role.getName()), asyncResult -> {
                    if(asyncResult.failed()) {
                        promise.fail(asyncResult.cause());
                        return;
                    }
                    promise.complete(id);
                });
        return promise.future();
    }

}
