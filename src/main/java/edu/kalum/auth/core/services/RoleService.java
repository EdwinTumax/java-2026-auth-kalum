package edu.kalum.auth.core.services;

import edu.kalum.auth.core.dtos.RoleCreateDTO;
import edu.kalum.auth.core.model.Role;
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
    public Future<Role> findByName(String name) { return roleRepository.findByName(name); }
    public Future<Role> findById(String id) { return  roleRepository.findyId(id);}
    public Future<String> create(RoleCreateDTO role) { return roleRepository.save(role);}
    public Future<Void> delete(String id) { return roleRepository.delete(id);}
    public Future<Void> update(Role role) { return roleRepository.update(role); }
}
