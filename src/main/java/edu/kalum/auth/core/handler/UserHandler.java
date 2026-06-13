package edu.kalum.auth.core.handler;

import edu.kalum.auth.core.dtos.ApiResponseDTO;
import edu.kalum.auth.core.services.UserService;
import io.vertx.core.json.JsonArray;
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
}
