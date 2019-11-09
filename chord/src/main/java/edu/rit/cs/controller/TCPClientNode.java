package edu.rit.cs.controller;

import edu.rit.cs.model.Event;
import edu.rit.cs.model.Topic;
import edu.rit.cs.model.User;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/*
 * Class for handles User connection to the Server
 */
public class TCPClientNode extends Thread{

        private ObjectInputStream in;
        private ObjectOutputStream out;
        private boolean running;
        private Socket s;
        private List<Topic> topicList;
        private List<String> keywords;
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

        /*
         * Same setup as the previous constructor, but also handles in the user login
         */
        public TCPClientNode(String addr, User user, String password) {
            String server_address = addr;
            s = null;
            try {
                // create connection
                int serverPort = 7896;
                s = new Socket(server_address, serverPort);

                out = new ObjectOutputStream(s.getOutputStream());
                in = new ObjectInputStream(s.getInputStream());

                this.autoLogin(user, password);

            } catch (UnknownHostException e) {
                System.out.println("Sock:" + e.getMessage());
            } catch (EOFException e) {
                System.out.println("EOF:" + e.getMessage());
            } catch (IOException e) {
                System.out.println("IO:" + e.getMessage());
            }
        }

    /**
     *  Setter method to set topic list
     *
     * @param topics - list of Topic objects
     */
    private void setTopicList(List<Topic> topics){
            this.topicList = topics;
        }

    /**
     *  Setter method to set keyword list
     *
     * @param keys - list of all unique keywords that relate to a topic
     */
    private void setKeywords(List<String> keys){
            this.keywords = keys;
        }

    /**
     * Getter method to get the topic list
     *
     * @return - list of Topic objects
     */
        public List<Topic> getTopicList() {
            return topicList;
        }

    /**
     * Getter method to get keyword list
     *
     * @return - list of all unique keywords that relate to a topic
     */
        public List<String> getKeywords() {
            return keywords;
        }

    /**
     * Handles the user login
     *
     * @param user - user trying to login
     * @param password - password to login
     * @return - the User that signed in
     */
        private User autoLogin(User user, String password){
            try {
                out.writeObject("false");
                out.writeObject(user.getId());
                in.readObject();

                out.writeObject(password);
                in.readObject();

                this.sendObject("true");

                this.setTopicList((ArrayList<Topic>)this.readObject());
                this.setKeywords((ArrayList<String>) this.readObject());

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e){
                e.printStackTrace();
            }
            return null;
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
        while (running) {
            try {
                if (s.isConnected()) {
                    Object obj = in.readObject();


                    if (obj instanceof Event) {
                        System.out.println(obj + "\n");
                    } else if (obj instanceof Topic) {
                        System.out.println(obj + "\n");
                    }
                }
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

