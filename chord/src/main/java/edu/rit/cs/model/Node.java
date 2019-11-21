package edu.rit.cs.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Node class is the model for a Chord Node
 */
public class Node implements Comparable, Serializable {

    private int id, nextId, prevId;

    private String ipAddr;
    private String serverIp;
    private int port;

    private FingerTable table;

    private ConcurrentLinkedQueue<File> storage;

    /**
     * Constructor for a Node Model
     * @param id Id of the Node
     * @param maxNumNodes max nodes in the network
     * @param ipAdrr IP address of the node
     * @param serverIp IP of the server
     * @param port port
     * @param nextId id of successor
     * @param prevId id of previous
     */
    public Node(int id, int maxNumNodes, String ipAdrr, String serverIp, int port, int nextId, int prevId){
        this.id = id;
        this.ipAddr = ipAdrr;
        this.serverIp = serverIp;
        this.port = port;
        this.table = new FingerTable(id, maxNumNodes);
        this.nextId = nextId;
        this.prevId = prevId;
        this.storage = new ConcurrentLinkedQueue<>();
    }

    //------------------- Getter --------------

    public synchronized int getId() {
        return id;
    }

    public int getNextId() {
        return nextId;
    }

    public int getPrevId() {
        return prevId;
    }

    public synchronized int getPort() {
        return port;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public synchronized FingerTable getTable() { return table; }

    public synchronized ConcurrentLinkedQueue<File> getStorage() { return storage; }

    public synchronized void emptyTable(){
        this.table = new FingerTable(this.id, this.getTable().getMaxNodes());
    }

    public synchronized String getServerIp() {
        return serverIp;
    }

    /**
     * Rehashes a finger table entry
     * @param str integer node id
     * @return boolean
     */
    public synchronized boolean rehash(String str){
        int num = Integer.parseInt(str);
        if(num > this.getTable().getMaxNodes()){
            this.table = new FingerTable(this.id, num);
            return true;
        }
        else{
            return false;
        }
    }

    //------------------ Setter -----------------

    public void setNextId(int nextId) {
        this.nextId = nextId;
    }

    public void setPrevId(int prevId) {
        this.prevId = prevId;
    }

    @Override
    public synchronized int compareTo(Object cmp){
        return this.id - ((Node)cmp).id;
    }

    @Override
    public String toString(){
        return this.id + "";
    }
}
