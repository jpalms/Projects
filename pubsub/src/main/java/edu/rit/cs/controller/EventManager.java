package edu.rit.cs.controller;


import edu.rit.cs.model.Event;
import edu.rit.cs.model.Topic;
import edu.rit.cs.model.User;
import edu.rit.cs.view.EventCLI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventManager {

	private HashMap<String, User> subscribers;
	private HashMap<String, User> allUsers;
	private HashMap<String, Topic> topics;
	private HashMap<String, List<Topic>> keyToTopics;
	private List<Event> newEvents;
	private List<Topic> advertise;
	private HashMap<String, User> onlineUsers, onlinePublishers;


	public EventManager() {
		subscribers = new HashMap<>();
		allUsers = new HashMap<>();
		topics = new HashMap<>();
		keyToTopics = new HashMap<>();
		newEvents = new ArrayList<>();
		advertise = new ArrayList<>();
		onlinePublishers = new HashMap<>();
		onlineUsers = new HashMap<>();
	}
	//

	/**
	 * Start the repo service
	 **/
	private void startService() {
		Handler handler = new Handler(this);
		handler.start();
		NotifyAll notifyAll = new NotifyAll(this, handler);
		notifyAll.start();
		EventCLI cli = new EventCLI();
		cli.startCLI(this, handler);
	}

	/**
	 *  Stop the repo service
	 * @param h Handler instance that takes care of shutdown
	 */
	public void stopService(Handler h) {
		h.turnOff();
	}
// ___________________________________________________________

	/**
	 * Write Operations
	 */

	public synchronized void on_offlineUser(String id, User user, boolean on_off){
		if(on_off){
			if(user.isSub())
				onlineUsers.put(id, user);
			else
				onlinePublishers.put(id, user);
		}else {
			onlineUsers.remove(id);
			onlinePublishers.remove(id);
		}
	}
	/**
	 * notify all subscribers of new event
	 *
	 * @param event - Event component
	 **/
	public synchronized void notifySubscribers(Event event) {
		newEvents.add(event);
	}

	/**
	 * add new topic when received advertisement of new topic
	 *
	 * @param topic - Topic component
	 **/
	public synchronized void addTopic(Topic topic) {
		topics.put(topic.getName() + "", topic);
		for (String keyword : topic.getKeywords()) {
			if (keyToTopics.containsKey(keyword)) {
				keyToTopics.get(keyword).add(topic);
			} else {
				ArrayList<Topic> list = new ArrayList<>();
				list.add(topic);
				keyToTopics.put(keyword, list);
			}
		}
		advertise.add(topic);
	}

	/**
	 * Either adds or removes a subscriber from the internal list
	 * Useful to use with TCPClient inputs
	 *
	 * @param user 			Passed user associated UserCLI node
	 * @param addOrRemove 	determines which function to execute
	 *                      	true = add, false = remove
	 */
	public synchronized void add_removeSub(User user, boolean addOrRemove) {
		if (addOrRemove) {
			addSubscriber(user);
		} else {
			removeSubscriber(user);
		}
	}

	public synchronized void addUser(User user){
		allUsers.put(user.getId(), user);
	}

	/**
	 * add subscriber to the internal list
	 *
	 * @param user - Subscriber instance of User component
	 **/
	private synchronized void addSubscriber(User user) {
		subscribers.put(user.getId(), user);
	}

	/**
	 * remove subscriber from the list
	 *
	 * @param user - Subscriber instance of User
	 **/

	private synchronized void removeSubscriber(User user) {
		subscribers.remove(user);
		for (Topic topic : topics.values()) {
			topic.removeSub(user.getId());
		}
	}

	public synchronized HashMap<String, User> getAllUsers(){
		return allUsers;
	}

	/**
	 * Either subscribes or unsubscribes a User from a given Topic
	 * Useful to use with TCPClient inputs
	 *
	 * @param user 			User to be added / removed from Topic's subscribed list
	 * @param obj 			Object input to account for different forms of functions
	 * @param subOrUnsub 	boolean to determine function performed
	 *                      	true = subscribe, false = unsubscribe
	 *
	 */
	public synchronized void subUnsubTopic(User user, Object obj, boolean subOrUnsub) {

		if (obj instanceof Topic)
			if (subOrUnsub)
				subscribeToTopic(user, (Topic) obj);
			else
				unSubscribeFromTopic(user, (Topic) obj);
		else if (obj instanceof String) {
			if (subOrUnsub)
				subscribeToTopic(user, (String) obj);
			else
				unSubscribeFromAll(user);
		}
	}

	/**
	 * adds a subscriber to a Topic
	 *
	 * @param user  - Subscriber instance of User component
	 * @param topic - Topic component
	 **/

	private synchronized void subscribeToTopic(User user, Topic topic) {
		this.topics.get(topic.getName()).addSub(user.getId(), user);
	}

	/**
	 * add a subscriber to Topics with a certain keyword
	 *
	 * @param user    - Subscriber instance of User component
	 * @param keyword - keyword associated with Topics that the subscriber subscribes to
	 */
	private synchronized void subscribeToTopic(User user, String keyword) {
		List<Topic> topicList = this.keyToTopics.get(keyword);

		for (Topic topic : topicList) {
			this.topics.get(topic.getName()).addSub(user.getId(), user);
		}
	}

	/**
	 * Unsubscribe's a user from a Topic
	 *
	 * @param user  - Subscriber instance of User component
	 * @param topic - Topic component
	 **/
	private synchronized void unSubscribeFromTopic(User user, Topic topic) {
		this.topics.get(topic.getName()).removeSub(user.getId());
	}


	/**
	 * Unsubscribe's a user from all topics
	 *
	 * @param user
	 */
	public synchronized void unSubscribeFromAll(User user) {
		for (Topic topic : topics.values()) {
			if (topic.hasSub(user)) {
				unSubscribeFromTopic(user, topic);
			}
		}

	}

// _________________________________________________________

	/**
	 *  Read Operations
	 */


	/**
	 * show the list of subscriber for a specified topic
	 *
	 * @param topic - Topic component
	 **/
	public synchronized void showSubscribers(Topic topic) {
		System.out.println("Subscribers:");
		for (String id : topic.getSubs().keySet()) {
			System.out.println("\t" + subscribers.get(id));
		}
	}

	/**
	 * show all subscribers
	 */
	public synchronized void showAllSubs() {
		System.out.println("All Subscribers:");
		for (User user : subscribers.values()) {
			System.out.println("\t " + user.toString());
		}
	}

	/**
	 * Returns a list of Topics that a User is subscribed to
	 *
	 * @param user - Subscriber instance of User
	 * @return - List of Subscribed Topics
	 */
	public synchronized ArrayList<Topic> getSubscribedTopics(User user) {
		ArrayList<Topic> topicArrayList = new ArrayList<>();

		for (Topic topic : topics.values()) {
			if (topic.hasSub(user)) {
				topicArrayList.add(topic);
			}
		}

		return topicArrayList;
	}

	/**
	 * Returns a list of all Topics
	 *
	 * @return - list of all topics
	 */
	public synchronized ArrayList<Topic> getTopicList() {
		ArrayList<Topic> list = new ArrayList();
		for (Topic top : topics.values())
			list.add(top);
		return list;
	}

	/**
	 * Returns the HashMap of all Topics
	 *
	 * @return - HashMap of all topics
	 */
	public synchronized HashMap<String, Topic> getTopicMap() {
		return this.topics;
	}

	/**
	 * Returns a list of all keywords
	 *
	 * @return - returns a list of all keywords
	 **/
	public synchronized ArrayList<String> getKeywords() {
		ArrayList<String> key = new ArrayList<>();
		for (String obj : keyToTopics.keySet()) {
			key.add(obj);
		}
		return key;
	}

	/**
	 * Helper function to see whether the given User exists in the internal list
	 *
	 * @param id 		name of User
	 * @return			boolean describing existence of User
	 */
	public synchronized boolean userExists(String id) {
		HashMap<String, User> temp = getAllUsers();
		if (temp.isEmpty())
			return false;
		return temp.containsKey(id);
	}

	/**
	 * Gets one user from the internal list based on internal list
	 *
	 * @param id		name of User
	 * @return			User that was searched for
	 */
	public synchronized User getUser(String id) {
		return allUsers.get(id);
	}

	/**
	 * Main function which is run and starts the program
	 *
	 * @param args
	 **/
	public static void main(String[] args) {
		new EventManager().startService();
		// start command line
	}
}