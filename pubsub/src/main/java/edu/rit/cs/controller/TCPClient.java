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
import java.util.Stack;

public class TCPClient extends Thread{

        private ObjectInputStream in;
        private ObjectOutputStream out;
        private boolean running;
        private Socket s;
        private List<Topic> topicList;
        private List<String> keywords;
        private ArrayList<Object> updates = new ArrayList<>();

    public TCPClient(String addr) {
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

        public TCPClient(String addr, User user, String password) {
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

        private void setTopicList(List<Topic> topics){
            this.topicList = topics;
        }

        private void setKeywords(List<String> keys){
            this.keywords = keys;
        }

        public List<Topic> getTopicList() {
            return topicList;
        }

        public List<String> getKeywords() {
            return keywords;
        }

        public void autoLogin(User user, String password){
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
        }

        public void sendObject(Object obj){
            try {
                out.writeObject(obj);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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

        public void sendBool(boolean bool){
            try {
                out.writeBoolean(bool);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public boolean readBool(){
            try {
                return in.readBoolean();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        public void run(){
            this.receiver();
        }
        public void receiver(){
            running = true;
            while (running){
                try {
                    if(s.isConnected()) {
                        Object obj = in.readObject();

                        if (obj instanceof Event) {
                            updates.add((Event) (obj));

                        } else if (obj instanceof Topic) {
                            updates.add((Topic) (obj));
                        }
                    }
                } catch (IOException e){
                    System.err.println("IO: " + e.getMessage());
                } catch (ClassNotFoundException e){
                    System.err.println("CLASS: " + e.getMessage());
                }
            }
        }

        public void turnOff(){
            running = false;
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void turnOffFirst(){
            running = false;
            try {
                s.close();
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }

        public ArrayList<Object> getUpdates() {
            return updates;
        }

        public void emptyUpdates() {
            updates = new ArrayList<>();
        }
    }

