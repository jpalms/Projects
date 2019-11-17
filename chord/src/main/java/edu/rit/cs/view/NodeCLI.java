package edu.rit.cs.view;

import edu.rit.cs.controller.TCPClientNode;
import edu.rit.cs.model.File;

import java.io.FileNotFoundException;
import java.io.FileReader;
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
        nch.quit();
        firstThread.turnOffFirst();
        System.exit(1);
    }


    /**
     * Function to run the CLI for a chord node.
     *
     * @param server - the String to connect to the Server thru TCP
     */
    private static void nodeCLI(String server){
        Scanner input = new Scanner(System.in);
        boolean exit_flag = true;

        NodeCLI_Helper helper = new NodeCLI_Helper(server);

        System.out.println("Loading Finger Table");

        helper.queryAll();

        do{
            System.out.println("Commands available to Node: \n" +
                    "Insert File (\"i\") \t" +
                    "Lookup File (\"l\") \t" +
                    "Show Finger Table (\"s\") \t" +
                    "Quit (\"q\")"
            );
            String command = input.nextLine();
            switch(command){
                case "i":
                    System.out.println("Enter file path: ");
                    String path = input.nextLine();
                    boolean notFound = false;
                    FileReader fileReader;
                    do {
                        try {
                            fileReader = new FileReader(path);
                            notFound = false;
                        } catch (FileNotFoundException e) {
                            notFound = true;
                        }
                    } while(notFound);

                    File f = new File(path);
                    File temp = new File(path);

                    Scanner file = new Scanner(path);
                    String content = "";

                    while(file.hasNextLine()) {
                        content += file.nextLine() + "\n";
                    }

                    f.setFileContent(content);
                    f.setFileName(temp.toString());

                    helper.insert(f);

                    break;
                case "l":
                    System.out.println("Enter file name to lookup: ");
                    String hash = input.nextLine();

                    helper.lookup(hash);
                    break;
                case "s":
                    helper.showTable();
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
        nodeCLI(server);
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
