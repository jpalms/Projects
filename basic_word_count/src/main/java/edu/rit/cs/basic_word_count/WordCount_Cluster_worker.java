// @author: Justin Palmer
package edu.rit.cs.basic_word_count;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.net.*;
import java.io.*;

// Does the work to a sublist given by the server
public class WordCount_Cluster_worker {

    private List<AmazonFineFoodReview> allReviews;
    private  List<String> order;
    private Map<String, Integer> result;
    private List<String> words;

    public WordCount_Cluster_worker(List<AmazonFineFoodReview> allReviews){
        this.allReviews = allReviews;
    }

    // Read in Data
    private void setWords(){
        List<String> words = new ArrayList<String>();
        for(AmazonFineFoodReview review : allReviews) {
            Pattern pattern = Pattern.compile("([a-zA-Z]+)");
            Matcher matcher = pattern.matcher(review.get_Summary());

            while(matcher.find()) {
                words.add(matcher.group().toLowerCase());
            }
        }

        this.words = words;
    }

    private void count_words(){
        //        /* Count words */
        Map<String, Integer> wordcount = new HashMap<>();
        List<String> order = new ArrayList<>();
        for(String word : words) {
            if(!wordcount.containsKey(word)) {
                wordcount.put(word, 1);
                // sort words
                int size = order.size();
                for (int i = 0; i < size ; i++) {
                    if(order.get(i).compareTo(word) > 0){
                        order.add(i,word);
                        break;
                    }
                    else if( i == (size - 1)){
                        order.add(word);
                    }
                }
                if(size == 0){
                    order.add(word);
                }
            } else{
                int init_value = wordcount.get(word);
                wordcount.replace(word, init_value, init_value+1);
            }
        }

        this.result = wordcount;
        this.order = order;
    }

    public Map<String, Integer> getResult(){
        return result;
    }

    public List<String> getOrder(){
        return order;
    }

    // Establish a connection with Server and gets the data partition to sort
    public static class TCPClient{

        public static void main(String [] args) {
            String server_address = args[0];
            ObjectInputStream in;
            ObjectOutputStream out;

            Socket s = null;
            try {
                // create connection
                int serverPort = 7896;
                s = new Socket(server_address, serverPort);

                out = new ObjectOutputStream(s.getOutputStream());
                in = new ObjectInputStream(s.getInputStream());

                // get data
                Object obj = in.readObject();
                List<AmazonFineFoodReview> partition = (List<AmazonFineFoodReview>) obj;

                //System.out.println(partition.size());

                // sort data
                WordCount_Cluster_worker cluster = new WordCount_Cluster_worker(partition);

                cluster.setWords();
                cluster.count_words();

                // send back result
                out.writeObject(cluster.getResult());
                out.writeObject(cluster.getOrder());

            } catch (UnknownHostException e) {
                System.out.println("Sock:" + e.getMessage());
            } catch (EOFException e) {
                System.out.println("EOF:" + e.getMessage());
            } catch (IOException e) {
                System.out.println("IO:" + e.getMessage());
            } catch (ClassNotFoundException e) {
                System.out.println("CLASS:" + e.getMessage());
            } finally {
                if (s != null)
                    try {
                        s.close();
                    } catch (IOException e) {
                        System.out.println("close:" + e.getMessage());
                    }
            }
        }
    }
}
