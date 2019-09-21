package edu.rit.cs.basic_word_count;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordCount_Cluster_worker {

    private List<AmazonFineFoodReview> allReviews;
    private  List<String> order;
    private Map<String, Integer> result;

    private List<String> getWords(){
        List<String> words = new ArrayList<String>();
        for(AmazonFineFoodReview review : allReviews) {
            Pattern pattern = Pattern.compile("([a-zA-Z]+)");
            Matcher matcher = pattern.matcher(review.get_Summary());

            while(matcher.find()) {
                words.add(matcher.group().toLowerCase());
            }
        }

        return words;
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
