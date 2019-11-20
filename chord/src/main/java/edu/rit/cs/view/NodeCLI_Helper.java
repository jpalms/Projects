package edu.rit.cs.view;

import edu.rit.cs.controller.TCPClientNode;

import edu.rit.cs.model.Connection;
import edu.rit.cs.model.File;
import edu.rit.cs.model.Node;

import java.util.ArrayList;
import java.util.Scanner;

public class NodeCLI_Helper {

    private ArrayList<TCPClientNode> connections;
    private Node node;
    private String server;
    private TCPClientNode firstThread;

    public NodeCLI_Helper(String server) {
        connections = new ArrayList<>();
        this.server = server;
        this.firstThread = nodeSignin(server);
    }

    /**
     * Function for a node to identify its ID and log in.
     *
     * @param server - String to connect with the Server thru TCP
     *this.server = server;
     * @return Node - acts as the instance's information
     */
    private TCPClientNode nodeSignin(String server) {
        System.out.println(
                "=================================\n" +
                        "            Chord Node            \n" +
                        "=================================\n\n");

        System.out.println("Sign In");

        TCPClientNode firstThread = new TCPClientNode(server);
        Scanner input = new Scanner(System.in);
        firstThread.sendObject("");
        while (true){
            System.out.println("Enter Node id: ");
            String id = input.nextLine();
            firstThread.sendObject(id);
            if (firstThread.readObject().equals("false")) {
                firstThread.sendObject(new Node(Integer.parseInt(id), 1, firstThread.getIpAddr(), server, firstThread.getPort(), 0, 0));
                this.node = (Node)firstThread.readObject();
                firstThread.setNode(node);
                firstThread.start();
                return firstThread;
            } else {
                System.out.println("Node id already exists.\n ");
            }
        }
    }

    /*
     * -------------------- Node -------------------------
     */
    /**
     *
     */
    public void queryAll() {

        for(int i = 0; i < node.getTable().getFingers().size(); i++){
            query(i);
        }
    }

    /**
     * Query initially for a fingertable
     * @param i
     */
    public void query(int i){
        Connection update;
        TCPClientNode clientNode = new TCPClientNode(server);
        update = clientNode.query(node, node.getTable().getIdealAtIndex(i));

        node.getTable().setSuccessorAtIndex(i, update);
    }

    /**
     * Function to insert file into the chord.
     * Sends hash generated per file and sends it to the anchor node to be placed.
     * @param file
     */
    public void insert(File file){
        TCPClientNode clientNode = new TCPClientNode(server);
        clientNode.insertLocation(node, file, 0);
    }

    public void lookup(String hash) {
        TCPClientNode clientNode = new TCPClientNode(server);
        File f = clientNode.lookupLocation(node, hash, 0);

        System.out.println(f.toString());
        System.out.println(f.getFileContent());

    }

    public void displayFile() {
        for(File f : node.getStorage()){
            System.out.println("\t" + f.toString() + "\thash: " + (f.hashCode() % node.getTable().getMaxNodes() + 1) + "\t" + "Stored at Node: " + node.getId());
        }
    }

    public void showTable(){
        System.out.println(this.node.getTable().toString());
    }
    /**
     * Gracefully shuts down a peer node.
     */
    public void quit() {
        TCPClientNode quitThread = new TCPClientNode(server);
        // send notification to Server that this node is shutting down
        quitThread.quit(node);

        firstThread.turnOffFirst();

    }
}
