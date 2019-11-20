package edu.rit.cs.model;

import java.io.Serializable;

/**
 * Finger Class. Used in Finger Table
 */
public class Finger implements Serializable {
    private int index, ideal;
    private Connection actualConnection;

    /**
     * Finger Constructor
     * @param index
     * @param ideal
     */
    public Finger(int index, int ideal){
        this.index = index;
        this.ideal = ideal;
        this.actualConnection = null;
    }

    //---------------------- Getters -------------------------

    public int getIdeal() {
        return ideal;
    }

    public Connection getActualConnection() {return actualConnection;}

    //----------------------- Setters -------------------------

    public void setActualConnection(Connection actualConnection) {this.actualConnection = actualConnection;}

    @Override
    public String toString(){
        return "index: " + index + "\tideal: " + ideal + "\t successor: " + actualConnection.getNodeId();
    }
}
