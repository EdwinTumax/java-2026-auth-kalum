package edu.kalum.auth.core.handler;

import edu.kalum.auth.core.services.RoleService;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class RoleHandler {
    private final RoleService roleService;

    public RoleHandler(RoleService roleService) {
        this.roleService = roleService;
    }

    public void getAll(RoutingContext routingContext){
        roleService.findAll()
                .onSuccess(handlerRoles -> routingContext.response().putHeader("Content-Type","application/json").end(new JsonArray(handlerRoles).encode()))
                .onFailure(error -> routingContext.response().setStatusCode(503).end("Error fetching roles"));
    }

    public void getByName(RoutingContext ctx) {
        String name = ctx.request().getParam("name");
        roleService.findByName(name)
                .onSuccess(role -> {
                    if(role == null) {
                        ctx.response().putHeader("Content-Type","application/json")
                                .setStatusCode(404)
                                .end("No se encontraron registros con el name ".concat(name));
                    } else {
                        ctx.response().putHeader("Content-Type","application/json")
                                .end(JsonObject.mapFrom(role).encodePrettily());
                    }
                }).onFailure(error -> ctx.response().putHeader("Content-Type","application/json")
                        .setStatusCode(503).end(error.getMessage()));
    }
}
