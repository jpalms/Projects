package edu.rit.cs.basic_word_count;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        //System.out.println("map: " + wordcount.size() + " sort: " + order.size());
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
        ArrayList<WordCount_Cluster_worker> nodes = new ArrayList<>(5);

        Map<String, Integer> result = nodes.get(0).getResult();
        List<String> order = nodes.get(0).getOrder();

        for (int i = 1; i < nodes.size(); i++) {
            order = sort(order, nodes.get(i).getOrder(), result);
            result = mergeMaps(result, nodes.get(i).getResult());

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
}
