package edu.kalum.auth.core.handler;

import edu.kalum.auth.core.dtos.ApiResponseDTO;
import edu.kalum.auth.core.services.UserService;
import edu.kalum.logging.core.helpers.Utils;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class UserHandler {
    private final String CONTENT_TYPE = "Content-Type";
    private final String APPLICATION_JSON = "application/json";
    private final UserService userService;
    private final Utils utils;

    public UserHandler(UserService userService, Utils utils) {
        this.userService = userService;
        this.utils = utils;
    }

    public void getAll(RoutingContext routingContext) {
        userService.findAll().onSuccess(handlerResult -> {
            JsonObject requestConfig = getParameterRequest(routingContext);
            this.utils.log(requestConfig.getLong("initialTime"),"Consulta de usuarios realizada con éxito",200,"info",
                    requestConfig.getString("token"),requestConfig.getString("uri"));
            routingContext.response()
                    .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                    .setStatusCode(200)
                    .end(ApiResponseDTO.success(new JsonArray(handlerResult))
                            .encode()).onFailure(error -> {
                        this.utils.log(requestConfig.getLong("initialTime"), error == null ? "Error fetching users": error.getMessage(),503,"error",
                                requestConfig.getString("token"),requestConfig.getString("uri"));
                        routingContext
                                .response()
                                .setStatusCode(503)
                                .end(ApiResponseDTO.error("Error fetching users", error.getMessage()).encode());
                    });
        });
    }
    public void searchByType(RoutingContext routingContext) {
        JsonObject requestConfig = getParameterRequest(routingContext);
        String type = routingContext.request().getParam("type");
        String value = routingContext.request().getParam("value");
        if(type.equalsIgnoreCase("username")) {
            userService.findByUsername(value).onSuccess(handlerResult -> {
                this.utils.log(requestConfig.getLong("initialTime"),"La consulta de la busqueda por ".concat(type).concat(" con el valor de ".concat(value).concat(" fue exitosa")),200,"info",
                        requestConfig.getString("token"),requestConfig.getString("uri"));
                routingContext.response().setStatusCode(200).putHeader(CONTENT_TYPE,APPLICATION_JSON)
                        .end(ApiResponseDTO.success(handlerResult).encode());
            }).onFailure(error -> {
                this.utils.log(requestConfig.getLong("initialTime"), error == null ? "Error search user": error.getMessage(),503,"error",
                        requestConfig.getString("token"),requestConfig.getString("uri"));
                routingContext
                        .response().putHeader(CONTENT_TYPE,APPLICATION_JSON)
                        .setStatusCode(503)
                        .end(ApiResponseDTO.error("Error fetching by username", error.getMessage()).encode());
                    });
        } else if(type.equalsIgnoreCase("email")) {
            this.utils.log(requestConfig.getLong("initialTime"),"La consulta de la busqueda por ".concat(type).concat(" con el valor de ".concat(value).concat(" fue exitosa")),200,"info",
                    requestConfig.getString("token"),requestConfig.getString("uri"));
            userService.findByEmail(value).onSuccess(handlerResult -> {
                routingContext.response().setStatusCode(200).putHeader(CONTENT_TYPE,APPLICATION_JSON)
                        .end(ApiResponseDTO.success(handlerResult).encode());
            }).onFailure(error -> routingContext
                    .response().putHeader(CONTENT_TYPE,APPLICATION_JSON)
                    .setStatusCode(503)
                    .end(ApiResponseDTO.error("Error fetching by email", error.getMessage()).encode()));
        } else {
            this.utils.log(requestConfig.getLong("initialTime"),"Error fetching by type, not exists the search criterial",404,"error",
                    requestConfig.getString("token"),requestConfig.getString("uri"));
            routingContext
                    .response()
                    .putHeader(CONTENT_TYPE,APPLICATION_JSON)
                    .setStatusCode(404)
                    .end(ApiResponseDTO.error("Error fetching by type","Not exists the search criterial").encode());
        }
    }

    public void getUserById(RoutingContext routingContext) {
        String userId = routingContext.pathParam("userId");
        userService.findById(userId).onSuccess(handlerResult -> {
            routingContext.response().putHeader(CONTENT_TYPE,APPLICATION_JSON)
                    .end(ApiResponseDTO.success(handlerResult).encode());
        }).onFailure( error -> routingContext
                .response()
                .putHeader(CONTENT_TYPE,APPLICATION_JSON)
                .setStatusCode(503)
                .end(ApiResponseDTO.error("Error fetching users", error.getMessage()).encode())
        );
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

    public void addRoles(RoutingContext routingContext) {
        String userId = routingContext.pathParam("userId");
        JsonObject roles = routingContext.body().asJsonObject();
        userService.addRoles(userId,roles.getString("roles")).onSuccess(result -> routingContext.response().setStatusCode(204)
                        .putHeader(CONTENT_TYPE,APPLICATION_JSON).end(ApiResponseDTO.updated(null).encode()))
                .onFailure(error -> routingContext.response().putHeader(CONTENT_TYPE,APPLICATION_JSON)
                        .setStatusCode(503).end(ApiResponseDTO.error("Error adding role", error.getMessage()).encode()));
    }

    public void removeRoles(RoutingContext routingContext) {
        String userId = routingContext.pathParam("userId");
        JsonObject roles = routingContext.body().asJsonObject();
        userService.removeRoles(userId,roles.getString("roles")).onSuccess(result ->
                routingContext.response().setStatusCode(204).putHeader(CONTENT_TYPE,APPLICATION_JSON)
                        .end(ApiResponseDTO.deleted().encode()))
                .onFailure(error -> routingContext.response().putHeader(CONTENT_TYPE,APPLICATION_JSON)
                        .setStatusCode(503).end(ApiResponseDTO.error("Error remove roles to user",error.getMessage()).encode()));
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

    private JsonObject getParameterRequest(RoutingContext routingContext) {
        long initialTime = new Date().getTime();
        JsonObject requestConfig = new JsonObject();
        requestConfig.put("initialTime",initialTime);
        String authorization = routingContext.request().getHeader("Authorization");
        if(authorization !=  null && authorization.startsWith("Bearer ")) {
            requestConfig.put("token",authorization.substring(7));
        }
        requestConfig.put("uri",routingContext.request().absoluteURI());
        return requestConfig;
    }
}
