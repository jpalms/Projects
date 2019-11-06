package edu.rit.cs.view;

import edu.rit.cs.controller.TCPClient;
import edu.rit.cs.model.Event;
import edu.rit.cs.model.Topic;
import edu.rit.cs.model.User;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Class to run on publisher / subscriber nodes.
 * Makes a User object based on input.
 */

/**
 * Function to determine whether a user has an account or is creating one.
 * No parameters, but operates based on user input.
 * Returns a User to be used for the node.
 *
 * @return User Object
 */
public class UserCLI {

    private static String password;
    private static ArrayList<TCPClient> connections = new ArrayList<TCPClient>();


    private static void turnOff(){
        while (connections.size() > 1) {
            connections.get(1).turnOff();
            connections.remove(1);
        }
        connections.get(0).turnOffFirst();
        System.exit(1);
    }

    private static User CLIBegin(String server) {
        Scanner initial = new Scanner(System.in);

        System.out.println(
                "=================================\n" +
                        "            User Node            \n" +
                        "=================================\n\n");

        System.out.println("Pick an option:\n" +
                "Create User(\"create\")" + "\t\t" +
                "Sign In(\"signin\")");

        TCPClient firstThread = new TCPClient(server);
        while(true) {
            String checkOpt = initial.nextLine(); // Read user's decision
            if (checkOpt.equals("create")) {
                User temp = usrCreate(firstThread);
                usrSignin(firstThread);

                return temp;
            } else if (checkOpt.equals("signin")) {
                firstThread.sendObject("false");

                return usrSignin(firstThread);
            } else {
                System.out.println("Does not match either of the commands.\n");
                System.out.println("Please enter \"create\" or \"signin\".\n");
            }
        }
    }

    /**
     * Function to create a new publisher or subscriber.
     * No parameters, but operates on user input, namely creating a User object based on input.
     * Returns a User for the node. (Called by CLIBegin)
     *
     * @return User Object
     */
    private static User usrCreate(TCPClient firstThread){
        // send msg saying new user

        Scanner create = new Scanner(System.in);

        String user = "";
        String pass = "";
        String roleStr = "";
        User.pubOrSub role = null;
        firstThread.sendObject("true");
        do{
            System.out.println("\nPlease enter in a new username: ");
            user = create.nextLine();
            firstThread.sendObject(user);
            if (firstThread.readObject().equals("true")){
                System.out.println("Username is taken already.\n");
            } else {
                System.out.println("Username is valid");
                break;
            }
        } while (true);

        boolean passChk = true;
        while( passChk ) {
            System.out.println("Please enter a new password: ");
            pass = create.nextLine();
            System.out.println("Re-enter password: ");
            passChk = pass.equals(create.nextLine());
            if (passChk){
                System.out.println("Password matches.\n");
                passChk = false;
            } else {
                System.out.println("Passwords do not match.\n");
                passChk = true;
            }
        }

        System.out.println("Are you a subscriber(\"sub\") or a publisher(\"pub\")?");
        while(role == null) {
            System.out.println("Please clarify here: ");
            roleStr = create.nextLine();
            if (roleStr.equals("sub")){
                role = User.pubOrSub.SUB;
            } else if (roleStr.equals("pub")){
                role = User.pubOrSub.PUB;
            } else {
                System.out.println("Does not match either of the commands.\n");
                System.out.println("Please enter \"sub\" or \"pub\".\n");
            }
        }
        User user1 = new User(role, user, pass);
        firstThread.sendObject(user1);
        return user1;
    }

    /**
     * Function for a node to sign into a previously-created account.
     * No parameters, but operates on user input, namely creating a User object based on EventManager info.
     * Returns a User for the node. (Called by CLIBegin)
     *
     * @return User Object
     */
    private static User usrSignin(TCPClient firstThread) {
        Scanner user_input = new Scanner(System.in);
        while (true){
            System.out.println("Enter username: ");
            String user = user_input.nextLine();
            String pass = "";
            firstThread.sendObject(user);
            if (firstThread.readObject().equals("true")) {
                System.out.println("Enter password: ");
                pass = user_input.nextLine();
                firstThread.sendObject(pass);
                User user_node = (User)firstThread.readObject();
                firstThread.sendObject(false);
                password = pass;
                connections.add(firstThread);
                firstThread.start();
                return user_node;
            } else {
                System.out.println("Username does not exist. Please try again. \n ");
            }
        }
    }

