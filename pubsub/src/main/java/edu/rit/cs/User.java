package edu.rit.cs;

import java.util.*;

public class User extends PubSubAgent{

    public enum pubOrSub {
        PUB,
        SUB
    }

    public pubOrSub role;
    private String id;
    private String password;

    // without PubSub info
    public User(pubOrSub role, String id, String password) {
        this.role = role;
        this.id = id;
        this.password = password;
    }

    public pubOrSub getRole() {
        return role;
    }

    public void setRole(pubOrSub role) {
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return id + " (pass: " +
                password + "), " +
                role;
    }

}
