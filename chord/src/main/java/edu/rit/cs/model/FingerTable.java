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

    public ArrayList<Finger> getFingers() {
        return fingers;
    }
}
