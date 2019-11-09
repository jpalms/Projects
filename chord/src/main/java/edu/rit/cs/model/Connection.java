package edu.rit.cs.model;

public class Connection {
    private String ipAddr;
    private int port;

    public Connection(String ipAddr, int port){
        this.ipAddr = ipAddr;
        this.port = port;
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
}
