package edu.rit.cs.model;

import java.io.Serializable;

public class Connection implements Serializable {
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
