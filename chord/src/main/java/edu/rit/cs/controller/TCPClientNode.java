package edu.rit.cs.controller;

import edu.rit.cs.model.Config;
import edu.rit.cs.model.Connection;
import edu.rit.cs.model.Node;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
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

    /**
     *  Function to look for new notification from the server and prints them
     */
    public void run() {
        running = true;
        int serverPort = node.getPort();
        while (running) {

            try {
                //wait for connection from server
                ServerSocket listenSocket = new ServerSocket(serverPort);

                Socket clientSocket = listenSocket.accept();

                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());

                String str = (String)in.readObject();

                if(str.equals("newNode")){
                    str = (String) in.readObject();

                    System.out.println("New Online Node: " + str);
                } else if(str.equals("removed")){
                    Set<String> removed = (Set<String>)in.readObject();
                    for(String r: removed){
                        System.out.println("Node has gone offline: " + r);
                    }

                    Object obj = in.readObject();
                    File f;

                    while(!(obj instanceof String)){
                        f = (File)obj;
                        System.out.println("File added to Node: " + f.toPath());
                        node.getStorage().add(f);
                        in.readObject();
                    }
                } else if(str.equals("insert")){
                    File file = (File)in.readObject();
                    System.out.println("Inserting File: " + file.toPath());
                    node.getStorage().add(file);
                } else if(str.equals("lookup")){
                    String hash = (String)in.readObject();
                    File temp = new File(hash);
                    for (File f : node.getStorage()){
                        if (f.equals(temp)){
                            sendObject(f);
                            break;
                        }
                    }

                }
                clientSocket.close();

            } catch (IOException e) {
                System.err.println("IO: " + e.getMessage());
                turnOff();
            } catch (ClassNotFoundException e) {
                System.err.println("CLASS: " + e.getMessage());
                //turnOff();
            }
        }
    }

    public void insertLocation(Node node, File file){
        sendObject(node);
        sendObject("file");
        sendObject("insert");

        sendObject(file);

        Connection conn = (Connection) readObject();

        System.out.println(conn.toString());
        if(conn.getNodeId() == node.getId()){
            node.getStorage().add(file);
        } else {
            TCPClientNode thread = new TCPClientNode(conn);

            thread.insert(node, file);
        }
    }

    private void insert(Node node, File file){
        sendObject("insert");
        sendObject(file);
    }

    public File lookupLocation(Node node, String hash){
        sendObject(node);
        sendObject("file");
        sendObject("lookup");

        sendObject(hash);

        Connection conn = (Connection)readObject();

        System.out.println(conn.toString());
        if(conn.getNodeId() == node.getId()) {
            for (File f : node.getStorage()){
                if (f.getName().equals(hash)){
                    return f;
                }
            }
        }
        TCPClientNode thread = new TCPClientNode(conn);

        return thread.lookup(node, hash);
    }

    private File lookup(Node node, String hash){
        File file;

        sendObject("lookup");
        sendObject(hash);

        file = (File)readObject();

        return file;
    }

    public String query(Node node, int ideal){
        sendObject(node);
        sendObject("query");
        sendObject(new Integer(ideal));

        String result = (String) readObject();

        return result;
    }

    public void quit(Node node){
        sendObject("quit");

        // send number of files to server, in preparation
        sendObject(node.getStorage().size());
        // send files to server for rehashing, remove from node
        for (File target : node.getStorage()) {
            sendObject(target);
            node.getStorage().remove(target);
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
     * Function to turn of first thread which recieves objects from the server
     */
    public void turnOffFirst(){
            running = false;
            try {
                s.close();
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
    }

