package edu.rit.cs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;

public class EventCLI {


    //TODO - Write termination protocol
    public static void emTerminate(EventManager em){
        em.stopService();
        System.out.println("EventManager has been terminated.");
        System.exit(1);
    }

    //TODO - Show subscriptions for every topic
    public static void showSubs(EventManager em){
        Scanner show = new Scanner(System.in);
        System.out.println("Please input the topic that you want to show subscribers for: ");
        String show_str = show.nextLine();
        HashMap<String, Topic> t_list = em.getTopicMap();
        Topic topic = t_list.get(show_str);
        if (topic != null){
            for(User user:topic.getSubs().values()){
                System.out.println(user);
            }
        } else {
            System.out.println("Input does not match available options.\n" +
                    "Returning to command list...\n");
        }
    }

    public static void startCLI(){
        System.out.println("============EVENTMANAGER============\n");

        EventManager em = new EventManager();

        Scanner em_input = new Scanner(System.in);
        boolean exit_flag = true;
        do{
            System.out.println("Commands available: \n" +
                    "Terminate (\"t\") \t" +
                    "Show Subscriber(s) for a Topic (\"s\") \t"
            );
            String command = em_input.nextLine();
            switch(command){
                case "t":
                    emTerminate(em);
                    exit_flag = false;
                    break;
                case "s":
                    showSubs(em);
                    break;
                default:
                    System.out.println("Not an available command for subscribers.\n");
            }
        } while (exit_flag);
    }

    public static void main(String args[]){
        startCLI();
    }

}
