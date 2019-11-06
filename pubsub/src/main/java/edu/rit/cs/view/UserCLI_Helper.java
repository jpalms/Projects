package edu.rit.cs.view;

import edu.rit.cs.controller.TCPClient;
import edu.rit.cs.model.Event;
import edu.rit.cs.model.Topic;
import edu.rit.cs.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserCLI_Helper {

    private ArrayList<TCPClient> connections;
    private User user;
    private String server, password;

    public UserCLI_Helper(User user, String server, String password){
        connections = new ArrayList<>();
        this.user = user;
        this.server = server;
        this.password = password;
    }

    public void turnOff(){
        while (connections.size() > 0) {
            connections.get(0).turnOff();
            connections.remove(0);
        }
    }

    /**
     * Prints all the topics in the list
     */
    private void printTopicList(List<Topic> topicList){
        System.out.println("Topics:");
        for(Topic topic: topicList){
            if(!topic.getName().equals(""))
                System.out.println("\n" + topic.toString());
        }
    }

    /**
     * Prints all the keys in the list
     */
    private void printKeyList(List<String> keyList){
        System.out.println("Keys:");
        for(String key: keyList){
            System.out.println("\n\t" + key);
        }
    }
    /*
     * --------------------Publisher-------------------------
     */
    /**
     * Function to advertise a Topic within EventManager.
     * Takes in the User object associated with the node.
     * No return, but creates a new Topic, handled by the EventManager.
     *
     */
    public void advertise() {
        Scanner advertise = new Scanner (System.in);

        System.out.println("Name of your topic: ");
        String t_name = advertise.nextLine();

        TCPClient thread = new TCPClient(server, user, password);

        List<Topic> topicList = thread.getTopicList();
        List<String> keywords = thread.getKeywords();

        List<String> key_list = new ArrayList<String>();
        String keyword;
        do {
            System.out.println("Input a keyword, or an empty string if you are done.");
            keyword = advertise.nextLine();
            if (!(keyword.equals(""))){
                key_list.add(keyword);
            }
        } while(!(keyword.equals("")));

        Topic new_topic = new Topic(key_list, t_name);

        thread.sendObject(new_topic);

        connections.add(thread);
        //currUser.advertise(new_topic);
    }

    /**
     * Function to publish an Event for use in EventManager.
     * Takes in the User object associated with the node.
     * No return, but creates a new Event, handled by the EventManager.
     *
     */
    public void publish() {
        Scanner publish = new Scanner (System.in);

        TCPClient thread = new TCPClient(server, user, password);

        connections.add(thread);

        List<Topic> topicList = thread.getTopicList();
        List<String> keywords = thread.getKeywords();

        printTopicList(topicList);

        Topic topic = null;
        String e_title = "";
        if (topicList.size() >= 1) {
            System.out.println("Title of your event: ");
            e_title = publish.nextLine();
            while (topic == null) {
                System.out.println("Enter Topic: ");
                String topic_str = publish.nextLine();

                for (Topic top : topicList) {
                    if (top.getName().equals(topic_str)) {
                        System.out.println("Valid topic");
                        topic = top;
                        break;
                    }
                }
                if (topic == null) {
                    System.out.println("Input does not match available options.\n" +
                            "Try again\n");
                }

            }
            System.out.println("Content for your event: \n");
            String e_content = publish.nextLine();

            Event new_event = new Event( topic, e_title, e_content);

            thread.sendObject(new_event);
        } else {
            System.out.println("No topics available; returning to main command prompt...\n");
        }
    }

    /*
     * -------------------------Subscribers------------------------------------------
     */

    /**
     * Function to subscribe to a Topic.
     * Takes in the User object associated with the node.
     * No return, but adds the User to a 'subscribed' list for the Topic.
     *
     */
    public void subcribe() {
        Scanner subscribe = new Scanner(System.in);
        System.out.println("Choose: subscribe by Topic (\"t\") " +
                "or by keyword (\"k\")\n");
        String sub_imp = subscribe.nextLine();

        TCPClient thread = new TCPClient(server, user, password);

        connections.add(thread);

        List<Topic> topicList = thread.getTopicList();
        List<String> keywords = thread.getKeywords();

        if (sub_imp.equals("t")) {
            System.out.println("What topic would you like to subscribe to?\n");
            Topic topic = null;
            printTopicList(topicList);
            while (topic == null) {
                String topic_str = subscribe.nextLine();

                for (Topic top : topicList) {
                    if (top.getName().equals(topic_str)) {
                        topic = top;
                        break;
                    }
                }
                if(topic == null) {
                    System.out.println("Input does not match available options.\n" +
                            "Try again\n");
                }
            }

            thread.sendObject(topic);
            thread.sendObject("true");
            thread.sendObject("false");
        } else if (sub_imp.equals("k")){
            printKeyList(keywords);
            System.out.println("Please enter a keyword that you would like to search by: ");
            String keyword = subscribe.nextLine();
            boolean match = false;

            while (!match) {
                for (String key : keywords) {
                    if (key.equals(keyword)) {
                        match = true;
                        break;
                    }
                }
                if(!match){
                    System.out.println("Input does not match available options.\n" +
                            "Try again\n");
                }
            }
            thread.sendObject(keyword);
            thread.sendObject("false");
            thread.sendObject("false");
        } else {
            System.out.println("Input does not match available options.\n" +
                    "Returning to command list...\n");
        }
    }
         /**
         * Function to unsubscribe from a topic.
         * Takes in the User object associated with the node.
         * No return, but removes the User from a 'subscribed' list for the Topic.
         * (Can also remove User from all currently subscribed nodes.)
         *
         */
         public void unSubscribe(){
            Scanner unsubscribe = new Scanner(System.in);
            System.out.println("Would you like to unsubscribe from all topics (\"a\")" +
                    "one just one (\"o\") ?\n");
            String unsub_imp = unsubscribe.nextLine();
            if (unsub_imp.equals("a")){
                System.out.println("Removing all subscriptions...\n");
                TCPClient thread = new TCPClient(server, user, password);

                connections.add(thread);

                List<Topic> topicList = thread.getTopicList();
                List<String> keywords = thread.getKeywords();

                thread.sendObject(new Topic(new ArrayList<>(), ""));
                thread.sendObject("false");
                thread.sendObject("true");
            } else if (unsub_imp.equals("o")){
                System.out.println("What topic would you like to unsubscribe from?\n");

                TCPClient thread = new TCPClient(server, user, password);

                connections.add(thread);

                List<Topic> topicList = thread.getTopicList();
                List<String> keywords = thread.getKeywords();

                printTopicList(topicList);

                Topic topic = null;
                while (topic == null) {
                    String topic_str = unsubscribe.nextLine();
                    for (Topic top : topicList) {
                        if (top.getName().equals(topic_str)) {
                            topic = top;
                            break;
                        }
                    }
                    if(topic == null) {
                        System.out.println("Input does not match available options.\n" +
                                "Try again\n");
                    }
                }

                thread.sendObject(topic);
                thread.sendObject("false");
                thread.sendObject("false");
            } else {
                System.out.println("Input does not match available options.\n" +
                        "Returning to command list...\n");
            }
         }

    public void listSubscriptions(){
        TCPClient thread = new TCPClient(server, user, password);

        connections.add(thread);

        List<Topic> topicList = thread.getTopicList();
        List<String> keywords = thread.getKeywords();

        thread.sendObject(new Topic(new ArrayList<>(), ""));
        thread.sendObject("true");
        thread.sendObject("true");

        ArrayList<Topic> subList = (ArrayList<Topic>)thread.readObject();

        System.out.println("Currently Subscribed: \n");
        for (Topic topic : subList){
            if (topic.hasSub(user)) {
                System.out.println(topic.toString() + "\n" +
                        "-----------------------------\n");
            }
        }
    }
}
