package edu.rit.cs.model;

import java.util.ArrayList;

public class FingerTable {

    private int n, index;
    private ArrayList<Finger> fingers;

    public FingerTable(int numNodes, int index){
        this.n = log2(numNodes);
        this.index = index;
        calcIdeal();
    }

    private int log2(int num){
        return (int) Math.ceil(((Math.log(num)/Math.log(2))));
    }

    public void calcIdeal(){
        fingers = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int ideal = (index + (int)Math.pow(2, i)) % n;
            Finger f = new Finger(i, ideal);
            fingers.add(f);
        }
    }

    //------------------- Setters ---------------------------

    public void setSuccessorAtIndex(int i, int successor){
        fingers.get(i).setActual(successor);
    }

    //------------------- Getters ---------------------------
    public int getIdealAtIndex(int i){
        return fingers.get(i).getIdeal();
    }

    public int getSuccessorAtIndex(int i){
        return fingers.get(i).getActual();
    }

    public ArrayList<Finger> getFingers() {
        return fingers;
    }

    @Override
    public String toString(){
        String result = "Table:\n";

        for (int i = 0; i < n; i++) {
            result += "\t" + fingers.get(i).toString() + "\n";
        }

        return result;
    }
}
