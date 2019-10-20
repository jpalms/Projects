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
        boolean uniqueId = false;
        while(!(uniqueId)) {
            System.out.println("\nPlease enter in a new username: ");
            user = create.nextLine();
            // TODO check username here, make equal to uniqueId
            if (!(uniqueId)){
                System.out.println("Username is taken already.\n");
            }
        }

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

    private static void startCMD() {
        User currUser = cmdBegin();
    }

    public static void main(String[] args) {
        startCMD();
    }


}
