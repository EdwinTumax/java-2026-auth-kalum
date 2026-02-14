package edu.kalum.auth.core.model;

public class User {
    private String username;
    private String password;
    private Role role;

    // POO Encasulamiento (get/set) -> accesores y modificadores
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return this.role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

}
