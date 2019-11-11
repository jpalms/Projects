package edu.rit.cs.controller;

import edu.rit.cs.model.Connection;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
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
                    HashMap<String, ArrayList<File>> filesToNode = new HashMap<>();
                    int n = (int) Math.pow(2, (int)Math.ceil(((int)(Math.log(handler.getMaxNodeNum())/Math.log(2)))));

                    // node online
                    ArrayList<String> newNodes = handler.getNewNodes();

                    if(!newNodes.isEmpty()) {
                        for (Connection conn : onlineNodes.values()) {
                            for(String node: newNodes){

                            }
                        }
                    }
                    // send files from offline to Online nodes
                    for(ArrayList<File> files: removedNodes.values()){
                        for(File f: files){
                            int k = f.hashCode() % n;
                            if(filesToNode.containsKey(k)){
                                ArrayList<File> temp = new ArrayList<>();
                                temp.add(f);
                                filesToNode.put(k + "", temp);
                            } else{
                                filesToNode.get(k).add(f);
                            }
                        }
                    }
                    for(Connection conn: onlineNodes.values()){
                        sendInfo(conn.getNodeId() + "", removedNodes.keySet(), filesToNode.get(conn.getNodeId()));
                    }
                }
            }
        }
    }


    //update online node about new Node
    private void newNodeOnline(String nodeId, Connection conn){
        ObjectInputStream in;
        ObjectOutputStream out;
        Socket clientSocket;

        try {
            clientSocket = new Socket(conn.getIpAddr(), conn.getPort());

            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());

            out.writeObject("newNode");

            out.writeObject(nodeId);

            out.writeObject("Done");
            // client will then query to update Successor
            clientSocket.close();
        } catch(UnknownHostException e){

        } catch (EOFException e){

        } catch (IOException e){

        }
    }
    private void sendInfo(String nodeId, Set<String> removed, ArrayList<File> files){
        Connection conn = anchorNode.getOnlineNodes().get(nodeId);

        ObjectInputStream in;
        ObjectOutputStream out;
        Socket clientSocket;

        try {
            clientSocket = new Socket(conn.getIpAddr(), conn.getPort());

            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());

            out.writeObject("removed");

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