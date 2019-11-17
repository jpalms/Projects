package edu.rit.cs.controller;


import edu.rit.cs.model.Connection;
import edu.rit.cs.view.ServerCLI;

import java.util.TreeMap;

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

	public synchronized void removeNode(String id){
		onlineNodes.remove(id);
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
	public synchronized TreeMap<String, Connection> getOnlineNodes(){
		return onlineNodes;
	}

	public synchronized boolean isOnline(String id){
		if(this.getOnlineNodes().isEmpty())
			return false;
		return this.getOnlineNodes().containsKey(id);
	}

	public synchronized String getNext(int ideal){
        if (this.getNumOnline() == 1) {
            return this.getOnlineNodes().firstKey();
        } else {
            TreeMap<String, Connection> tree = this.getOnlineNodes();
            String key = tree.higherKey(ideal + "");
            if (key != null) {
                return key;
            } else {
                return tree.firstKey();
            }
        }
    }

    public synchronized String getSuccessor(int ideal){
        if (this.getNumOnline() == 1) {
            return this.getOnlineNodes().firstKey();
        } else {
            TreeMap<String, Connection> tree = this.getOnlineNodes();
            if(tree.containsKey(ideal + "")){
                return ideal + "";
            }
            String key = tree.higherKey(ideal + "");
            if (key != null) {
                return key;
            } else {
                return tree.firstKey();
            }
        }
    }

    public synchronized Connection getNode(String id){
		return onlineNodes.get(id);
	}

	public synchronized void showAllNode(){
		for(String node: this.getOnlineNodes().keySet())
		System.out.println("Node: " + node + "\n");
	}

	public synchronized String getPrev(int id){
		if (this.getNumOnline() == 1){
			return id + "";
		} else{
			TreeMap<String, Connection> tree = this.getOnlineNodes();
			String key = tree.lowerKey(id + "");
			if(key != null){
				return key;
			} else{
				return tree.lastKey();
			}
		}
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