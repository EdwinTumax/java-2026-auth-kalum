package edu.kalum.auth.core.model;

public class User extends Person {
    private String username;
    private String password;
    private Role role;

    public User() {
        super();
    }

    @Override
    public boolean authenticate() {
        if(username.equals("edwintumax") && password.equals("123")) {
            return true;
        }
        return false;
    }

    public User(String id, String lastname, String firstName, String email, String addesss, String phone, String username, String password, Role role) {
        super(id,lastname,firstName,addesss,phone,email);
        this.username = username;
        this.password = password;
        this.role = role;
    }

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
