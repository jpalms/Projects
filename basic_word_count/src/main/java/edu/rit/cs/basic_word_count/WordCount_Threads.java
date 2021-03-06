// @author: Justin Palmer
package edu.rit.cs.basic_word_count;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Counts the occurrence of words and sorts them a-z
// Partitions the data with threads for efficiency
public class WordCount_Threads {
    public static final String AMAZON_FINE_FOOD_REVIEWS_file="basic_word_count/amazon-fine-food-reviews/Reviews.csv";

    // Read in Data
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
    public static void print_word_count( Map<String, Integer> wordcount, List<String> order){
        for(String word : order){
            System.out.println(word + " : " + wordcount.get(word));
        }
        System.out.println("map: " + wordcount.size() + " sort: " + order.size());
    }

    // main program to partition and sort word count from a-z
    public static void main(String[] args) {
        List<AmazonFineFoodReview> allReviews = read_reviews(AMAZON_FINE_FOOD_REVIEWS_file);

        MyTimer myTimer = new MyTimer("wordCount");
        myTimer.start_timer();


        int numThreads = 5;
        int size = allReviews.size();
        ArrayList<countThread> countThreads = new ArrayList<>();

        // partitions data into sublists
        for (int i = 0; i < numThreads; i++) {
            int start = (int)((double)i * size/numThreads);
            int end = (int)((i+1.0)* size/numThreads);
            if(i == (numThreads -1)) {
                end = size;
            }
            List partition = new ArrayList<>();

            partition.addAll(allReviews.subList(start, end));

            // generates thread to sort a sublist
            countThreads.add(new countThread(partition));
            countThreads.get(i).start();
        }

        // wait till all threads are done
        boolean alive = true;
        while(alive){
            for (Thread thread: countThreads) {
                if(thread.isAlive()){
                    alive = true;
                    break;
                }else
                    alive = false;
            }
        }

        Map<String, Integer> result = countThreads.get(0).getResult();
        List<String> order = countThreads.get(0).getOrder();

        // merge results and sort
        for (int i = 1; i < countThreads.size(); i++) {
            order = sort(order, countThreads.get(i).getOrder(), result);
            result = mergeMaps(result, countThreads.get(i).getResult());

        }

        myTimer.stop_timer();

        print_word_count(result, order);

        myTimer.print_elapsed_time();
    }

    // function to merge two maps together
    private static Map<String, Integer> mergeMaps(Map<String, Integer> base, Map<String, Integer> extension){
        for (String word: extension.keySet()) {
            // add duplicate word's counts together
            if(base.containsKey(word)){
                int val = base.get(word);
                base.replace(word, val, val + extension.get(word));
            } // add word to combined maps
            else{
                base.put(word, extension.get(word));
            }
        }
        return base;
    }

    // function to combine two sorted lists
    private static List<String> sort(List<String> base, List<String> extenstion, Map<String, Integer> map){
        int i = 0;
        // add words not in the first list to the second list
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

    // Threads that are used to sort partitioned data
    public static class countThread extends Thread{
        private List<AmazonFineFoodReview> allReviews;
        private  List<String> order;
        private Map<String, Integer> result;

        public countThread(List<AmazonFineFoodReview> allReviews){
            this.allReviews = allReviews;
            order = new ArrayList<>();
        }

        public void run(){
            /* Tokenize words */
            List<String> words = new ArrayList<String>();
            for(AmazonFineFoodReview review : allReviews) {
                Pattern pattern = Pattern.compile("([a-zA-Z]+)");
                Matcher matcher = pattern.matcher(review.get_Summary());

                while(matcher.find()) {
                    words.add(matcher.group().toLowerCase());
                }
            }

            count_words(words);
        }

        private void count_words(List<String> words){
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
    }
}
