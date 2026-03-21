package edu.kalum.auth.core.handler;

import edu.kalum.auth.core.services.RoleService;

public class RoleHandler {
    private final RoleService roleService;

    public RoleHandler(RoleService roleService) {
        this.roleService = roleService;
    }
}
