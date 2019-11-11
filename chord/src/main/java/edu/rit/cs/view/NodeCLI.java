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

    /**
     * Function for a chord node to gracefully shut down.
     */
    private static void turnOff(NodeCLI_Helper nch){
        // TODO - NCH should notify server, send any files to be rehashed, and clear finger table
        // call the new function gracefulShutdown()
        firstThread.turnOffFirst();
        System.exit(1);
    }

    /**
     * Function for a node to identify its ID and log in.
     *
     * @param server - String to connect with the Server thru TCP
     *
     * @return Node - acts as the instance's information
     */
    private static Node nodeSignin(String server) {
        System.out.println(
                "=================================\n" +
                        "            Chord Node            \n" +
                        "=================================\n\n");

        System.out.println("Sign In\n");

        firstThread = new TCPClientNode(server);
        Scanner user_input = new Scanner(System.in);
        while (true){
            System.out.println("Enter Node id: ");
            String id = user_input.nextLine();
            firstThread.sendObject(id);
            if (firstThread.readObject().equals("true")) {
                System.out.println("Loading Finger Table...\n");
                Node user_node = (Node)firstThread.readObject();
                firstThread.start();
                return user_node;
            } else {
                System.out.println("Node id already exists.\n ");
            }
        }
    }

    /**
     * Function to run the CLI for a chord node.
     *
     * @param currNode - the nodes' associated Node obj.
     * @param server - the String to connect to the Server thru TCP
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
                case "s":
                    // TODO showFTable()
                    break;
                case "q":
                    helper.quit();

                    turnOff(helper);
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
        Node currNode = nodeSignin(server);
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
