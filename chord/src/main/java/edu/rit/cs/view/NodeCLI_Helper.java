package edu.rit.cs.view;

import edu.rit.cs.controller.TCPClientNode;

import edu.rit.cs.model.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class NodeCLI_Helper {

    private ArrayList<TCPClientNode> connections;
    private Node node;
    private String server;
    private int nodeId;

    public NodeCLI_Helper(Node node, String server){
        connections = new ArrayList<>();
        this.node = node;
        this.server = server;
        this.nodeId = node.getId();
    }

    public void turnOff(){
        while (connections.size() > 0) {
            connections.get(0).turnOff();
            connections.remove(0);
        }
    }


    /*
     * --------------------Publisher-------------------------
     */
    /**
     *
     */
    public void queueAll() {

        TCPClientNode thread = new TCPClientNode(server);

        connections.add(thread);
    }

    public void queue(){

    }

    public void insert(File file){

    }

    public void lookup(String hash){

    }

    public void quit(){

    }
}
