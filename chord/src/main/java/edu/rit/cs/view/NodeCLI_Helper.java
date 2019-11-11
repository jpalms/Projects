package edu.rit.cs.view;

import edu.rit.cs.controller.TCPClientNode;

import edu.rit.cs.model.Node;

import java.io.File;
import java.util.ArrayList;

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
    public void queryAll() {

        for(int i = 0; i < node.getTable().getFingers().size(); i++){
            query(i);
        }
    }

    public void query(int i){
        int update = 0;
        // todo
        node.getTable().setSuccessorAtIndex(i, update);
    }

    public void insert(File file){
        TCPClientNode clientNode = new TCPClientNode(server);
        clientNode.insertLocation(node, file);
    }

    public File lookup(String hash){
        TCPClientNode clientNode = new TCPClientNode(server);
        File f = clientNode.lookupLocation(node, hash);

        return f;
    }

    public void quit(){

    }
}
