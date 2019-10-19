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

    public boolean isSub(){
        if (this.getRole().equals(pubOrSub.SUB)){
            return true;
        } else {
            return false;
        }
    }

    public boolean isPub(){
        if (this.getRole().equals(pubOrSub.PUB)){
            return true;
        } else {
            return false;
        }
    }

    public String getId() {
        return id;
    }

    public boolean isCorrectPassord(String password){
        return this.password.equals(password);
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
