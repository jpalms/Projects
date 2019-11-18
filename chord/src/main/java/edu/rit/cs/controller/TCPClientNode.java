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

        private ObjectInputStream in;
        private ObjectOutputStream out;
        private boolean running;
        private Socket s;
        private Node node;
        private ArrayList<Worker> workers;
     /*
      * Constructor Class that connects to controller.Handler, then communicates
      * with controller.Handler.Worker
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

    public void insertLocation(Node node, File file, int hopCounter){

        // Calculate ideal successor for filename hash
        int ideal = (file.hashCode() % node.getTable().getMaxNodes()) + 1;

        // If this is the ideal spot, place it here
        if(node.getId() == ideal || hopCounter > node.getTable().getFingers().size()){
            node.getStorage().add(file);
        }
        else{
            // Get the closest ideal table entry to destination node id
            int localIdeal = node.getTable().getTableIdealGivenDestinationIdeal(ideal);

            // Get the connection the the ideal node.
            Connection connection = node.getTable().getSuccessorConnectionGivenIdeal(localIdeal);

            TCPClientNode nextNode = new TCPClientNode(connection);
            nextNode.insert(node, file, hopCounter);
        }
    }

    private void insert(Node node, File file, int hopCounter){
        sendObject(Config.INSERT);
        sendObject(file);
        sendObject(hopCounter);
    }

    public File lookupLocation(Node node, String name, int hopCounter){
        // Check if this node has the file.
        for(File f : node.getStorage()){
            if(f.getFileName().equals(name)){
                return f;
            }
        }

        if(hopCounter > node.getTable().getFingers().size()){
            return new File("DNE");
        }

        // Calculate ideal successor for filename hash
        int ideal = name.length() % node.getTable().getMaxNodes() + 1;

        // Get the closest ideal table entry to destination node id
        int localIdeal = node.getTable().getTableIdealGivenDestinationIdeal(ideal);

        // Get the connection the the ideal node.
        Connection connection = node.getTable().getSuccessorConnectionGivenIdeal(localIdeal);

        // Tell the next node to lookup this file and give it back to us
        TCPClientNode nextNode = new TCPClientNode(connection);
        return nextNode.lookup(node, name, hopCounter);
    }

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

        // send number of files to server, in preparation
        sendObject(new Integer(node.getStorage().size()));
        // send files to server for rehashing, remove from node
        for (File f : node.getStorage()) {
            sendObject(f);
            node.getStorage().remove(f);
        }
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public String getIpAddr(){
        System.out.println(s.getLocalAddress().toString().substring((1)));
        return s.getLocalAddress().toString().substring(1);
    }

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
     * Function to turn of first thread which recieves objects from the server
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

    public class Worker extends Thread{
        ObjectInputStream in;
        ObjectOutputStream out;
        Socket clientSocket;
        private boolean loop;

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

                        int ideal = Integer.parseInt(str);

                        for(File f: node.getStorage()){
                            if(f.hashCode() % node.getTable().getMaxNodes() != ideal){
                                node.getStorage().remove(f);
                                insertLocation(node, f, 0);
                            }
                        }

                    } else if(str.equals(Config.REMOVED)){
                        Set<String> removed = (Set<String>)in.readObject();
                        for(String r: removed){
                            System.out.println("Node has gone offline: " + r);
                        }

                        Object obj = in.readObject();
                        File f;
                        while(!(obj instanceof String)){
                            f = (File)obj;
                            System.out.println("File added to Node: " + f.getPath());
                            node.getStorage().add(f);
                            in.readObject();
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

                        insertLocation(node,file, hopCount+1);
                    } else if(str.equals(Config.LOOKUP)){
                        System.out.println("Looking up File");

                        String hash = (String)in.readObject();
                        Integer hopCount = (Integer)in.readObject();
                        System.out.println("# Hops in Lookup: " + hopCount);

                        sendObject(lookupLocation(node, hash, hopCount+1));
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

