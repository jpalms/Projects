package edu.rit.cs.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Finger Table class, uses Finger class
 */
public class FingerTable implements Serializable {

    /*
    Class Variables
     */

    private int n, numFingers, index;
    private ArrayList<Finger> fingers;

    /**
     * Constructor of a Finger Table
     * @param index index for start of fingertable
     * @param numNodes num of network nodes
     */
    public FingerTable(int index, int numNodes){
        this.n = numNodes;
        this.numFingers = log2(numNodes);
        if(this.n ==  1){
            numFingers = 1;
        }
        this.index = index;
        calcIdeal();
    }

    /**
     * Calculate log base 2 of the finger table
     * @param num number to calculate log2 of
     * @return log2(num)
     */
    private int log2(int num){
        return (int) Math.ceil(((Math.log(num -1)/Math.log(2) + 1e-10)));
    }

    /**
     * Calculate the ideal successor for each finger in the fingertable
     */
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

    /**
     * Given a destination node, return the ideal hop closest to that node
     * @param startNodeID NodeID we are currently at
     * @param destinationNodeID NodeID we are trying to reach
     * @return Connection that is the biggest hop available to destination
     */
    public synchronized Connection getConnectionGivenStartAndDestinationID(int startNodeID, int destinationNodeID){
        /*
        Depending on how ideal is calculated, we may not need the various if cases.
        If ideal properly hops, the logic will be different.
         */

        // Normal Case
        int maxBeforeIdeal = -1;
        for(Finger f : fingers){
            if(f.getIdeal() <= destinationNodeID && f.getIdeal() > maxBeforeIdeal){
                maxBeforeIdeal = f.getIdeal();
            }
        }
        return getActualConnectionGivenIdeal(maxBeforeIdeal);
    }

    /**
     * Given a destination node, return the ideal hop closest to that node
     * @param startNodeID NodeID we are currently at
     * @param destinationNodeID NodeID we are trying to reach
     * @return Connection that is the biggest hop available to destination
     */
    public synchronized int getConnectionSuccessor(int startNodeID, int destinationNodeID){
        /*
        Depending on how ideal is calculated, we may not need the various if cases.
        If ideal properly hops, the logic will be different.
         */

        // Normal Case
        if(startNodeID < destinationNodeID){
            int maxBeforeIdeal = -1;
            for(Finger f : fingers){
                if(f.getIdeal() <= destinationNodeID && f.getIdeal() > maxBeforeIdeal){
                    maxBeforeIdeal = f.getIdeal();
                }
            }
            return maxBeforeIdeal;
        }
        // If we pass zero
        // TODO Add Logic and Test
        else if(startNodeID > destinationNodeID){
            int maxBeforeIdeal = -1;
            for(Finger f : fingers){
                if(f.getIdeal() <= destinationNodeID + getMaxNodes() && f.getIdeal() > maxBeforeIdeal + getMaxNodes()){
                    maxBeforeIdeal = f.getIdeal() + getMaxNodes();
                }
            }
            return maxBeforeIdeal;
        }
        // Should never get here
        else{
            return -1;
        }
    }
    /**
     * Helper function for above. gets the connection given
     * a correct ideal. Assumed correct
     * @param i int representing ideal
     * @return Connection to node
     */
    private Connection getActualConnectionGivenIdeal(int i){
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