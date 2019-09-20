package edu.rit.cs.basic_word_count;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class WordCount_Threads {
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
    public static void print_word_count( Map<String, Integer> wordcount, List<String> order){
        for(String word : order){
            System.out.println(word + " : " + wordcount.get(word));
        }
    }

    public static void main(String[] args) {
        List<AmazonFineFoodReview> allReviews = read_reviews(AMAZON_FINE_FOOD_REVIEWS_file);

        /* For debug purpose */
//        for(AmazonFineFoodReview review : allReviews){
//            System.out.println(review.get_Text());
//        }

        MyTimer myTimer = new MyTimer("wordCount");
        myTimer.start_timer();
        /* Tokenize words */
        List<String> words = new ArrayList<String>();
        for(AmazonFineFoodReview review : allReviews) {
            Pattern pattern = Pattern.compile("([a-zA-Z]+)");
            Matcher matcher = pattern.matcher(review.get_Summary());

            while(matcher.find()) {
                words.add(matcher.group().toLowerCase());
            }
        }

        int numThreads = 5;
        int size = words.size();
        ArrayList<countThread> countThreads = new ArrayList<>();
        for (double i = 0.0; i < numThreads; i++) {
            int start = (int)((i/numThreads) * size);
            int end = (int)((((i+1)/numThreads) * size) - 1);
            if(i == (numThreads -1)) {
                end = size;
            }
            countThreads.add(new countThread(words.subList(start, end)));
            countThreads.get((int)i).start();
        }


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

        for (int i = 1; i < countThreads.size(); i++) {
            result = mergeMaps(result, countThreads.get(i).getResult());
            order = sort(order, countThreads.get(i).getOrder());
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

    private static List<String> sort(List<String> base, List<String> extenstion){

        for (String word: extenstion) {
            int size = base.size();
            if(!base.contains(word)){
                for (int i = 0; i < size; i++) {
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
    public static class countThread extends Thread{
        private  List<String> words, order;
        private Map<String, Integer> result;

        public countThread(List<String> words){
            this.words = words;
            order = new ArrayList<>();
        }

        public void run(){
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
