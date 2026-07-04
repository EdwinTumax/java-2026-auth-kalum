package edu.kalum.auth.core.verticles;

import edu.kalum.auth.core.config.MySQLPoolConfig;
import edu.kalum.auth.core.repository.RoleRepository;
import edu.kalum.auth.core.repository.UserRepository;
import edu.kalum.auth.core.routers.ApiRouter;
import edu.kalum.auth.core.services.JwtService;
import edu.kalum.auth.core.services.RoleService;
import edu.kalum.auth.core.services.UserService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class MainVerticle extends AbstractVerticle {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    @Override
    public void start(Promise<Void> startPromise) {
        MySQLPool client = MySQLPoolConfig.createPool(vertx);
        RoleRepository roleRepository = new RoleRepository(client);
        UserRepository userRepository = new UserRepository(client,
                passwordEncoder,
                jwtService,
                roleRepository,
                config().getJsonObject("localServer").getString("apiKey"),
                config().getJsonObject("localServer").getString("roleDefault"));
        RoleService roleService = new RoleService(roleRepository);
        UserService userService = new UserService(userRepository);
        Router router = ApiRouter.create(roleService, userService, vertx);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(9088)
                .onSuccess(server -> {
                    System.out.print("Api running on http://localhost:9080");
                }).onFailure(startPromise::fail);
    }
}
