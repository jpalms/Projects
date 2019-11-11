package edu.rit.cs.controller;

import edu.rit.cs.model.Config;
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
     /*
      * Constructor Class that connects to controller.Handler, then communicates
      * with controller.Handler.Worker
      */
    public TCPClientNode(String addr) {
            String server_address = addr;
            s = null;
            try {
                // create connection
                int serverPort = 7896;
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
        int serverPort = Config.port;
        while (running) {

            try {
                //todo wait for connection from server
                ServerSocket listenSocket = new ServerSocket(serverPort);

                Socket clientSocket = listenSocket.accept();

                out = new ObjectOutputStream(s.getOutputStream());
                in = new ObjectInputStream(s.getInputStream());

                String str = (String)in.readObject();

                if(str.equals("newNode")){
                    str = (String) in.readObject();

                    System.out.println("New Online Node: " + str);

                    in.readObject();
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
                        in.readObject();
                    }
                } else if(str.equals("insert")){
                    File file = (File)in.readObject();
                    System.out.println("Inserting File: " + file.toPath());
                    // todo add file to node
                } else if(str.equals("lookup")){
                    String hash = (String)in.readObject();
                    // todo get file from node
                }

            } catch (IOException e) {
                System.err.println("IO: " + e.getMessage());
                //turnOff();
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

        String ipAddr = (String)readObject();

        TCPClientNode thread = new TCPClientNode(ipAddr);

        thread.insert(file);
    }

    private void insert(File file){
        sendObject("insert");
        sendObject(file);
    }

    public File lookupLocation(Node node, String hash){
        sendObject(node);
        sendObject("file");
        sendObject("lookup");

        sendObject(hash);

        String ipAddr = (String)readObject();

        TCPClientNode thread = new TCPClientNode(ipAddr);

        return thread.lookup(hash);
    }

    private File lookup(String hash){
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

