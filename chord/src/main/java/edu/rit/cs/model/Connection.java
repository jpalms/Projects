package edu.rit.cs.model;

import java.io.Serializable;

/**
 * Connection Class. Data structure to represent
 * a node connection, with ip, port, and node id
 */
public class Connection implements Serializable {
    private String ipAddr;
    private int port, nodeId;

    /**
     * Constructor
     * @param ipAddr IP Address of the Node
     * @param port Port of the Node
     * @param id ID of the Node
     */
    public Connection(String ipAddr, int port, int id){
        this.ipAddr = ipAddr;
        this.port = port;
        this.nodeId = id;
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

    //-------------- Override -------------

    @Override
    public String toString(){
        return "IP Address: " + ipAddr + " Port: " + port + " Node: " + nodeId;
    }
}
