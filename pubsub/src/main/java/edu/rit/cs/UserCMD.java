package edu.rit.cs;

import java.util.*;

/*
 * Class to run on publisher / subscriber nodes.
 * Makes a User object based on input.
 */

public class UserCMD {

    private static User cmdBegin() {
        Scanner initial = new Scanner(System.in);

        System.out.println(
                "=================================\n" +
                "            User Node            \n" +
                "=================================\n\n");

        System.out.println("Pick an option:\n" +
                            "Create User(\"create\"" + "\t\t" +
                            "Sign In(\"signin\"");

        while(true) {
            String checkOpt = initial.nextLine(); // Read user's decision
            if (checkOpt.equals("create")) {
                return usrCreate();
            } else if (checkOpt.equals("signin")) {
                return usrSignin();
            } else {
                System.out.println("Does not match either of the commands.\n");
                System.out.println("Please enter \"create\" or \"signin\".\n");
            }
        }
    }

    private static User usrCreate(){
        // send msg saying new user

        Scanner create = new Scanner(System.in);

        String user = "";
        String pass = "";
        String roleStr = "";
        User.pubOrSub role = null;
        //boolean uniqueId = false;

        /*
        while(!(uniqueId)) {
            System.out.println("\nPlease enter in a new username: ");
            user = create.nextLine();
            // TODO check username here, make equal to uniqueId
            if (!(uniqueId)){
                System.out.println("Username is taken already.\n");
            }
        }
        */

        boolean passChk = true;
        while( passChk ) {
            System.out.println("Please enter a new password: ");
            pass = create.nextLine();
            System.out.println("Re-enter password: ");
            passChk = pass.equals(create.nextLine());
            if (passChk){
                System.out.println("Account created.\n");
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
        return new User(role, user, pass);
    }

    public static User usrSignin() {
        Scanner user_input = new Scanner(System.in);
        while (true){
            System.out.println("Enter username: ");
            String user = user_input.nextLine();
            String pass = "";
            if (/* TODO check if username exists */) {
                while (true) {
                    System.out.println("Enter password: ");
                    pass = user_input.nextLine();
                    int pass_tries = 0;
                    if (/* TODO check to see if password is correct*/) {
                        User node_user = null;
                        // TODO get user from eventmanager hashmap
                        return node_user;
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

    public static Topic topicExist(String check_string){

    }

    public static void subSub(User currUser){
        Scanner subscribe = new Scanner(System.in);
        System.out.println("Choose: subscribe by Topic (\"t\") " +
                           "or by keyword (\"k\")\n");
        String sub_imp = subscribe.nextLine();
        if (sub_imp.equals("t")){
            System.out.println("What topic would you like to subscribe to?\n");
            String topic_str = subscribe.nextLine();
            Topic topic = topicExist(topic_str);
            if (topic != null){
                currUser.subscribe(topic);
            } else {
                System.out.println("Input does not match available options.\n" +
                        "Returning to command list...\n");
            }
        } else if (sub_imp.equals("k")){
            System.out.println("Please enter a keyword that you would like to search by: ");
            String keyword = subscribe.nextLine();
            currUser.subscribe(keyword);
        } else {
            System.out.println("Input does not match available options.\n" +
                               "Returning to command list...\n");
        }

    }

    public static void subUnsub(User currUser){
        Scanner unsubscribe = new Scanner(System.in);
        System.out.println("Would you like to unsubscribe from all topics (\"a\")" +
                            "one just one (\"o\") ?\n");
        String unsub_imp = unsubscribe.nextLine();
        if (unsub_imp.equals("a")){
            System.out.println("Removing all subscriptions...\n");
            currUser.unsubscribe();
        } else if (unsub_imp.equals("o")){
            System.out.println("What topic would you like to unsubscribe from?\n");
            System.out.println("Available topics: ");
            currUser.listSubscribedTopics();
            String topic_str = unsubscribe.nextLine();
            // TODO error check input topic against topic list
            Topic unsub_topic = null; //TODO - FIX
            currUser.unsubscribe(unsub_topic);
        } else {
            System.out.println("Input does not match available options.\n" +
                    "Returning to command list...\n");
        }

    }

    public static void subCLI(User currUser){
        Scanner sub_input = new Scanner(System.in);
        boolean exit_flag = true;
        do{
            System.out.println("Commands available to subscribers: \n" +
                                "Subscribe (\"s\") \t" +
                                "Unsubscribe (\"u\") \t" +
                                "Quit (\"q\")\n"
                              );
            String command = sub_input.nextLine();
            switch(command){
                case "s":
                    subSub();
                    break;
                case "u":
                    subUnsub();
                    break;
                case "q":
                    exit_flag = false;
                    break;
                default:
                    System.out.println("Not an available command for subscribers.\n");
            }
        } while (exit_flag);
    }

    private static void pubPub(User currUser) {
        Scanner publish = new Scanner (System.in);

        System.out.println("Title of your event: ");
        String e_title = publish.nextLine();

        boolean topic_flag = true;
        String e_topic_str;
        Topic chk_top = null;
        do {
            System.out.println("Topic of your event: ");
            e_topic_str = publish.nextLine();
            chk_top = topicExist(e_topic_str);
            if (chk_top != null) {
                topic_flag = false;
            }
        } while (topic_flag);

        System.out.println("Content for your event: \n");
        String e_content = publish.nextLine();

        Event new_event = new Event( -1, chk_top, e_title, e_content);

        currUser.publish(new_event);
    }

    private static void pubAdv(User currUser) {
        Scanner advertise = new Scanner (System.in);

        System.out.println("Name of your topic: ");
        String t_name = advertise.nextLine();

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

        currUser.advertise(new_topic);
    }

    public static void pubCLI(User currUser){
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
                    pubPub();
                    break;
                case "a":
                    pubAdv();
                    break;
                case "q":
                    exit_flag = false;
                    break;
                default:
                    System.out.println("Not an available command for subscribers.\n");
            }
        } while (exit_flag);
    }




    private static void startCMD() {
        User currUser = cmdBegin();
        // send user to eventmanager
        if (currUser.role == User.pubOrSub.SUB){
            System.out.println("============SUBSCRIBER============");
            subCLI(currUser);
        } else if (currUser.role == User.pubOrSub.PUB){
            System.out.println("============PUBLISHER============");
            pubCLI(currUser);
        } else {
            //error message here
        }
    }

    public static void main(String[] args) {
        startCMD();
    }


}
