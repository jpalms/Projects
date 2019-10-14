package edu.rit.cs;


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
	private void notifySubscribers(Event event) {
		//topics.get(event.getTopic().getId()).getSubs();
	}
	
	/*
	 * add new topic when received advertisement of new topic
	 */
	private void addTopic(Topic topic){
		topics.put(topic.getId() + "", topic);
	}
	
	/*
	 * add subscriber to the internal list
	 */
	private void addSubscriber(User user){
		subscribers.put(user.getId(), user);
	}
	
	/*
	 * remove subscriber from the list
	 */
	private void removeSubscriber(Object user){
		subscribers.remove(user);
		//for (Topic topic: topics.

	}
	
	/*
	 * show the list of subscriber for a specified topic
	 */
	private void showSubscribers(Topic topic){
		for (User user: subscribers.values()) {
			//if(user.getTopic().equals(topic))
				System.out.println(user);
		}
	}
	
	
	public static void main(String[] args) {
		new EventManager().startService();
	}


}
