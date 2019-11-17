package edu.rit.cs.model;

import edu.rit.cs.model.File;
import java.io.Serializable;
import java.util.ArrayList;

public class Node implements Comparable, Serializable {

    private int id, nextId, prevId;

    private String ipAddr;
    private int port;

    private FingerTable table;

    private ArrayList<File> storage;

    public Node(int id, int maxNumNodes, String ipAdrr, int port, int nextId, int prevId){
        this.id = id;
        this.ipAddr = ipAdrr;
        this.port = port;
        this.table = new FingerTable(id, maxNumNodes);
        this.nextId = nextId;
        this.prevId = prevId;
        this.storage = new ArrayList<>();
    }

    //------------------- Getter --------------

    public int getId() {
        return id;
    }

    public int getNextId() {
        return nextId;
    }

    public int getPrevId() {
        return prevId;
    }

    public int getPort() {
        return port;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public FingerTable getTable() { return table; }

    public ArrayList<File> getStorage() { return storage; }

    //------------------ Setter -----------------

    public void setNextId(int nextId) {
        this.nextId = nextId;
    }

    public void setPrevId(int prevId) {
        this.prevId = prevId;
    }

    @Override
    public int compareTo(Object cmp){
        return this.id - ((Node)cmp).id;
    }
}
