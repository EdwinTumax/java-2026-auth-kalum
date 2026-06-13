package edu.kalum.auth.core.handler;

import edu.kalum.auth.core.dtos.ApiResponseDTO;
import edu.kalum.auth.core.dtos.RoleCreateDTO;
import edu.kalum.auth.core.model.Role;
import edu.kalum.auth.core.services.RoleService;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class RoleHandler {
    private final String CONTENT_TYPE = "Content-Type";
    private final String APPLICATION_JSON = "application/json";
    private final RoleService roleService;

    public RoleHandler(RoleService roleService) {
        this.roleService = roleService;
    }

    public void getAll(RoutingContext routingContext){
        roleService.findAll()
                .onSuccess(handlerRoles -> routingContext.response().putHeader("Content-Type","application/json").end(ApiResponseDTO.success(new JsonArray(handlerRoles)).encode()))
                .onFailure(error -> routingContext.response().setStatusCode(503).end(ApiResponseDTO.error("Error fetching roles", error.getMessage()).encode()));
    }

    public void getByName(RoutingContext ctx) {
        String name = ctx.request().getParam("name");
        roleService.findByName(name)
                .onSuccess(role -> {
                    if(role == null) {
                        ctx.response().putHeader("Content-Type","application/json")
                                .setStatusCode(404)
                                .end(ApiResponseDTO.error("Role not found", "Invalid name").encode());
                    } else {
                        ctx.response().putHeader("Content-Type","application/json")
                                .end(ApiResponseDTO.success(role).encode());
                    }
                }).onFailure(error -> ctx.response().putHeader("Content-Type","application/json")
                        .setStatusCode(503).end(ApiResponseDTO.error("Error fetching role", error.getMessage()).encode()));
    }

    public void getById(RoutingContext ctx) {
        String id = ctx.pathParam("id");
        roleService.findById(id)
                .onSuccess(role -> {
                    if(role == null) {
                        ctx.response().putHeader("Content-Type","application/json")
                                .setStatusCode(404)
                                .end(ApiResponseDTO.error("Role not found", "Invalid id").encode());
                    } else {
                        ctx.response().putHeader("Content-Type","application/json")
                                .end(ApiResponseDTO.success(role).encode());
                    }
                }).onFailure(error -> ctx.response()
                        .putHeader("Content-Type","application/json")
                        .setStatusCode(503).end(ApiResponseDTO.error("Error fetchiing role", error.getMessage()).encode()));
    }

    public void create(RoutingContext ctx) {
        JsonObject body = ctx.body().asJsonObject();
        if(body == null) {
            ctx.response().setStatusCode(400).putHeader("Content-Type","application/json").end(ApiResponseDTO.error("Bad request", "Empty body").encode());
            return;
        }
        RoleCreateDTO role = body.mapTo(RoleCreateDTO.class);
        roleService.create(role)
                .onSuccess( id -> ctx.response().putHeader("Content-Type","application/json")
                        .setStatusCode(201).end(ApiResponseDTO.created(new JsonObject().put("id", id)).encode()))
                .onFailure(error -> ctx.response().putHeader("Content-Type","application/json")
                        .setStatusCode(503).end(ApiResponseDTO.error("Error creating role", error.getMessage()).encode()));

    }

    public void update(RoutingContext ctx) {
        String id = ctx.pathParam("id");
        JsonObject body = ctx.body().asJsonObject();
        if(body == null) {
            ctx.response().setStatusCode(400).putHeader(CONTENT_TYPE,APPLICATION_JSON)
                .end(ApiResponseDTO.error("Bad request", "Empty body").encode());
            return;
        }
        roleService.findById(id).onSuccess(result -> {
            if(result == null) {
                ctx.response().setStatusCode(404).putHeader(CONTENT_TYPE,APPLICATION_JSON).end(ApiResponseDTO.error("Not found","Not exists row").encode());
                return;
            }
            result.setName(body.getString("name"));
            roleService.update(result)
                    .onSuccess(v -> ctx.response().setStatusCode(204).putHeader(CONTENT_TYPE,APPLICATION_JSON)
                            .end(ApiResponseDTO.updated(new JsonObject().put("id",id)).encode()))
                    .onFailure(e -> ctx.response().putHeader(CONTENT_TYPE,APPLICATION_JSON)
                            .setStatusCode(503).end(ApiResponseDTO.error("Error updating role", e.getMessage())
                                    .encode()));
        });
    }

    public void delete(RoutingContext ctx) {
        String id = ctx.pathParam("id");
        roleService.findById(id).onSuccess(result -> {
            if(result == null) {
                ctx.response().setStatusCode(404).putHeader(CONTENT_TYPE,APPLICATION_JSON)
                        .end(ApiResponseDTO.error("Not found", "Not exists row with id ".concat(id)).encode());
                return;
            }
            roleService.delete(id)
                    .onSuccess(v -> ctx.response().setStatusCode(204).putHeader(CONTENT_TYPE,APPLICATION_JSON)
                            .end(ApiResponseDTO.deleted().encode()))
                    .onFailure(e -> ctx.response().putHeader(CONTENT_TYPE,APPLICATION_JSON)
                            .setStatusCode(503).end(ApiResponseDTO.error("Error deleting role", e.getMessage())
                                    .encode()));
        });
    }

}