    /**
     * Posts a list of Events / Topics that the user needs to be aware of.
     * If pub, only shows new Topics.
     * If sub, shows new Topics, and Events that they are subscribed to.
     * Takes no parameters, does not return, prints stuff out.
     */
    private static void infoUpdate() {

        TCPClient receiverThread = connections.get(0);
        ArrayList<Object> updates = receiverThread.getUpdates();
        receiverThread.emptyUpdates();

        System.out.println("Updates:\n");
        if (updates.isEmpty()) {
            System.out.println("No new updates.\n");
        } else {
            for (Object obj : updates) {
                System.out.println(obj.toString() + "\n");
                System.out.println("-----------------------------\n");
            }
        }
    }

    /**
     * Function to subscribe to a Topic.
     * Takes in the User object associated with the node.
     * No return, but adds the User to a 'subscribed' list for the Topic.
     *
     * @param currUser - the node's associated User obj
     */
    private static void subSub(User currUser, String server, String password) {
        Scanner subscribe = new Scanner(System.in);
        System.out.println("Choose: subscribe by Topic (\"t\") " +
                "or by keyword (\"k\")\n");
        String sub_imp = subscribe.nextLine();

        TCPClient thread = new TCPClient(server, currUser, password);

        connections.add(thread);

        List<Topic> topicList = thread.getTopicList();
        List<String> keywords = thread.getKeywords();

        if (sub_imp.equals("t")) {
            System.out.println("What topic would you like to subscribe to?\n");
            Topic topic = null;
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
            currUser.subscribe(keyword);
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
     * @param currUser - the nodes' associated User obj
     */
    private static void subUnsub(User currUser, String server, String password){
        Scanner unsubscribe = new Scanner(System.in);
        System.out.println("Would you like to unsubscribe from all topics (\"a\")" +
                "one just one (\"o\") ?\n");
        String unsub_imp = unsubscribe.nextLine();
        if (unsub_imp.equals("a")){
            System.out.println("Removing all subscriptions...\n");
            TCPClient thread = new TCPClient(server, currUser, password);

            connections.add(thread);

            List<Topic> topicList = thread.getTopicList();
            List<String> keywords = thread.getKeywords();

            thread.sendObject(new Topic(new ArrayList<>(), ""));
            thread.sendObject("false");
            thread.sendObject("true");
            currUser.unsubscribe();
        } else if (unsub_imp.equals("o")){
            System.out.println("What topic would you like to unsubscribe from?\n");
            System.out.println("Available topics: ");


            TCPClient thread = new TCPClient(server, currUser, password);

            connections.add(thread);

            List<Topic> topicList = thread.getTopicList();
            List<String> keywords = thread.getKeywords();
            
            //System.out.println("\n");
            for (Topic topic : topicList) {
                System.out.println(topic.getName() + "\n");
            }

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
            //currUser.unsubscribe(topic);
        } else {
            System.out.println("Input does not match available options.\n" +
                    "Returning to command list...\n");
        }

    }

    /**
     * Function to run the CLI for a subscriber node.
     * Takes in the User object associated with the node.
     * No return, but calls other functions based on user input.
     *
     * @param currUser - the nodes' associated User obj.
     */
    private static void subCLI(User currUser, String server, String password){
        Scanner sub_input = new Scanner(System.in);
        boolean exit_flag = true;
        do{

            System.out.println("Commands available to subscribers: \n" +
                    "Subscribe (\"s\") \t" +
                    "Unsubscribe (\"u\") \t" +
                    "List Subscribed Topics (\"l\") \t" +
                    "Show New Information (\"i\") \t" +
                    "Quit (\"q\")\n"
            );
            String command = sub_input.nextLine();
            switch(command){
                case "s":
                    subSub(currUser, server, password);
                    break;
                case "u":
                    subUnsub(currUser, server, password);
                    break;
                case "l":
                    TCPClient thread = new TCPClient(server, currUser, password);

                    connections.add(thread);

                    List<Topic> topicList = thread.getTopicList();
                    List<String> keywords = thread.getKeywords();

                    thread.sendObject(new Topic(new ArrayList<>(), ""));
                    thread.sendObject("true");
                    thread.sendObject("true");

                    ArrayList<Topic> subList = (ArrayList<Topic>)thread.readObject();

                    System.out.println("Currently Subscribed: \n");
                    for (Topic topic : subList){
                        if (topic.hasSub(currUser)) {
                            System.out.println(topic.toString() + "\n" +
                                    "-----------------------------\n");
                        }
                    }

                    break;
                case "q":
                    infoUpdate();
                    turnOff();
                    exit_flag = false;
                    break;
                default:
                    System.out.println("Not an available command for subscribers.\n");
            }
        } while (exit_flag);
    }

    /**
     * Function to publish an Event for use in EventManager.
     * Takes in the User object associated with the node.
     * No return, but creates a new Event, handled by the EventManager.
     *
     * @param currUser - the nodes' associated User obj.
     */
    private static void pubPub(User currUser, String server, String password) {
        Scanner publish = new Scanner (System.in);

        TCPClient thread = new TCPClient(server, currUser, password);

        connections.add(thread);

        List<Topic> topicList = thread.getTopicList();
        List<String> keywords = thread.getKeywords();

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

            Event new_event = new Event( -1, topic, e_title, e_content);

            thread.sendObject(new_event);

            //currUser.publish(new_event);
        } else {
            System.out.println("No topics available; returning to main command prompt...\n");
        }


    }

    /**
     * Function to advertise a Topic within EventManager.
     * Takes in the User object associated with the node.
     * No return, but creates a new Topic, handled by the EventManager.
     *
     * @param currUser - user associated with this UserCLI node
     */
    private static void pubAdv(User currUser, String server, String password) {
        Scanner advertise = new Scanner (System.in);

        System.out.println("Name of your topic: ");
        String t_name = advertise.nextLine();

        TCPClient thread = new TCPClient(server, currUser, password);

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
     * Function to run the CLI for a publisher node.
     * Takes in the User object associated with the node.
     * No return, but calls other functions based on user input.
     *
     * @param currUser - the nodes' associated User obj.
     */
    private static void pubCLI(User currUser, String server, String password){
        Scanner pub_input = new Scanner(System.in);
        boolean exit_flag = true;
        do{
            System.out.println("Commands available to publishers: \n" +
                    "Publish (\"p\") \t" +
                    "Advertise (\"a\") \t" +
                    "Quit (\"q\")\n"
            );
            String command = pub_input.nextLine();
            switch(command){
                case "p":
                    pubPub(currUser, server, password);
                    break;
                case "a":
                    pubAdv(currUser, server, password);
                    break;
                case "q":
                    infoUpdate();
                    turnOff();
                    exit_flag = false;
                    break;
                default:
                    System.out.println("Not an available command for subscribers.\n");
            }
        } while (exit_flag);
    }

    /**
     * Function to start the CLI for a User node.
     * Takes no parameters and returns nothing.
     * Calls publisher / subscriber CLI based on user input in CLIBegin.
     */
    private static void startCLI(String server) {
        User currUser = CLIBegin(server);
        // send user to eventmanager
        if (currUser.role == User.pubOrSub.SUB){
            System.out.println("============SUBSCRIBER============");
            subCLI(currUser, server, password);
        } else if (currUser.role == User.pubOrSub.PUB){
            System.out.println("============PUBLISHER============");
            pubCLI(currUser, server, password);
        } else {
            //error message here
        }
    }

    /**
     * Function that runs the UserCLI.
     * @param args - runs with the IP address of the EventManager
     */
    public static void main(String[] args) {
        String server = args[0];
        startCLI(server);
    }
}
