package edu.kalum.auth.core.handler;

import edu.kalum.auth.core.dtos.ApiResponseDTO;
import edu.kalum.auth.core.services.UserService;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class UserHandler {
    private final String CONTENT_TYPE = "Content-Type";
    private final String APPLICATION_JSON = "application/json";
    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public void getAll(RoutingContext routingContext) {
        userService.findAll().onSuccess(handlerResult -> {
            routingContext.response()
                    .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                    .end(ApiResponseDTO.success(new JsonArray(handlerResult))
                            .encode())
                    .onFailure(error -> routingContext
                            .response()
                            .setStatusCode(503)
                            .end(ApiResponseDTO.error("Error fetching users", error.getMessage()).encode()));
        });
    }

    public void login(RoutingContext routingContext) {
        JsonObject body = routingContext.body().asJsonObject();
        if(body == null) {
            routingContext.response().putHeader(CONTENT_TYPE,APPLICATION_JSON).setStatusCode(400).end(ApiResponseDTO.error("Bad request","Empty Body").encode());
            return;
        }
        userService.findByUsernameAndPassword(body)
                .onSuccess(credentials -> routingContext.response().putHeader(CONTENT_TYPE,APPLICATION_JSON)
                        .setStatusCode(201).end(ApiResponseDTO.success(credentials).encode()))
                .onFailure( error -> routingContext.response().putHeader(CONTENT_TYPE,APPLICATION_JSON)
                        .setStatusCode(503).end(ApiResponseDTO.error("Error username and password invalid", error.getMessage()).encode()));
    }

    public void createUserWithToken(RoutingContext routingContext) {
        JsonObject body = routingContext.body().asJsonObject();
        if(body == null){
            routingContext.response().putHeader(CONTENT_TYPE,APPLICATION_JSON)
                    .setStatusCode(400).end(ApiResponseDTO.error("Bad request","Empty body").encode());
            return;
        }
        userService.createUserWithToken(body)
                .onSuccess(handlerResponse -> routingContext
                        .response()
                        .putHeader(CONTENT_TYPE,APPLICATION_JSON)
                        .setStatusCode(201).end(ApiResponseDTO.created(handlerResponse).encode()))
                .onFailure(error -> routingContext.response().putHeader(CONTENT_TYPE,APPLICATION_JSON)
                        .setStatusCode(503).end(ApiResponseDTO.error("Error al momento de crear el usuario",error.getMessage()).encode()));
    }

    public void addRole(RoutingContext routingContext) {
        String userId = routingContext.pathParam("userId");
        String roleId = routingContext.pathParam("roleId");
        userService.addRole(userId,roleId).onSuccess(result -> routingContext.response().setStatusCode(204).putHeader(CONTENT_TYPE,APPLICATION_JSON).end(ApiResponseDTO.updated(null).encode()))
                .onFailure(error -> routingContext.response().putHeader(CONTENT_TYPE,APPLICATION_JSON).setStatusCode(503).end(ApiResponseDTO.error("Error adding role", error.getMessage()).encode()));
    }

    public void create(RoutingContext routingContext) {
        JsonObject body = routingContext.body().asJsonObject();
        if(body == null) {
            routingContext.response().putHeader(CONTENT_TYPE,APPLICATION_JSON)
                    .setStatusCode(400).end(ApiResponseDTO.error("Bad request","Empty body").encode());
            return;
        }
        userService.create(body)
                .onSuccess(id -> routingContext
                        .response()
                            .putHeader(CONTENT_TYPE,APPLICATION_JSON)
                            .setStatusCode(201).end(ApiResponseDTO.created(new JsonObject().put("id",id)).encode()))
                .onFailure(error -> routingContext
                        .response()
                        .putHeader(CONTENT_TYPE,APPLICATION_JSON)
                        .setStatusCode(503).end(ApiResponseDTO.error("Error creating user",error.getMessage()).encode()));
    }

    public void update(RoutingContext routingContext) {
        String userId = routingContext.pathParam("userId");
        JsonObject body = routingContext.body().asJsonObject();
        if(body == null) {
            routingContext.response().putHeader(CONTENT_TYPE,APPLICATION_JSON)
                    .setStatusCode(400).end(ApiResponseDTO.error("Bad request", "Empty body").encode());
            return;
        }
        userService.update(userId,body)
                .onSuccess( v -> routingContext
                        .response()
                        .putHeader(CONTENT_TYPE,APPLICATION_JSON)
                        .end(ApiResponseDTO.updated(new JsonObject().put("id",userId))
                                .encode()))
                .onFailure(error -> routingContext
                        .response()
                        .putHeader(CONTENT_TYPE,APPLICATION_JSON)
                        .end(ApiResponseDTO.error("Error updating user", error.getMessage())
                                .encode()));
    }

    public void remove(RoutingContext routingContext) {
        String userId = routingContext.pathParam("userId");
        if(userId == null || userId.isEmpty()) {
            routingContext.response().putHeader(CONTENT_TYPE,APPLICATION_JSON)
                    .setStatusCode(400).end(ApiResponseDTO.error("Bad Request", "Empty userId").encode());
        }
        userService.delete(userId)
                .onSuccess( v -> routingContext
                        .response()
                        .putHeader(CONTENT_TYPE,APPLICATION_JSON)
                        .setStatusCode(204)
                        .end(ApiResponseDTO.deleted().encode()))
                .onFailure(error -> routingContext
                        .response()
                        .putHeader(CONTENT_TYPE,APPLICATION_JSON)
                        .end(ApiResponseDTO.error("Error remove user",error.getMessage()).encode()));
    }
}
