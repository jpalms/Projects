package edu.rit.cs.model;

public class Connection {
    private String ipAddr;
    private int port, nodeId;

    public Connection(String ipAddr, int port, int id){
        this.ipAddr = ipAddr;
        this.port = port;
        this.nodeId = id;
    }

    public boolean isOnline(){
        //todo
        return false;
    }
    //-------------- Getters -------------

    public String getIpAddr(){
        return this.ipAddr;
    }

    public int getPort(){
        return this.port;
    }

    public int getNodeId() {
        return nodeId;
    }
}
