package edu.kalum.auth.core.services;

import edu.kalum.auth.core.repository.UserRepository;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.util.List;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public Future<List<JsonObject>> findAll() { return userRepository.findAll(); }

}

