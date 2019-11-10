package edu.rit.cs.view;

import edu.rit.cs.controller.TCPClientNode;
import edu.rit.cs.model.Node;

import java.io.File;
import java.util.Scanner;

/**
 * Class to run the chord peer nodes.
 * Makes a Node object, and based on input, assigns a location.
 */

public class NodeCLI {

    private static TCPClientNode firstThread;


    private static void turnOff(){
        firstThread.turnOffFirst();
        System.exit(1);
    }

    /**
     * Function to determine whether a user has an account or is creating one.
     * No parameters, but operates based on user input.
     * Returns a Node to be used for the node.
     *
     * @return Node Object
     */
    private static Node CLIBegin(String server) {
        Scanner initial = new Scanner(System.in);

        System.out.println(
                "=================================\n" +
                        "            Node Node            \n" +
                        "=================================\n\n");

        System.out.println("Sign In\n");

        firstThread = new TCPClientNode(server);

        return nodeSignin();
    }

    /**
     * Function for a node to sign into a previously-created account.
     * No parameters, but operates on user input, namely creating a Node object based on EventManager info.
     * Returns a Node for the node. (Called by CLIBegin)
     *
     * @return Node Object
     */
    private static Node nodeSignin() {
        Scanner user_input = new Scanner(System.in);
        while (true){
            System.out.println("Enter Node id: ");
            String id = user_input.nextLine();
            firstThread.sendObject(id);
            if (firstThread.readObject().equals("true")) {
                Node node = (Node)firstThread.readObject();

                return node;
            } else {
                System.out.println("Node id already exists\n ");
            }
        }
    }

    /**
     * Function to run the CLI for a publisher node.
     * Takes in the Node object associated with the node.
     * No return, but calls other functions based on user input.
     *
     * @param currNode - the nodes' associated Node obj.
     */
    private static void nodeCLI(Node currNode, String server){
        Scanner input = new Scanner(System.in);
        boolean exit_flag = true;

        NodeCLI_Helper helper = new NodeCLI_Helper(currNode, server);

        System.out.println("Loading Finger Table");

        helper.queryAll();

        do{
            System.out.println("Commands available to Node: \n" +
                    "Insert File (\"i\") \t" +
                    "Lookup File (\"l\") \t" +
                    "Quit (\"q\")\n"
            );
            String command = input.nextLine();
            switch(command){
                case "i":
                    System.out.println("Enter file path: ");
                    String path = input.nextLine();
                    File f = new File(path);

                    helper.insert(f);

                    break;
                case "l":
                    System.out.println("Enter file to lookup: ");
                    String hash = input.nextLine();

                    helper.lookup(hash);
                    break;
                case "q":
                    helper.quit();

                    turnOff();
                    exit_flag = false;
                    break;
                default:
                    System.out.println("Not an available command for a Node.\n");
            }
        } while (exit_flag);
    }

    /**
     * Function to start the CLI for a Node node.
     * Takes no parameters and returns nothing.
     * Calls publisher / subscriber CLI based on user input in CLIBegin.
     */
    private static void startCLI(String server) {
        Node currNode = CLIBegin(server);
        System.out.println("============NODE============");

        nodeCLI(currNode, server);
    }

    /**
     * Function that runs the NodeCLI.
     * @param args - runs with the IP address of the EventManager
     */
    public static void main(String[] args) {
        String server = args[0];
        startCLI(server);
    }
}
