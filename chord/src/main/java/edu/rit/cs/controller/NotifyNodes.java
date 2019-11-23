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

                    // node online
                    ArrayList<String> newNodes = handler.getNewNodes();

                    // notify nodes of new online node
                    if(newNodes.size() > 0) {
                        for (Connection conn: connections) {
                            for(String node: newNodes){
                                newNodeOnline(node, conn);
                            }
                        }
                    }

                    // notify nodes of node that has gone offline
                    if(removedNodes.size() > 0) {
                        for (Connection conn: connections) {
                            for(String node: removedNodes){
                                //System.out.println("removed node update " + conn.getNodeId());
                                offlineNode(node, conn);
                            }
                        }
                    }

                    //System.out.println("boop");

                    // tels node to update finger table
                    if(newNodes.size() > 0 || removedNodes.size() > 0) {
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

    /**
     * Connects to node and tells node that another node has come online
     * @param conn - how to connect to node
     * @param nodeId - node to notify
     */
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

    /**
     * Connects to node and tells node what nodes have gone offline
     * @param conn - how to connect to node
     */
    private void offlineNode(String node, Connection conn){

        ObjectInputStream in;
        ObjectOutputStream out;
        Socket clientSocket;

        try {
            clientSocket = new Socket(conn.getIpAddr(), conn.getPort());

            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());

            out.writeObject(Config.REMOVED);

            out.writeObject(node);
            // client will then query to update Successor
            clientSocket.close();
        } catch(UnknownHostException e){

        } catch (EOFException e){

        } catch (IOException e){

        }
    }

    /**
     * Connects to node and tells node to reorder its files
     * @param conn - how to connect to node
     * @param nodeId - node to notify
     */
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

    /**
     * Connects to node and tells node to update its finger table
     * @param conn - node to connect to
     */
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

            out.writeObject(Config.DONE);

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