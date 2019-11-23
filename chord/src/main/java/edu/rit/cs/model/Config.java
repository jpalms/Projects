package edu.rit.cs.model;

/**
 * All Global Non-Argument Parameters.
 * Mostly used for Switch Cases.
 */
public class Config {

    // Base port for Server
    public static final int port = 13787;

    public static final String QUIT = "quit";

    public static final String LOOKUP= "lookup";
    public static final String UPDATE = "update";

    public static final String INSERT = "insert";
    public static final String INSERT_QUIT = "insert/quit";

            ;
    public static final String REORDER = "reorder";
    public static final String QUERY = "query";

    public static final String NEW_NODE = "newNode";
    public static final String REMOVED = "removed";

    public static final String DONE = "Done";
    public static final int MAX_NODES = 512;
}
