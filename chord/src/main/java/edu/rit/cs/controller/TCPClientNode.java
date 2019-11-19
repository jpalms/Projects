package edu.rit.cs.controller;

import edu.rit.cs.model.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.EOFException;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Set;

/*
 * Class for handles User connection to the Server
 */
public class  TCPClientNode extends Thread{

    /*
    Class Variables
     */
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean running;
    private Socket s;
    private Node node;
    private ArrayList<Worker> workers;

    /**
     * Constructor Class that connects to controller.Handler, then communicates
     * with controller.Handler.Worker
     * @param addr address of the server to connect to
     */
    public TCPClientNode(String addr) {
            String server_address = addr;
            s = null;
            try {
                // create connection
                int serverPort = Config.port;
                s = new Socket(server_address, serverPort);

                out = new ObjectOutputStream(s.getOutputStream());
                in = new ObjectInputStream(s.getInputStream());

            } catch (UnknownHostException e) {
                System.out.println("Sock:" + e.getMessage());
            } catch (EOFException e) {
                System.out.println("EOF:" + e.getMessage());
            } catch (IOException e) {
                System.out.println("IO:" + e.getMessage());
            }
    }

    /**
     * Overloaded Constructor to use a Connection data structure
     * Creates a client connection to an existing server
     * @param conn Connection data structure
     */
    public TCPClientNode(Connection conn) {
        String server_address = conn.getIpAddr();
        s = null;
        try {
            // create connection
            int serverPort = conn.getPort();
            s = new Socket(server_address, serverPort);

            out = new ObjectOutputStream(s.getOutputStream());
            in = new ObjectInputStream(s.getInputStream());

        } catch (UnknownHostException e) {
            System.out.println("Sock:" + e.getMessage());
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO:" + e.getMessage());
        }
    }


