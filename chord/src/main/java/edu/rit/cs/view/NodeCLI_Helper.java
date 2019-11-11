package edu.rit.cs.view;

import edu.rit.cs.controller.TCPClientNode;

import edu.rit.cs.model.FingerTable;
import edu.rit.cs.model.Node;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

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
        TCPClientNode clientNode = new TCPClientNode(server);
        update = Integer.parseInt(clientNode.query(node, node.getTable().getIdealAtIndex(i)));

        node.getTable().setSuccessorAtIndex(i, update);
    }

    /**
     * Function to insert file into the chord.
     * Sends hash generated per file and sends it to the anchor node to be placed.
     * @param file
     */
    public void insert(File file){
        TCPClientNode clientNode = new TCPClientNode(server);
        clientNode.insertLocation(node, file);
    }

    /** This is the code Jeff wrote, not sure if usable or not


     public void insert(File target){
        TCPClientNode insertThread = new TCPClientNode(server);

        // send notification that an insertion is taking place
        insertThread.sendObject("file");
        insertThread.sendObject("insert");

        // send file to be inserted into the Chord
        insertThread.sendObject(target);

        Object obj = insertThread.readObject();
        Integer id = (Integer) obj;
        fileTransfer(id);
    }


    */

    public File lookup(String hash){
        TCPClientNode clientNode = new TCPClientNode(server);
        File f = clientNode.lookupLocation(node, hash);
    /**
     * Function to find right node to send a file to.
     * Either sends to the correct node, or one prior to it.
     * @param id
     */
    public void fileTransfer(Integer id){

        return f;
    }

    /**
     * For use in insert. Creates a hash code based on the file.
     * @param target - file to be sent
     * @return - hashCode
     */
    public String hash(File target){ return " " ; };

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
