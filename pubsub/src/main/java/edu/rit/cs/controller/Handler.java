package edu.rit.cs.controller;

import edu.rit.cs.model.Event;
import edu.rit.cs.model.Topic;
import edu.rit.cs.model.User;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class to Handle the creations of all threads that communicate with clients
 **/
public class Handler extends Thread {
    private ArrayList<Worker> workers = new ArrayList<>();
    boolean running;
    private HashMap<String, Worker> sockets;
    private EventManager em;

    /**
     * Constructor class for Handler, initialize onlineUsers maps
     **/
    public Handler(EventManager eventManager) {
        this.em = eventManager;
        running = true;
    }

    /**
     * Starts a notify thread, and continuously hits new connections
     **/
    public void run() {
        workers = new ArrayList<>();
        sockets = new HashMap<>();
        try {
            // start server
            int serverPort = 7896;
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
    public int getSocketsSize(){
        return sockets.size();
    }

    /**
     * Gets hashmap of sockets
     *
     * @return		hashmap of sockets
     */
    public HashMap<String, Worker> getSockets(){
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

    /**
     * Class for Worker threads that handle the communication with clients
     */
    public class Worker extends Thread {
        ObjectInputStream in;
        ObjectOutputStream out;
        Socket clientSocket;
        boolean running = false;
        String username = "";
        ArrayList<Object> eventsToSend = new ArrayList<>();
        List<Object> newTopics = new ArrayList<>();
        private Object info = new Object();

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
                boolean newUser = in.readObject().equals("true");
                if (newUser) {
                    newLogin();
                }
                if (login()) {
                    User user = em.getUser(username);
                    out.writeObject(user);
                    boolean sending = in.readObject().equals("true");
                    if (sending) {

                        out.writeObject(em.getTopicList());
                        out.writeObject(em.getKeywords());

                        if (user.isPub()) {
                            receivedFromPub();
                        } else if (user.isSub()) {
                            receivedFromSub(user);
                        }
                        this.clientSocket.close();
                    } else {
                        em.on_offlineUser(username, user, true);
                        sockets.put(username, this);
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
        public void newLogin() throws IOException, ClassNotFoundException {
            // loop till unique username is generated
            Object obj;
            String id;
            do {
                obj = in.readObject();
                id = (String) obj;
                out.writeObject(em.userExists(id) + "");
            } while (em.userExists(id));

            obj = in.readObject();
            User user = (User) obj;
            em.addUser(user);
            if (user.isSub())
                em.add_removeSub(user, true);
        }

        /**
         * @return true if username is in allUsers and the password matches the user
         * @throws IOException
         * @throws ClassNotFoundException
         **/
        public boolean login() throws IOException, ClassNotFoundException {
            String id, password;
            Object obj;
            //login
            do {
                obj = in.readObject();
                id = (String) obj;
                out.writeObject(em.userExists(id) + "");
            } while (!em.userExists(id));

            obj = in.readObject();
            password = (String) obj;
            setUsername(id);

            return em.userExists(id) && em.getUser(id).isCorrectPassord(password);
        }

        /**
         * Sets the username for this connections user
         *
         * @param username - unique String id for a User
         */
        public void setUsername(String username) {
            this.username = username;
        }

        /**
         * returns Object instance of Event or Topic to publish/advertise
         *
         * @return - Object instance of Event or Topic
         */
        public synchronized Object newInfo() {
            return info;
        }

        /**
         * adds events to a list of events to send
         *
         * @param events - list of events to send to Subscriber
         * @throws IOException
         **/
        public synchronized void queueEvents(ArrayList<Object> events) throws IOException {
            for(Object e: events)
                eventsToSend.add(e);
        }

        /**
         * adds events to a list of events to send
         *
         * @param topics - list of Topics to advertise
         * @throws IOException
         **/
        public synchronized void queueTopics(ArrayList<Object> topics) throws IOException {
            for(Object t: topics)
                newTopics.add(t);
        }

        /**
         * Takes given values and places it in notify lists
         *
         * @param objects		this arraylist of stuff to be added to a queue
         * @throws IOException	thrown when IO functions error
         */
        public synchronized void queueBoth(ArrayList<Object> objects) throws IOException {
            ArrayList<Object> events = new ArrayList<>();
            ArrayList<Object> topicArrayList = new ArrayList<>();
            for (Object obj : objects) {
                if (obj instanceof Event) {
                    Event e = (Event) obj;
                    events.add(e);
                } else {
                    Topic t = (Topic) obj;
                    topicArrayList.add(t);
                }
            }

            queueEvents(events);
            queueTopics(topicArrayList);
        }

        /**
         * Reading input from a Publisher
         *
         * @throws IOException
         * @throws ClassNotFoundException
         */
        public void receivedFromPub() throws IOException, ClassNotFoundException {
            Object obj;
            obj = in.readObject();
            if (obj instanceof Event) {
                Event e = (Event) obj;
                em.notifySubscribers(e);
                this.info = e;
            } else if (obj instanceof Topic) {
                Topic t = (Topic) obj;
                em.addTopic(t);
                this.info = t;
            }
        }

        /**
         * Reading input from Subscriber
         *
         * @param user
         * @throws IOException
         * @throws ClassNotFoundException
         */
        public void receivedFromSub(User user) throws IOException, ClassNotFoundException {
            Object obj;
            obj = in.readObject();
            boolean subOrUnsubAction = in.readObject().equals("true");
            boolean listOrUnsubAll = in.readObject().equals("true");
            if (obj instanceof Topic) {
                Topic t = (Topic) obj;
                if (subOrUnsubAction) {
                    if (listOrUnsubAll) {
                        out.writeObject(em.getSubscribedTopics(user));
                    } else {
                        em.subUnsubTopic(user, t, true);
                    }
                } else {
                    if (listOrUnsubAll) {
                        em.subUnsubTopic(user, "", false);
                        em.unSubscribeFromAll(user);
                    } else {
                        em.subUnsubTopic(user, t, false);
                    }
                }
            } else if (obj instanceof String) {
                String key = (String) obj;
                em.subUnsubTopic(user, key, true);
            }
        }

        /**
         * helper functions to handle sending info back and forth between TCP and EM
         *
         * @throws IOException
         */
        public void sendObj() throws IOException {
            while(!eventsToSend.isEmpty()){
                out.writeObject(eventsToSend.remove(0));
            }
            while(!newTopics.isEmpty()){
                out.writeObject(newTopics.remove(0));
            }
        }
        /**
         * Turns off the connection and removes itself from the list of active workers
         */
        public void turnOff() {
            try {
                workers.remove(this);
                em.on_offlineUser(username, null, false);
                clientSocket.close();
                running = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
