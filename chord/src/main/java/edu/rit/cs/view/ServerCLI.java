package edu.rit.cs.view;

import edu.rit.cs.controller.AnchorNode;
import edu.rit.cs.controller.NotifyNodes;

import java.util.Scanner;

public class ServerCLI {

    /**
     * Function to gracefully terminate the server node.
     * Takes a handler thread (with the connections to the peer nodes of the chords
     * No return, but calls System.exit with status (1).
     *
     * @param  - Handler passed in, used in stopService
     */
    public static void serverTerminate(NotifyNodes notifyNodes){
        notifyNodes.turnOff();
        System.out.println("MiniServer has been terminated.");
        System.exit(1);
    }

    /**
     * Function to start up the MiniServer's CLI.
     * Takes an MiniServer and a Handler, to be passed later on.
     * No return value, but responds based on user input.
     *
     * @param anchorNode - MiniServer currently running on the node
     * @param notifyNodes - Handler to be passed on later
     */
    public void startCLI(AnchorNode anchorNode, NotifyNodes notifyNodes){
        System.out.println("============SERVER============\n");

        Scanner sv_input = new Scanner(System.in);
        boolean exit_flag = true;
        do{
            System.out.println("Commands available: \n" +
                    "Terminate (\"t\") \t" +
                    "Show All Nodes (\"a\") \t" // +
            );
            String command = sv_input.nextLine();
            switch(command){
                case "t":
                    serverTerminate(notifyNodes);
                    exit_flag = false;
                    break;
                case "a":
                    anchorNode.showAllNode();
                    break;
                default:
                    System.out.println("Not an available command for servers.\n");
            }
        } while (exit_flag);
    }
}
