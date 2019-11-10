package edu.rit.cs.controller;


import edu.rit.cs.model.Connection;
import edu.rit.cs.view.ServerCLI;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.List;

public class AnchorNode {

	private TreeMap<String, Connection> onlineNodes;


	public AnchorNode() {
		onlineNodes = new TreeMap<>();
	}
	//

	/**
	 * Start the repo service
	 **/
	private void startService() {
		NotifyNodes notifyAll = new NotifyNodes(this);
		notifyAll.start();
		ServerCLI cli = new ServerCLI();
		cli.startCLI(this, notifyAll);
	}

	/**
	 *  Stop the repo service
	 * @param h Handler instance that takes care of shutdown
	 */
	public void stopService(MiniServer h) {
		h.turnOff();
	}
// ___________________________________________________________

	/**
	 * Write Operations
	 */
	
	

    /**
     * adds a user to list of all users that have an account
     *
     * @param
     */
	public synchronized void addNode(String id, Connection conn){
		onlineNodes.put(id, conn);
	}    

// _________________________________________________________

	/**
	 *  Read Operations
	 */

	/**
	 * Gets size of the onlineNodes list
	 *
	 * @return		int, size of the onlineNodes list
	 */
	public synchronized int getNumOnline(){
		return onlineNodes.size();
	}

	/**
	 * Gets TreeMap of onlineNodes
	 *
	 * @return		TreeMap of onlineNodes
	 */
	public TreeMap<String, Connection> getOnlineNodes(){
		return onlineNodes;
	}

	public synchronized boolean isOnline(String id){
		if(onlineNodes.isEmpty())
			return false;
		return onlineNodes.containsKey(id);
	}
	
	//_______________________________________________________________________

	/**
	 * Main function which is run and starts the program
	 *
	 * @param args
	 **/
	public static void main(String[] args) {
		new AnchorNode().startService();
		// start command line
	}
}