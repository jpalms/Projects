package edu.rit.cs.model;

import java.io.File;

public class Node implements Comparable{

    private int id, nextId, prevId;

    private String ipAddr;
    private int port;

    private FingerTable table;

    public Node(int id){
        this.id = id;
        this.port = Config.port;
        this.table = new FingerTable();
    }

    public Node(int id, String ipAdrr){
        this.id = id;
        this.ipAddr = ipAdrr;
        this.port = Config.port;
        this.table = new FingerTable();
    }

    public Node(int id, String ipAdrr, int port){
        this.id = id;
        this.ipAddr = ipAdrr;
        this.port = port;
        this.table = new FingerTable();
    }

    public Node(int id, int maxNumNodes, String ipAdrr, int port){
        this.id = id;
        this.ipAddr = ipAdrr;
        this.port = port;
        this.table = new FingerTable(id, maxNumNodes);
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

    //------------------ Setter -----------------


    public void setId(int id) {
        this.id = id;
    }

    public void setNextId(int nextId) {
        this.nextId = nextId;
    }

    public void setPrevId(int prevId) {
        this.prevId = prevId;
    }

    // ----------------- TBD -------------------

    public String insert(File file){
        return "";
    }

    public File lookup(String hashCode){
        return null;
    }

    @Override
    public int compareTo(Object cmp){
        return this.id - ((Node)cmp).id;
    }
}
