package edu.kalum.auth.core.model;

public class Student extends Person {
    private String identity;

    public Student() {
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }
}
