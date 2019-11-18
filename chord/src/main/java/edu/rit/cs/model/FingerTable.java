package edu.rit.cs.model;

import java.io.Serializable;
import java.util.ArrayList;

public class FingerTable implements Serializable {

    private int n, numFingers, index;
    private ArrayList<Finger> fingers;

    public FingerTable(int index, int numNodes){
        this.n = numNodes;
        this.numFingers = log2(numNodes);
        if(numFingers == 0){
            numFingers = 1;
        }
        this.index = index;
        calcIdeal();
    }

    public static int log2(int num){
        return (int) Math.ceil(((Math.log(num)/Math.log(2) + 1e-10)));
    }

    public void calcIdeal(){
        fingers = new ArrayList<>();
        for (int i = 0; i < numFingers; i++) {
            int ideal = (index - 1 + (int)Math.pow(2, i)) % n + 1;
            Finger f = new Finger(i, ideal);
            fingers.add(f);
        }
    }

    //------------------- Setters ---------------------------

    public void setSuccessorAtIndex(int i, Connection actualConn){
        fingers.get(i).setActualConnection(actualConn);
    }

    //------------------- Getters ---------------------------
    public int getIdealAtIndex(int i){
        return fingers.get(i).getIdeal();
    }

    public int getMaxNodes(){
        return this.n;
    }

    public Connection getSuccessorConnectionAtIndex(int i) {return fingers.get(i).getActualConnection();}

    /**
     * Given a destination node, return the ideal hop closest to that node
     * @param destinationNode
     * @return
     */
    public int getTableIdealGivenDestinationIdeal(int destinationNode){
        int maxBeforeIdeal = -1;
        for(Finger f : fingers){
            if(f.getIdeal() <= destinationNode && f.getIdeal() > maxBeforeIdeal){
                maxBeforeIdeal = f.getIdeal();
            }
        }
        return maxBeforeIdeal;
    }

    public Connection getSuccessorConnectionGivenIdeal(int i){
        for(Finger f : fingers){
            if(f.getIdeal() == i){
                return f.getActualConnection();
            }
        }
        return fingers.get(fingers.size() - 1).getActualConnection();
    }

    public ArrayList<Finger> getFingers() {
        return fingers;
    }

    @Override
    public String toString(){
        String result = "Table:\n";

        for (int i = 0; i < numFingers; i++) {
            result += "\t" + fingers.get(i).toString() + "\n";
        }

        return result;
    }
}
