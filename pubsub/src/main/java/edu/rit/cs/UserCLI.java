package edu.rit.cs;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

/*
 * Class to run on publisher / subscriber nodes.
 * Makes a User object based on input.
 */

public class UserCLI {

    private static String password;

    private static User CLIBegin(String server) {
        Scanner initial = new Scanner(System.in);

        System.out.println(
                "=================================\n" +
                "            User Node            \n" +
                "=================================\n\n");

        System.out.println("Pick an option:\n" +
                            "Create User(\"create\"" + "\t\t" +
                            "Sign In(\"signin\"");

        TCPClient firstThread = new TCPClient(server);
        while(true) {
            String checkOpt = initial.nextLine(); // Read user's decision
            if (checkOpt.equals("create")) {
                User temp = usrCreate(firstThread);
                usrSignin(firstThread);
                firstThread.reciever();
                return temp;
            } else if (checkOpt.equals("signin")) {
                firstThread.sendBool(false);
                firstThread.reciever();
                return usrSignin(firstThread);
            } else {
                System.out.println("Does not match either of the commands.\n");
                System.out.println("Please enter \"create\" or \"signin\".\n");
            }
        }
    }

    private static User usrCreate(TCPClient firstThread){
        // send msg saying new user

        Scanner create = new Scanner(System.in);

        String user = "";
        String pass = "";
        String roleStr = "";
        User.pubOrSub role = null;
        boolean uniqueId = false;

        firstThread.sendBool(true);
        do{
            System.out.println("\nPlease enter in a new username: ");
            user = create.nextLine();
            firstThread.sendObject(uniqueId);
            if (firstThread.readBool()){
                System.out.println("Username is taken already.\n");
            } else {

                uniqueId = true;
            }
        } while (!(uniqueId));

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

        System.out.println("Are you a subscriber(\"sub\") or a publisher(\"pub\"?");
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

    public static User usrSignin(TCPClient firstThread) {
        Scanner user_input = new Scanner(System.in);
        while (true){
            System.out.println("Enter username: ");
            String user = user_input.nextLine();
            String pass = "";
            firstThread.sendObject(user);
            if (firstThread.readBool()) {
                while (true) {
                    System.out.println("Enter password: ");
                    pass = user_input.nextLine();
                    firstThread.sendObject(pass);
                    int pass_tries = 0;
                    if (firstThread.readBool()) {
                        User user_node = (User)firstThread.readObject();
                        firstThread.sendBool(false);
                        password = pass;
                        return user_node;
                    } else {
                        if (pass_tries > 3) {
                            System.out.println("Too many attempts. Program terminating.");
                            System.exit(1);
                        } else {
                            System.out.println("Password does not match, please try again.");
                            pass_tries++;
                        }
                    }
                }
            } else {
                System.out.println("Username does not exist. Please try again. \n ");
            }
        }
    }

    /**
     *  Return a Topic from the list if it exists.
     *  Takes a string, returns a Topic.
     **/
    public static void subSub(User currUser, String server, String password) {
        Scanner subscribe = new Scanner(System.in);
        System.out.println("Choose: subscribe by Topic (\"t\") " +
                "or by keyword (\"k\")\n");
        String sub_imp = subscribe.nextLine();

        TCPClient thread = new TCPClient(server);

        thread.autoLogin(currUser, password);
        currUser = (User) thread.readObject();
        thread.sendBool(true);

        List<Topic> topicList = (List<Topic>) thread.readObject();
        List<String> keywords = (List<String>) thread.readObject();

        if (sub_imp.equals("t")) {
            System.out.println("What topic would you like to subscribe to?\n");
            String topic_str = subscribe.nextLine();
            Topic topic = null;
            while (topic == null) {
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
            thread.sendBool(true);
            thread.sendBool(false);
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
            thread.sendBool(false);
            thread.sendBool(false);
            currUser.subscribe(keyword);
        } else {
            System.out.println("Input does not match available options.\n" +
                               "Returning to command list...\n");
        }

    }

    public static void subUnsub(User currUser, String server, String password){
        Scanner unsubscribe = new Scanner(System.in);
        System.out.println("Would you like to unsubscribe from all topics (\"a\")" +
                            "one just one (\"o\") ?\n");
        String unsub_imp = unsubscribe.nextLine();
        if (unsub_imp.equals("a")){
            System.out.println("Removing all subscriptions...\n");
            TCPClient thread = new TCPClient(server);

            thread.autoLogin(currUser, password);
            currUser = (User) thread.readObject();
            thread.sendBool(true);

            List<Topic> topicList = (List<Topic>) thread.readObject();
            List<String> keywords = (List<String>) thread.readObject();

            thread.sendObject(new Topic(new ArrayList<>(), ""));
            thread.sendBool(false);
            thread.sendBool(true);
            currUser.unsubscribe();
        } else if (unsub_imp.equals("o")){
            System.out.println("What topic would you like to unsubscribe from?\n");
            System.out.println("Available topics: ");

            TCPClient thread = new TCPClient(server);

            thread.autoLogin(currUser, password);
            currUser = (User) thread.readObject();
            thread.sendBool(true);

            List<Topic> topicList = (List<Topic>) thread.readObject();
            List<String> keywords = (List<String>) thread.readObject();

            // print topicList
            String topic_str = unsubscribe.nextLine();
            Topic topic = null;
            while (topic == null) {
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
            thread.sendBool(false);
            thread.sendBool(false);
            //currUser.unsubscribe(topic);
        } else {
            System.out.println("Input does not match available options.\n" +
                    "Returning to command list...\n");
        }

    }

    public static void subCLI(User currUser, String server, String password){
        Scanner sub_input = new Scanner(System.in);

        boolean exit_flag = true;
        do{
            System.out.println("Commands available to subscribers: \n" +
                                "Subscribe (\"s\") \t" +
                                "Unsubscribe (\"u\") \t" +
                                "List Subscribed Topics (\"l\") \t" +
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
                    TCPClient thread = new TCPClient(server);

                    thread.autoLogin(currUser, password);
                    currUser = (User) thread.readObject();
                    thread.sendBool(true);

                    List<Topic> topicList = (List<Topic>) thread.readObject();
                    List<String> keywords = (List<String>) thread.readObject();

                    thread.sendObject(new Topic(new ArrayList<>(), ""));
                    thread.sendBool(true);
                    thread.sendBool(true);
                    currUser.listSubscribedTopics();
                    break;
                case "q":
                    exit_flag = false;
                    break;
                default:
                    System.out.println("Not an available command for subscribers.\n");
            }
        } while (exit_flag);
    }

    private static void pubPub(User currUser, String server, String password) {
        Scanner publish = new Scanner (System.in);

        System.out.println("Title of your event: ");
        String e_title = publish.nextLine();

        TCPClient thread = new TCPClient(server);

        thread.autoLogin(currUser, password);
        currUser = (User) thread.readObject();
        thread.sendBool(true);

        List<Topic> topicList = (List<Topic>) thread.readObject();
        List<String> keywords = (List<String>) thread.readObject();

        Topic chk_top = null;

        String topic_str = publish.nextLine();
        Topic topic = null;
        while (topic == null) {
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

        System.out.println("Content for your event: \n");
        String e_content = publish.nextLine();

        Event new_event = new Event( -1, chk_top, e_title, e_content);

        thread.sendObject(new_event);

        //currUser.publish(new_event);
    }

    private static void pubAdv(User currUser, String server, String password) {
        Scanner advertise = new Scanner (System.in);

        System.out.println("Name of your topic: ");
        String t_name = advertise.nextLine();

        TCPClient thread = new TCPClient(server);

        thread.autoLogin(currUser, password);
        currUser = (User) thread.readObject();
        thread.sendBool(true);

        List<Topic> topicList = (List<Topic>) thread.readObject();
        List<String> keywords = (List<String>) thread.readObject();

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
        //currUser.advertise(new_topic);
    }

    public static void pubCLI(User currUser, String server, String password){
        Scanner pub_input = new Scanner(System.in);
        boolean exit_flag = true;
        do{
            System.out.println("Commands available to subscribers: \n" +
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
                    exit_flag = false;
                    break;
                default:
                    System.out.println("Not an available command for subscribers.\n");
            }
        } while (exit_flag);
    }

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

    public static class TCPClient extends Thread {

        ObjectInputStream in;
        ObjectOutputStream out;

        public TCPClient(String addr) {
            String server_address = addr;
            Socket s = null;
            try {
                // create connection
                int serverPort = 7896;
                s = new Socket(server_address, serverPort);

                out = new ObjectOutputStream(s.getOutputStream());
                in = new ObjectInputStream(s.getInputStream());


            } catch (UnknownHostException e) {
                System.out.println("Sock:" + e.getMessage());
            } catch (EOFException e) {
                System.out.println("EOF:" + e.getMessage());
            } catch (IOException e) {
                System.out.println("IO:" + e.getMessage());
            } finally {
                if (s != null)
                    try {
                        s.close();
                    } catch (IOException e) {
                        System.out.println("close:" + e.getMessage());
                    }
            }
        }

        public void autoLogin(User user, String password){
            try {
                out.writeBoolean(false);
                out.writeObject(user.getId());
                out.writeObject(password);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void sendObject(Object obj){
            try {
                out.writeObject(obj);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public Object readObject(){
            try {
                return in.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return new Object();
        }

        public void sendBool(boolean bool){
            try {
                out.writeBoolean(bool);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public boolean readBool(){
            try {
                return in.readBoolean();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        public void reciever(){
            boolean running = true;
            while (running){
                try {
                    in.readObject();
                } catch (IOException e){
                    System.err.println("IO: " + e.getMessage());
                } catch (ClassNotFoundException e){
                    System.err.println("CLASS: " + e.getMessage());
                }
            }
        }

    }
    public static void main(String[] args) {
        String server = args[0];
        startCLI(server);
    }
}
