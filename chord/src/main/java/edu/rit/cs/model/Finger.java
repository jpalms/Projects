package edu.rit.cs.model;

import java.io.Serializable;

public class Finger implements Serializable {
    private int index, ideal, actual;
    private Connection actualConnection;

    public Finger(int index, int ideal, int actual){
        this.index = index;
        this.ideal = ideal;
        this.actual = actual;
        this.actualConnection = null;
    }

    public Finger(int index, int ideal, Connection actual){
        this.index = index;
        this.ideal = ideal;
        this.actualConnection = actual;
    }

    public Finger(int index, int ideal){
        this.index = index;
        this.ideal = ideal;
        this.actualConnection = null;
    }

    //---------------------- Getters -------------------------

    /**
     *
     * @return
     */
    public int getIndex() {
        return index;
    }

    public int getIdeal() {
        return ideal;
    }

    public Connection getActualConnection() {return actualConnection;}


    //----------------------- Setters -------------------------


    public void setIndex(int index) {
        this.index = index;
    }

    public void setIdeal(int ideal) {
        this.ideal = ideal;
    }

    public void setActualConnection(Connection actualConnection) {this.actualConnection = actualConnection;}

    @Override
    public String toString(){
        return "index: " + index + "\tideal: " + ideal + "\t successor: " + actual;
    }
}
