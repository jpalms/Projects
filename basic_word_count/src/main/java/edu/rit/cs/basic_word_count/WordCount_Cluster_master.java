package edu.rit.cs.basic_word_count;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.net.*;
import java.io.*;

public class WordCount_Cluster_master {
    public static final String AMAZON_FINE_FOOD_REVIEWS_file="basic_word_count/amazon-fine-food-reviews/Reviews.csv";

    public static List<AmazonFineFoodReview> read_reviews(String dataset_file) {
        List<AmazonFineFoodReview> allReviews = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(dataset_file))){
            String reviewLine = null;
            // read the header line
            reviewLine = br.readLine();

            //read the subsequent lines
            while ((reviewLine = br.readLine()) != null) {
                allReviews.add(new AmazonFineFoodReview(reviewLine));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return allReviews;
    }


    // prints word count in a-z order
    public static void print_word_count(Map<String, Integer> wordcount, List<String> order){
        for(String word : order){
            System.out.println(word + " : " + wordcount.get(word));
        }
        System.out.println("map: " + wordcount.size() + " sort: " + order.size());
    }

    public static void main(String[] args) {
        List<AmazonFineFoodReview> allReviews = read_reviews(AMAZON_FINE_FOOD_REVIEWS_file);

        /* For debug purpose */
//        for(AmazonFineFoodReview review : allReviews){
//            System.out.println(review.get_Text());
//        }

        MyTimer myTimer = new MyTimer("wordCount");
        myTimer.start_timer();

        // insert partition and docker nodes

        int numNodes = 5;
        int size = allReviews.size();
        List nodeParam = new ArrayList<>();
        ArrayList<WordCount_Cluster_worker> countThreads = new ArrayList<>();
        for (int i = 0; i < numNodes; i++) {
            int start = (int)((double)i * size/numNodes);
            int end = (int)((i+1.0)* size/numNodes);
            if(i == (numNodes -1)) {
                end = size;
            }
            List partition = new ArrayList<>();
            partition.addAll(allReviews.subList(start, end));
            nodeParam.add(partition);

            //countThreads.add(new countThread(partition));
            //countThreads.get(i).start();
        }

        TCPServer server = new TCPServer(nodeParam);

        ArrayList<Map<String, Integer>> maps = server.getMaps();
        ArrayList<List<String>> sortedLists = server.getSortedLists();

        Map<String, Integer> result = maps.get(0);
        List<String> order = sortedLists.get(0);

        for (int i = 1; i < maps.size(); i++) {
            order = sort(order, sortedLists.get(i), result);
            result = mergeMaps(result, maps.get(i));

        }

        myTimer.stop_timer();

        print_word_count(result, order);

        myTimer.print_elapsed_time();
    }

    private static Map<String, Integer> mergeMaps(Map<String, Integer> base, Map<String, Integer> extension){
        for (String word: extension.keySet()) {
            if(base.containsKey(word)){
                int val = base.get(word);
                base.replace(word, val, val + extension.get(word));
            }
            else{
                base.put(word, extension.get(word));
            }
        }
        return base;
    }

    private static List<String> sort(List<String> base, List<String> extenstion, Map<String, Integer> map){
        //List<String> arr = new ArrayList<>(base.size() + extenstion.size());
        int i = 0;
        for (String word: extenstion) {
            int size = base.size();
            if(!map.containsKey(word)){
                for (; i < size; i++) {
                    if(base.get(i).compareTo(word) > 0){
                        base.add(i,word);
                        break;
                    }
                    else if( i == (size - 1)){
                        base.add(word);
                    }
                }
            }
        }

        return base;
    }

    public static class TCPServer {

        List nodeParam;
        ArrayList<Map<String, Integer>> maps = new ArrayList<>();
        ArrayList<List<String>> sortedLists = new ArrayList<>();
        public TCPServer(List nodeParam) {
            this.nodeParam = nodeParam;
            try {
                int serverPort = 7896;
                ServerSocket listenSocket = new ServerSocket(serverPort);
                System.out.println("TCP Server is running and accepting client connections...");

                while (!nodeParam.isEmpty()) {
                    Socket clientSocket = listenSocket.accept();
                    Connection c = new Connection(clientSocket, (List<AmazonFineFoodReview>) nodeParam.remove(0));
                    if(!c.isAlive()) {
                        maps.add(c.getMap());
                        sortedLists.add(c.getOrder());
                    }
                }
            } catch (IOException e) {
                System.out.println("Listen :" + e.getMessage());
            }
        }

        public ArrayList<Map<String, Integer>> getMaps() {
            return maps;
        }

        public ArrayList<List<String>> getSortedLists() {
            return sortedLists;
        }
    }

    static class Connection extends Thread {
        ObjectInputStream in;
        ObjectOutputStream out;
        Socket clientSocket;
        Map<String, Integer> wordcount;
        List<String> order;

        public Connection(Socket aClientSocket, List<AmazonFineFoodReview> partition) {
            try {
                System.out.println("Made a connection");
                clientSocket = aClientSocket;
                in = new ObjectInputStream(clientSocket.getInputStream());
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                this.run(partition);
            } catch (IOException e) {
                System.out.println("Connection:" + e.getMessage());
            }
        }

        public void run(List<AmazonFineFoodReview> partition) {
            try {   // send word to node, retrieve result

                System.out.println("Send");
                out.writeObject((Object)partition);
                System.out.println("Read");
                wordcount = (Map<String, Integer>)in.readObject();
                order = (List<String>) in.readObject();
            } catch (EOFException e) {
                System.out.println("EOF:" + e.getMessage());
            } catch (IOException e) {
                System.out.println("IO:" + e.getMessage());
            } catch (ClassNotFoundException e){
                System.out.println("CLASS:" + e.getMessage());
            } catch (NullPointerException e){
                System.out.println("NULL: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {/*close failed*/}
            }
        }

        public Map<String, Integer> getMap(){
            return wordcount;
        }

        public List<String> getOrder(){
            return order;
        }
    }

}
