package edu.rit.cs;

import java.util.HashMap;
import java.util.Scanner;

public class EventCLI {


    public static void emTerminate(EventManager em, EventManager.Handler h){
        em.stopService(h);
        System.out.println("EventManager has been terminated.");
        System.exit(1);
    }

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

    public void startCLI(EventManager em, EventManager.Handler h){
        System.out.println("============EVENTMANAGER============\n");

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
                    emTerminate(em, h);
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

}
