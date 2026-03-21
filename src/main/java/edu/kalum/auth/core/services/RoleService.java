package edu.kalum.auth.core.services;

import edu.kalum.auth.core.repository.RoleRepository;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.util.List;

public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository repository) {
        this.roleRepository = repository;
    }

    public Future<List<JsonObject>> findAll() {
        return roleRepository.findAll();
    }

}
