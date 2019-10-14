package edu.rit.cs;


import java.util.ArrayList;
import java.util.HashMap;

public class EventManager{

	private HashMap<String, User> subscribers;
	private HashMap<String, Topic> topics;

	/*
	 * Start the repo service
	 */
	private void startService() {
		// create a thread to look for new logins

		//
	}

	/*
	 * notify all subscribers of new event 
	 */
	private synchronized void notifySubscribers(Event event) {
		//topics.get(event.getTopic().getId()).getSubs();
	}
	
	/*
	 * add new topic when received advertisement of new topic
	 */
	private synchronized void addTopic(Topic topic){
		topics.put(topic.getId() + "", topic);
	}
	
	/*
	 * add subscriber to the internal list
	 */
	private synchronized void addSubscriber(User user){
		subscribers.put(user.getId(), user);
	}
	
	/*
	 * remove subscriber from the list
	 */

	private synchronized void removeSubscriber(User user){
		subscribers.remove(user);
		for (Topic topic: topics.values()){
		    topic.removeSub(user.getId());
        }


	}
	
	/*
	 * show the list of subscriber for a specified topic
	 */
	private synchronized void showSubscribers(Topic topic){
		for (String id: topic.getSubs().keySet()){
		    System.out.println(subscribers.get(id));
        }
	}
	
	
	public static void main(String[] args) {
		new EventManager().startService();
	}


}
