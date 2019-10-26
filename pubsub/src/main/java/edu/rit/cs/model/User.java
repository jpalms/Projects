package edu.rit.cs.model;

/**
 * User Class, which represents a Publisher or Subscriber
 */
public class User extends PubSubAgent {

    /**
     * Enum to indicate whether the instance is a Publisher or Subscriber
     */
    public enum pubOrSub {
        PUB,
        SUB
    }

    public pubOrSub role;
    private String id;
    private String password;

    /**
     * Constructor class for User
     *
     * @param role - indicates if this instance is a Publisher of Subscriber
     * @param id - unique username
     * @param password - password to login
     */
    public User(pubOrSub role, String id, String password) {
        this.role = role;
        this.id = id;
        this.password = password;
    }

    /**
     * Getter method for Role
     *
     * @return - role enum
     */
    public pubOrSub getRole() {
        return role;
    }

    /**
     * Determines if a user is a Subscriber
     *
     * @return - true if Subscriber, false otherwise
     */
    public boolean isSub(){
        if (this.getRole().equals(pubOrSub.SUB)){
            return true;
        } else {
            return false;
        }
    }

    /**
     * Determines if a user is a Publisher
     *
     * @return - true if Publisher, false otherwise
     */
    public boolean isPub(){
        if (this.getRole().equals(pubOrSub.PUB)){
            return true;
        } else {
            return false;
        }
    }

    /**
     * Getter method for id
     *
     * @return - unique id of User
     */
    public String getId() {
        return id;
    }

    /**
     * Determines if a password matches
     *
     * @param password - String to compare
     * @return - true if matches, false otherwise
     */
    public boolean isCorrectPassord(String password){
        return this.password.equals(password);
    }

    /**
     * toString function
     *
     * @return - String representation of User
     */
    @Override
    public String toString() {
        return id + " (pass: " +
                password + "), " +
                role;
    }

}
