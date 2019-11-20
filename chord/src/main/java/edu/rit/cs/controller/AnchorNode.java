package edu.rit.cs.controller;


import edu.rit.cs.model.Connection;
import edu.rit.cs.view.ServerCLI;

import java.util.Collection;
import java.util.concurrent.ConcurrentSkipListMap;

public class AnchorNode {

	private ConcurrentSkipListMap<Integer, Connection> onlineNodes;


	public AnchorNode() {
		onlineNodes = new ConcurrentSkipListMap<>();
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

// ___________________________________________________________

	/**
	 * Write Operations
	 */
	
	

    /**
     * adds a node to list of all online nodes
     *
     * @param id - node id
	 * @param conn Connection to node
     */
	public synchronized void addNode(String id, Connection conn){
		onlineNodes.put(new Integer(id), conn);
	}

	/**
	 * remove node from list of online nodes
	 * @param id - node id
	 */
	public synchronized void removeNode(String id){
		onlineNodes.remove(new Integer(id));
	}

// _________________________________________________________

	/**
	 *  Read Operations
	 */

	/**
	 * Gets size of the onlineNodes list
	 *
	 * @return	- int, size of the onlineNodes list
	 */
	public synchronized int getNumOnline(){
		return onlineNodes.size();
	}

	/**
	 * Gets the connections for each online node
	 * @return - List of online node's connections
	 */
	public synchronized Collection<Connection> getOnlineConnections(){
		return this.onlineNodes.values();
	}
	/**
	 * Gets Map of onlineNodes
	 *
	 * @return - Map of onlineNodes
	 */
	public synchronized ConcurrentSkipListMap<Integer, Connection> getOnlineNodes(){
		return onlineNodes;
	}

	/**
	 * Determines if a user is online
	 * @param id - node id to check
	 * @return - true if online, false otherwise
	 */
	public synchronized boolean isOnline(String id){
		if(this.getOnlineNodes().isEmpty())
			return false;
		return this.getOnlineNodes().containsKey(new Integer(id));
	}

	/**
	 * Gets the next node
	 * @param ideal - current node
	 * @return - next node in map
	 */
	public synchronized String getNext(int ideal){
        if (this.getNumOnline() == 1) {
            return this.getOnlineNodes().firstKey().toString();
        } else {
            ConcurrentSkipListMap<Integer, Connection> tree = this.getOnlineNodes();
            Integer key = tree.higherKey(new Integer(ideal));
            if (key != null) {
                return key.toString();
            } else {
                return tree.firstKey().toString();
            }
        }
    }

	/**
	 * Gets the successor node
	 * @param ideal - ideal node id
	 * @return - actual node connection
	 */
	public synchronized Connection getSuccessor(int ideal){
		Integer id = new Integer(ideal);
        if (this.getNumOnline() == 1) {
            return this.getOnlineNodes().get(this.getOnlineNodes().firstKey());
        } else {
            ConcurrentSkipListMap<Integer, Connection> tree = this.getOnlineNodes();
            if(tree.containsKey(id)){
                return tree.get(id);
            }
            String key = tree.higherKey(id).toString();
            if (key != null) {
                return tree.get(new Integer(key));
            } else {
                return tree.get(tree.firstKey());
            }
        }
    }

	/**
	 * Displays all the online nodes
	 */
	public synchronized void showAllNode(){
		for(Integer node: this.getOnlineNodes().keySet())
		System.out.println("Node: " + node);
	}

	/**
	 * Gets the previous node
	 * @param id - current node
	 * @return - previous node in map
	 */
	public synchronized String getPrev(int id){
		if (this.getNumOnline() == 1){
			return id + "";
		} else{
			ConcurrentSkipListMap<Integer, Connection> tree = this.getOnlineNodes();
			Integer key = tree.lowerKey(new Integer(id));
			if(key != null){
				return key.toString();
			} else{
				return tree.lastKey().toString();
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