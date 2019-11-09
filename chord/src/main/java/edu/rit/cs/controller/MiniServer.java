package edu.rit.cs.controller;

import edu.rit.cs.model.Config;
import edu.rit.cs.model.Connection;
import edu.rit.cs.model.Node;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Class to Handle the creations of all threads that communicate with clients
 **/
public class MiniServer extends Thread {
    private ArrayList<Worker> workers = new ArrayList<>();
    boolean running;
    private TreeMap<String, Connection> sockets;
    private int maxNodeNum;
    private AnchorNode anchorNode;

    /**
     * Constructor class for Handler, initialize onlineUsers maps
     **/
    public MiniServer(AnchorNode anchorNode) {
        this.anchorNode = anchorNode;
        this.running = true;
        this.maxNodeNum = 0;
    }

    /**
     * Starts a notify thread, and continuously hits new connections
     **/
    public void run() {
        workers = new ArrayList<>();
        sockets = new TreeMap<>();
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
     * Helper function to get number of worker thread nodes
     *
     * @return		size of ArrayList workers
     */
    public int getWorkersSize() {
        return workers.size();
    }

    /**
     * Shows workers and removes ones that are no longer alive.
     *
     * @return		returns alive worker threads
     */
    public ArrayList<Worker> getWorkers() {
        ArrayList<Worker> info = new ArrayList<>();
        for (int i = 0; i < workers.size() ; i++) {
            if (workers.get(i) != null && !workers.get(i).isAlive()) {
                info.add(workers.get(i));
                workers.remove(workers.get(i));
            }
        }
        return info;
    }

    /**
     * Gets size of the sockets list
     *
     * @return		int, size of the sockets list
     */
    public synchronized int getSocketsSize(){
        return sockets.size();
    }

    /**
     * Gets hashmap of sockets
     *
     * @return		hashmap of sockets
     */
    public TreeMap<String, Connection> getSockets(){
        return sockets;
    }

    /**
     * Clean turns off all the running worker threads
     */
    private void turnOffWorkers() {
        for (Worker work : workers) {
            work.turnOff();
        }
    }

    private synchronized void newNode(int id){
        if(id > maxNodeNum){
            this.maxNodeNum = id;
        }
    }

    private synchronized boolean isOnline(String id){
        return sockets.containsKey(id);
    }

    private synchronized int getMaxNodeNum(){
        return this.maxNodeNum;
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
                //System.out.println("Made a connection");
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
                    boolean query = in.readObject().equals("true");
                    if(query) {
                        // query for actual successors
                        Node node = (Node) obj;
                        obj = in.readObject();
                        Integer integer = (Integer) obj;
                        int ideal = integer.intValue();

                        if (getSocketsSize() == 1) {
                            out.writeObject(obj);
                        } else {
                            TreeMap<String, Connection> tree = getSockets();
                            String key = tree.higherKey(ideal + "");
                            if (key != null) {
                                out.writeObject(key);
                            } else {
                                out.writeObject(tree.firstKey());
                            }
                        }
                    } else{
                        // file stuff

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
            }
        }

        /**
         * Validates a unique username, than adds the new user to list of subscribers
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
                out.writeObject(isOnline(id) + "");
            } while (isOnline(id));

            obj = in.readObject();
            Node node = (Node) obj;

            sockets.put(id, new Connection(node.getIpAddr(), node.getPort()));

            out.writeObject(new Node(node.getId(), getMaxNodeNum(), node.getIpAddr(), node.getPort()));
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
