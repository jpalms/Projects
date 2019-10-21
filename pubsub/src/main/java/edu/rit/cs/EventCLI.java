package edu.rit.cs;

import java.util.HashMap;
import java.util.Scanner;

public class EventCLI {

    /**
     * Function to gracefully terminate the node.
     * Takes an EventManager and a handler
     * No return, but calls System.exit with status (1).
     *
     * @param em - EventHandler passed in, used to call stopService
     * @param h - Handler passed in, used in stopService
     */
    public static void emTerminate(EventManager em, EventManager.Handler h){
        em.stopService(h);
        System.out.println("EventManager has been terminated.");
        System.exit(1);
    }

    /**
     * Function to show all subscribers in the EventManager.
     * Takes an EventManager as a parameter.
     * No return, but prints the list of subscribers to console.
     */
    public static void showAllsubs(EventManager em){
        em.showAllSubs();
    }

    /**
     * Function to show all subscribers to a given topic, based on user input.
     * Takes an EventManager as a parameter.
     * Within the function, takes user input and either
     *      -prints out a list of users that have subscribed to the given topic
     *      -prints a pseudo-error message, returns you to the main prompt
     *
     * @param em - used to get the topic HashMap
     */
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

    /**
     * Function to start up the EventManager's CLI.
     * Takes an EventManager and a Handler, to be passed later on.
     * No return, but responds based on user input.
     *
     * @param em - EventHandler currently running on the node
     * @param h - Handler from the EventManager to be passed later
     */
    public void startCLI(EventManager em, EventManager.Handler h){
        System.out.println("============EVENTMANAGER============\n");

        Scanner em_input = new Scanner(System.in);
        boolean exit_flag = true;
        do{
            System.out.println("Commands available: \n" +
                    "Terminate (\"t\") \t" +
                    "Show All Subscribers (\"a\") \t" +
                    "Show Subscriber(s) for a Topic (\"s\") \t"
            );
            String command = em_input.nextLine();
            switch(command){
                case "t":
                    emTerminate(em, h);
                    exit_flag = false;
                    break;
                case "a":
                    showAllsubs(em);
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
