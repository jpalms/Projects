package edu.rit.cs.controller;

import edu.rit.cs.model.Connection;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Class of Notifying Subscribers about events and all Users about Topics
 **/
public class NotifyNodes extends Thread {
    private boolean running;
    private MiniServer handler;
    private AnchorNode anchorNode;


    /**
     * Constructor Class for NotifySubs
     **/
    public NotifyNodes(AnchorNode em) {
        this.running = true;
        MiniServer handler = new MiniServer(em);
        handler.start();
        this.handler = handler;
        this.anchorNode = em;
    }

    /**
     * Continuously checks if there are events to send to online subscribers
     * Then tells the workers what to send
     **/
    public void run() {
        System.out.println("Server is running ...");
        TreeMap<String, Connection> onlineNodes;
        while (running) {
            System.out.print("");
            onlineNodes = anchorNode.getOnlineNodes();
            if(!onlineNodes.isEmpty()){
                if(handler.update()) {
                    HashMap<String, ArrayList<File>> removedNodes = handler.getRemovedNodes();
                    // send removed Node, Files that they get
                }
            }
        }
    }


    private void sendInfo(String nodeId, String removed, ArrayList<File> files){
        Connection conn = anchorNode.getOnlineNodes().get(nodeId);

        ObjectInputStream in;
        ObjectOutputStream out;
        Socket clientSocket;

        try {
            clientSocket = new Socket(conn.getIpAddr(), conn.getPort());

            out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.writeObject(removed);
            for (File f:files) {
                out.writeObject(f);
            }

            out.writeObject("Done");
            // client will then query to update Successor
            clientSocket.close();
        } catch(UnknownHostException e){

        } catch (EOFException e){

        } catch (IOException e){

        }
    }
    // stops the loop
    public void turnOff () {
        running = false;
        handler.turnOff();
    }
}