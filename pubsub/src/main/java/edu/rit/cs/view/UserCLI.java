package edu.rit.cs.view;

import edu.rit.cs.controller.TCPClient;
import edu.rit.cs.model.User;

import java.util.*;

/**
 * Class to run on publisher / subscriber nodes.
 * Makes a User object based on input.
 */

public class UserCLI {

    private static String password;
    private static TCPClient firstThread;


    private static void turnOff(){
        firstThread.turnOffFirst();
        System.exit(1);
    }

    /**
     * Function to determine whether a user has an account or is creating one.
     * No parameters, but operates based on user input.
     * Returns a User to be used for the node.
     *
     * @return User Object
     */
    private static User CLIBegin(String server) {
        Scanner initial = new Scanner(System.in);

        System.out.println(
                "=================================\n" +
                        "            User Node            \n" +
                        "=================================\n\n");

        System.out.println("Pick an option:\n" +
                "Create User(\"create\")" + "\t\t" +
                "Sign In(\"signin\")");

        firstThread = new TCPClient(server);
        while(true) {
            String checkOpt = initial.nextLine(); // Read user's decision
            if (checkOpt.equals("create")) {
                User temp = usrCreate();
                usrSignin();

                return temp;
            } else if (checkOpt.equals("signin")) {
                firstThread.sendObject("false");

                return usrSignin();
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
    private static User usrCreate(){
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
    private static User usrSignin() {
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
                firstThread.start();
                return user_node;
            } else {
                System.out.println("Username does not exist. Please try again. \n ");
            }
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

        UserCLI_Helper helper = new UserCLI_Helper(currUser, server, password);

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
                    helper.subcribe();
                    break;
                case "u":
                    helper.unSubscribe();
                    break;
                case "l":
                    helper.listSubscriptions();

                    break;
                case "q":
                    helper.turnOff();
                    turnOff();
                    exit_flag = false;
                    break;
                default:
                    System.out.println("Not an available command for subscribers.\n");
            }
        } while (exit_flag);
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

        UserCLI_Helper helper = new UserCLI_Helper(currUser, server, password);
        do{
            System.out.println("Commands available to publishers: \n" +
                    "Publish (\"p\") \t" +
                    "Advertise (\"a\") \t" +
                    "Quit (\"q\")\n"
            );
            String command = pub_input.nextLine();
            switch(command){
                case "p":
                    helper.publish();
                    break;
                case "a":
                    helper.advertise();
                    break;
                case "q":
                    helper.turnOff();
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
        if (currUser.role == User.pubOrSub.SUB){
            System.out.println("============SUBSCRIBER============");
            subCLI(currUser, server, password);
        } else if (currUser.role == User.pubOrSub.PUB){
            System.out.println("============PUBLISHER============");
            pubCLI(currUser, server, password);
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
