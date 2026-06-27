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

    public Future<JsonObject> findByUsernameAndPassword(JsonObject body) {
        return userRepository.findByUsernameAndPassword(body);
    }

    public Future<String> create(JsonObject body) {
        return userRepository.save(body);
    }

    public Future<Void> update(String userId, JsonObject body) {
        return userRepository.udpate(userId,body);
    }

    public Future<Void> delete(String userId) {
        return userRepository.delete(userId);
    }

}

