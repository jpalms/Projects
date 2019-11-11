package edu.rit.cs.model;

import java.io.Serializable;

public class Finger implements Serializable {
    private int index, ideal, actual;

    public Finger(int index, int ideal, int actual){
        this.index = index;
        this.ideal = ideal;
        this.actual = actual;
    }

    public Finger(int index, int ideal){
        this.index = index;
        this.ideal = ideal;
    }

    //---------------------- Getters -------------------------

    /**
     *
     * @return
     */
    public int getIndex() {
        return index;
    }

    public int getActual() {
        return actual;
    }

    public int getIdeal() {
        return ideal;
    }


    //----------------------- Setters -------------------------


    public void setIndex(int index) {
        this.index = index;
    }

    public void setActual(int actual) {
        this.actual = actual;
    }

    public void setIdeal(int ideal) {
        this.ideal = ideal;
    }

    @Override
    public String toString(){
        return "index: " + index + "\tideal: " + ideal + "\t successor: " + actual;
    }
}
