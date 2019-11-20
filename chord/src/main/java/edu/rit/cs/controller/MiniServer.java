package edu.rit.cs.controller;

import edu.rit.cs.model.Config;
import edu.rit.cs.model.Connection;
import edu.rit.cs.model.Node;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Class to Handle the creations of all threads that communicate with clients
 **/
public class MiniServer extends Thread {
    private ArrayList<Worker> workers = new ArrayList<>();
    private ArrayList<String> removedNodes;
    private ArrayList<String> newNodes;
    boolean running;
    private int maxNodeNum;
    private AnchorNode anchorNode;

    /**
     * Constructor class for Handler, initialize onlineUsers maps
     **/
    public MiniServer(AnchorNode anchorNode) {
        this.anchorNode = anchorNode;
        this.running = true;
        this.maxNodeNum = 0;
        this.removedNodes = new ArrayList<>();
        this.newNodes = new ArrayList<>();
    }

    /**
     * Starts a notify thread, and continuously hits new connections
     **/
    public void run() {
        workers = new ArrayList<>();
        try {
            // start server
            int serverPort = Config.port;
            ServerSocket listenSocket = new ServerSocket(serverPort);

            // look for new connections, then pass it to worker thread
            while (running) {
                Socket clientSocket = listenSocket.accept();
                Worker c = new Worker(clientSocket);
                workers.add(c);
            }
        } catch (IOException e) {
            System.out.println("Listen :" + e.getMessage());
        }
    }

    /**
     * Cleanly stops looking for new connections and closes open socket connections
     **/
    public void turnOff() {
        this.running = false;
        this.turnOffWorkers();
    }

    /**
     * Clean turns off all the running worker threads
     */
    private void turnOffWorkers() {
        for (Worker work : workers) {
            work.turnOff();
        }
    }

    /**
     * Updates number of nodes if new online node is bigger than current max
     * @param id - new online node id
     */
    private synchronized void newNode(int id){
        if(id > maxNodeNum){
            this.maxNodeNum = id;
        }
    }

    /**
     * Adds new to list of nodes to update other nodes about
     * @param id - new online node id
     */
    private synchronized void newNodeOnline(int id){
        newNodes.add(id + "");
    }

    /**
     * Gets the new online nodes and empties the list
     * @return - list of online nodes
     */
    public synchronized ArrayList<String> getNewNodes(){
        ArrayList<String> temp = newNodes;
        newNodes = new ArrayList<>();
        return temp;
    }

    /**
     * Getter method for maxNodeNum
     * @return - the max number of nodes that can be on in the system
     */
    public synchronized int getMaxNodeNum(){
        return this.maxNodeNum;
    }

    /**
     * Removes offline node
     * @param node - offline node
     */
    private synchronized void nodeRemoved(Node node){
        anchorNode.removeNode(node.getId() + "");

        removedNodes.add(node.getId() + "");
    }

    /**
     * determines if there information to update other nodes with
     * @return - true if new online or offline nodes, false otherwise
     */
    public synchronized boolean update(){
        return !removedNodes.isEmpty() || !newNodes.isEmpty();
    }

    /**
     * Getter method for removedNodes
     * @return -
     */
    public synchronized ArrayList<String> getRemovedNodes() {
        ArrayList <String> temp = removedNodes;
        removedNodes = new ArrayList<>();
        return temp;
    }

    /**
     * Class for Worker threads that handle the communication with clients
     */
    public class Worker extends Thread {
        ObjectInputStream in;
        ObjectOutputStream out;
        Socket clientSocket;

        /**
         * Constructor class for Worker
         * Sets up input and output streams, then starts the thread
         *
         * @param aClientSocket - socket connection to a Client
         **/
        public Worker(Socket aClientSocket) {
            // Make a connection
            try {
                clientSocket = aClientSocket;
                in = new ObjectInputStream(clientSocket.getInputStream());
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                this.start();
            } catch (IOException e) {
                System.out.println("Connection:" + e.getMessage());
            }
        }

        /**
         * Handles communication with client
         **/
        @Override
        public void run() {
            try {
                Object obj = in.readObject();

                if(!(obj instanceof Node)) {
                    login();
                }else {
                    Node node = (Node) obj;
                    obj = in.readObject();
                    if(Config.QUERY.equals(obj)) {
                        // query for actual successors
                        obj = in.readObject();
                        Integer integer = (Integer) obj;
                        int ideal = integer.intValue();

                        out.writeObject(anchorNode.getSuccessor(ideal));

                    } else if(Config.QUIT.equals(obj)){
                        // ping server that node is disconnecting
                        nodeRemoved(node);
                    }
                }

            } catch (EOFException e) {
                System.err.println("EOF:" + e.getMessage());
            } catch (IOException e) {
                System.err.println("IO:" + e.getMessage());
            } catch (ClassNotFoundException e) {
                System.err.println("CLASS:" + e.getMessage());
            } catch (NullPointerException e) {
                System.err.println("NULL: " + e.getMessage());
            } finally {
                turnOff();
            }
        }

        /**
         * Validates a unique node id, than adds the node to list of online nodes
         *
         * @throws IOException
         * @throws ClassNotFoundException
         */
        public void login() throws IOException, ClassNotFoundException {
            // loop till unique username is generated
            Object obj;
            String id;
            do {
                obj = in.readObject();
                id = (String) obj;
                out.writeObject(anchorNode.isOnline(id) + "");
            } while (anchorNode.isOnline(id));

            obj = in.readObject();
            Node node = (Node) obj;

            newNode(Integer.parseInt(id));

            anchorNode.addNode(id, new Connection(node.getIpAddr(), Config.port + node.getId(), node.getId()));

            int nextId = Integer.parseInt(anchorNode.getNext(node.getId()));
            int prevId =  Integer.parseInt(anchorNode.getPrev(node.getId()));

            out.writeObject(new Node(node.getId(), getMaxNodeNum(), node.getIpAddr(), node.getServerIp(), Config.port + node.getId(),nextId , prevId));

            newNodeOnline(Integer.parseInt(id));
        }

        /**
         * Turns off the connection and removes itself from the list of active workers
         */
        public void turnOff() {
            try {
                workers.remove(this);
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
