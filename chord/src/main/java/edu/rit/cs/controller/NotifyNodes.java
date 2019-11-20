package edu.rit.cs.controller;

import edu.rit.cs.model.Config;
import edu.rit.cs.model.Connection;
import edu.rit.cs.model.Node;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;


import java.util.concurrent.ConcurrentSkipListMap;

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
        while (running) {
            System.out.print("");
            ConcurrentSkipListMap<Integer, Connection> onlineNodes = anchorNode.getOnlineNodes();
            Collection<Connection> connections = anchorNode.getOnlineConnections();
            if(!onlineNodes.isEmpty()){
                if(handler.update()) {
                    ArrayList<String> removedNodes = handler.getRemovedNodes();
                    // send removed Node, Files that they get
                    HashMap<String, ArrayList<File>> filesToNode = new HashMap<>();
                    int n = (int) Math.pow(2, (int)Math.ceil(((int)(Math.log(handler.getMaxNodeNum())/Math.log(2)))));

                    // node online
                    ArrayList<String> newNodes = handler.getNewNodes();

                    // notify nodes of new online node
                    if(newNodes.size() > 0) {
                        for (Object conn: connections) {
                            for(String node: newNodes){
                                newNodeOnline(node, (Connection)conn);
                            }
                        }
                    }

                    // notify nodes of node that has gone offline
                    if(removedNodes.size() > 0) {
                        for (Connection conn : onlineNodes.values()) {
                            sendInfo(conn, removedNodes);
                        }
                    }

                    if(newNodes.size() > 0) {
                        for (Object conn: connections) {
                                update((Connection)conn);
                        }
                    }


                    // tell nodes to update file storage
                    if(newNodes.size() > 0) {
                        for (Object conn: connections) {
                            for(String node: newNodes){
                                fileRorder(node, (Connection)conn);
                            }
                        }
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

            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());

            out.writeObject(Config.NEW_NODE);

            out.writeObject(nodeId);
            // client will then query to update Successor
            clientSocket.close();
        } catch(UnknownHostException e){

        } catch (EOFException e){

        } catch (IOException e){

        }
    }
    private void sendInfo(Connection conn, ArrayList<String> removed){

        ObjectInputStream in;
        ObjectOutputStream out;
        Socket clientSocket;

        try {
            clientSocket = new Socket(conn.getIpAddr(), conn.getPort());

            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());

            out.writeObject(Config.REMOVED);

            for(String node: removed) {
                out.writeObject(node);
            }

            out.writeObject(Config.DONE);
            // client will then query to update Successor
            clientSocket.close();
        } catch(UnknownHostException e){

        } catch (EOFException e){

        } catch (IOException e){

        }
    }

    private void fileRorder(String nodeId, Connection conn){
        ObjectInputStream in;
        ObjectOutputStream out;
        Socket clientSocket;

        try {
            clientSocket = new Socket(conn.getIpAddr(), conn.getPort());

            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());

            out.writeObject(Config.REORDER);

            out.writeObject(nodeId);
            // client will then update file storage

            clientSocket.close();
        } catch(UnknownHostException e){

        } catch (EOFException e){

        } catch (IOException e){

        }
    }

    private void update(Connection conn){
        ObjectInputStream in;
        ObjectOutputStream out;
        Socket clientSocket;

        try {
            clientSocket = new Socket(conn.getIpAddr(), conn.getPort());

            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());

            out.writeObject(Config.UPDATE);

            //out.writeObject(nodeId);
            // client will then update file storage

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