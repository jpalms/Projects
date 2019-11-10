package edu.rit.cs.controller;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

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
        while (running) {
            /*
            try {
                //todo wait for connection from server

            } catch (IOException e) {
                System.err.println("IO: " + e.getMessage());
                turnOff();
            } catch (ClassNotFoundException e) {
                System.err.println("CLASS: " + e.getMessage());
                turnOff();
            }*/
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

