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
        myTimer.stop_timer();

        print_word_count(wordcount, order);

        myTimer.print_elapsed_time();
    }

}
