package edu.kalum.auth.core.model;

public class Student extends Person {
    private String identity;

    public Student() {
        super();
    }

    @Override
    public boolean authenticate() {
        if (identity.equals("123456")) {
            return true;
        }
        return false;
    }

    public Student(String id, String lastname, String firstname, String email, String address, String phone, String identity) {
        super(id, lastname, firstname, email, address, phone);
        this.identity = identity;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }
}
