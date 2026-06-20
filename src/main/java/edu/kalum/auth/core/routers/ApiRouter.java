package edu.kalum.auth.core.routers;

import edu.kalum.auth.core.handler.RoleHandler;
import edu.kalum.auth.core.handler.UserHandler;
import edu.kalum.auth.core.services.RoleService;
import edu.kalum.auth.core.services.UserService;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class ApiRouter {
    public static Router create(RoleService roleService, UserService userService, Vertx vertx) {
        final String API_PATH = "/auth-management/v1";
        Router router = Router.router(vertx);
        RoleHandler roleHandler = new RoleHandler(roleService);
        UserHandler userHandler = new UserHandler(userService);

        router.route().handler(BodyHandler.create());
        //roles
        router.get(API_PATH + "/roles").handler(roleHandler::getAll);
        router.get(API_PATH + "/roles/search").handler(roleHandler::getByName);
        router.get(API_PATH + "/roles/:id").handler(roleHandler::getById);
        router.post(API_PATH + "/roles").handler(roleHandler::create);
        router.delete(API_PATH + "/roles/:id").handler(roleHandler::delete);
        router.put(API_PATH + "/roles/:id").handler(roleHandler::update);

        //Users
        router.get(API_PATH + "/users").handler(userHandler::getAll);
        router.post(API_PATH + "/users").handler(userHandler::create);
        router.put(API_PATH + "/users/:userId").handler(userHandler::update);
        router.delete(API_PATH + "/users/:userId").handler(userHandler::remove);
        return router;
    }
}
