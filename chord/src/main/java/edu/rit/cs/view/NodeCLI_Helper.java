package edu.rit.cs.view;

import edu.rit.cs.controller.TCPClientNode;

import edu.rit.cs.model.FingerTable;
import edu.rit.cs.model.Node;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class NodeCLI_Helper {

    private ArrayList<TCPClientNode> connections;
    private Node node;
    private String server;
    private int nodeId;

    public NodeCLI_Helper(Node node, String server) {
        connections = new ArrayList<>();
        this.node = node;
        this.server = server;
        this.nodeId = node.getId();
    }

    public void turnOff() {
        while (connections.size() > 0) {
            connections.get(0).turnOff();
            connections.remove(0);
        }
    }




    /*
     * --------------------Publisher-------------------------
     *//*
    *//**
     *
     */
    public void queueAll() {

        TCPClientNode thread = new TCPClientNode(server);

        connections.add(thread);
    }

    public void queue(){

    }

    /**
     * Function to insert file into the chord.
     * Sends hash generated per file and sends it to the anchor node to be placed.
     * @param target
     */
    public void insert(File target){
        TCPClientNode insertThread = new TCPClientNode(server);

        // send notification that an insertion is taking place
        insertThread.sendObject("file");
        insertThread.sendObject("insert");

        // send file to be inserted into the Chord
        insertThread.sendObject(target);

        Object obj = insertThread.readObject();
        Integer id = (Integer) obj;
        // TODO - send to correct node
        fileTransfer(id);
    };

    /**
     * Function to find right node to send a file to.
     * Either sends to the correct node, or one prior to it.
     * @param id
     */
    public void fileTransfer(Integer id){

    }

    /**
     * For use in insert. Creates a hash code based on the file.
     * @param target - file to be sent
     * @return - hashCode
     */
    public String hash(File target){ return " " ; };

    /**
     * Looks for file in this node.
     * @param hash - hashCode of file to be looked for.
     */
    public void lookup(String hash){};

    /**
     * Gracefully shuts down a peer node.
     */
    public void quit(){

        TCPClientNode quitThread = new TCPClientNode(server);

        // send notification to Server that this node is shutting down
        quitThread.sendObject("quit");

        // send number of files to server, in preparation
        quitThread.sendObject(this.node.getStorage().size());

        // send files to server for rehashing, remove from node
        for(File target: this.node.getStorage()){
            quitThread.sendObject(target);
            this.node.getStorage().remove(target);
        }

    }

}
