package edu.kalum.auth.core.handler;

import edu.kalum.auth.core.services.RoleService;
import io.vertx.core.json.JsonArray;
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
}