    /**
     * Function to send objects to the Server
     *
     * @param obj - object to send
     */
        public void sendObject(Object obj){
            try {
                out.writeObject(obj);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    /**
     * Function to read objects sent from the Server
     *
     * @return
     */
        public Object readObject(){
            try {
                return in.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return new Object();
        }

    /**
     * Starts a listen server and dispatches all incoming connection
     * to Worker Threads
     */
    public void run() {
        workers = new ArrayList<>();
        try {
            // start server
            int serverPort = node.getPort();
            ServerSocket listenSocket = new ServerSocket(serverPort);
            running = true;
            // look for new connections, then pass it to worker thread
            while (running) {
                //System.out.println("Looking for connections");

                Socket clientSocket = listenSocket.accept();
                Worker c = new Worker(clientSocket);
                workers.add(c);
            }
        } catch (IOException e) {
            System.out.println("Listen :" + e.getMessage());
        }
    }

    /**
     * Calculates the ideal node to store a file in based on a hash
     * Will either place in node, or forward using a helper function
     * @param node Node we are considering to place the file in
     * @param file a File we are transfering
     * @param hopCounter Number of hops before failure (# of fingers)
     */
    public void insertLocation(Node node, File file, int hopCounter){

        // Calculate ideal successor for filename hash
        int destination = (file.hashCode() % node.getTable().getMaxNodes()) + 1;

        // If this is the ideal spot, place it here
        if(this.node.getId() == destination || hopCounter > this.node.getTable().getFingers().size()){
            insert(node, file, hopCounter);
            // node.getStorage().add(file);
        }
        else{
            // Get the next biggest hop connection to the destination from ourselves
            Connection connection = this.node.getTable().getConnectionGivenStartAndDestinationID(this.node.getId(), destination);

            this.turnOff();
            TCPClientNode nextNode = new TCPClientNode(connection);
            nextNode.insertLocation(this.node, file, hopCounter++);
        }
    }

    /**
     * Helper function for insertLocation. Sends the objects over TCP
     * @param node Node we are talking about
     * @param file File to send over network
     * @param hopCounter hops to make over network
     */
    private void insert(Node node, File file, int hopCounter){
        sendObject(Config.INSERT);
        sendObject(file);
        sendObject(hopCounter);
    }

    /**
     * Lookup a file given name of file
     * @param node Node we are at
     * @param name File name to look up
     * @param hopCounter hops to make over network
     * @return File being looked up. Can Return DNE
     */
    public File lookupLocation(Node node, String name, int hopCounter){
        if(hopCounter > this.node.getTable().getFingers().size()){
            return new File("DNE");
        }

        // Calculate ideal successor for filename hash
        int destination = name.length() % this.node.getTable().getMaxNodes() + 1;

        if(this.node.getId() == destination){
            return lookup(node, name, hopCounter);
        }

        // Get the next biggest hop connection to the destination from ourselves
        Connection connection = this.node.getTable().getConnectionGivenStartAndDestinationID(this.node.getId(), destination);

        // Tell the next node to lookup this file and give it back to us
        TCPClientNode nextNode = new TCPClientNode(connection);
        return nextNode.lookupLocation(this.node, name, hopCounter++);
    }

    /**
     * Helper function for lookupLocation. Sends the objects over TCP
     * @param node Node we are talking about
     * @param hash hash of the file we are looking up
     * @param hopCounter hops to make over network
     */
    private File lookup(Node node, String hash, int hopCounter){
        File file;

        sendObject(Config.LOOKUP);
        sendObject(hash);
        sendObject(hopCounter);

        file = (File)readObject();

        return file;
    }

    public Connection query(Node node, int ideal){
        sendObject(node);
        sendObject(Config.QUERY);
        sendObject(new Integer(ideal));

        Connection result = (Connection) readObject();

        return result;
    }

    public void quit(Node node){
        sendObject(node);
        sendObject(Config.QUIT);

        // send number of files to next successor
        sendAllFiles(node);
    }

    private void sendAllFiles(Node node){
        for (File f : node.getStorage()) {
            new TCPClientNode(node.getTable().getFingers().get(0).getActualConnection()).insert(node, f, 0);
            node.getStorage().remove(f);
        }
    }
    public void setNode(Node node) {
        this.node = node;
    }

    /**
     * Returns the user's IP as a String
     * @return user IP
     */
    public String getIpAddr(){
        System.out.println(s.getLocalAddress().toString().substring((1)));
        return s.getLocalAddress().toString().substring(1);
    }

    /**
     * Returns the local port
     * @return local port
     */
    public int getPort(){
        return s.getLocalPort();
    }

    /**
     * Function to turn off this thread
     */
        public void turnOff(){
            running = false;
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
     * Function to turn of first thread which receives objects from the server
     */
    public void turnOffFirst(){
            running = false;
            turnOffWorkers();
            try {
                s.close();
            } catch (IOException e) {
                //e.printStackTrace();
            } catch (NullPointerException e){

            }
    }

    /**
     * Worker Thread. Dispatched to from TCPClientNode
     * Handles commands in a switch case.
     */
    public class Worker extends Thread{
        ObjectInputStream in;
        ObjectOutputStream out;
        Socket clientSocket;
        private boolean loop;

        /**
         * Worker constructor. Assigns the socket given and dispatches to commands.
         * @param clientSocket Socket accepted from TCPClientNode
         */
        public Worker(Socket clientSocket){
            try {
                System.out.println("Connection Made");
                this.clientSocket = clientSocket;

                in = new ObjectInputStream(clientSocket.getInputStream());
                out = new ObjectOutputStream(clientSocket.getOutputStream());

                loop = true;
                this.start();
            } catch (IOException e){
                System.err.println(e.getMessage());
            }
        }

        /**
         *  Function to look for new notification from the server and prints them
         */
        //todo - refactor this to be similar to handler, TCP_Handler
        public void run() {
            while (loop) {

                try {
                    //wait for connection from server

                    String str = (String)in.readObject();

                    if(str.equals(Config.NEW_NODE)){
                        str = (String) in.readObject();

                        System.out.println("New Online Node: " + str);

                        node.rehash(str);
                        for (int i = 0; i < node.getTable().getFingers().size(); i++) {
                            TCPClientNode clientNode = new TCPClientNode(node.getServerIp());
                            Connection conn = clientNode.query(node, node.getTable().getIdealAtIndex(i));
                            node.getTable().setSuccessorAtIndex(i, conn);
                        }

                    } else if(str.equals(Config.REMOVED)){
                        String obj;
                        obj = (String) in.readObject();

                        while(!obj.equals(Config.DONE)){
                            System.out.println("Node has gone offline: " + obj);
                            obj = (String) in.readObject();
                        }

                        for (int i = 0; i < node.getTable().getFingers().size(); i++) {
                            TCPClientNode clientNode = new TCPClientNode(node.getServerIp());
                            Connection conn = clientNode.query(node, node.getTable().getIdealAtIndex(i));
                            node.getTable().setSuccessorAtIndex(i, conn);
                        }

                    } else if(str.equals(Config.INSERT)){
                        File file = (File)in.readObject();

                        System.out.println("Inserting File Check: " + file.getPath());
                        Integer hopCount = (Integer)in.readObject();
                        System.out.println("# Hops in Lookup: " + hopCount);

                    } else if(str.equals(Config.REORDER)) {

                        int newNode = Integer.parseInt((String)in.readObject());
                        int ideal = node.getId();

                        // TODO
                        for(File f: node.getStorage()){
                            if(f.hashCode() % node.getTable().getMaxNodes() != ideal /* and file belongs at newNode*/){
                                node.getStorage().remove(f);
                                insertLocation(node, f, 0);
                            }
                        }

                    } else if(str.equals(Config.LOOKUP)){
                        System.out.println("Looking up File");

                        String hash = (String)in.readObject();
                        Integer hopCount = (Integer)in.readObject();
                        System.out.println("# Hops in Lookup: " + hopCount);

                        sendObject(lookupLocation(node, hash, hopCount++));
                    }

                    turnOff();

                } catch (IOException e) {
                    System.err.println("IO: " + e.getMessage());
                    turnOff();
                } catch (ClassNotFoundException e) {
                    System.err.println("CLASS: " + e.getMessage());
                    turnOff();
                }
            }
        }

        /**
         * Close a socket connection
         */
        public void turnOff(){
            try {
                loop = false;
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

