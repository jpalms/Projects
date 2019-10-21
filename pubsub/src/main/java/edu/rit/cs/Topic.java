package edu.rit.cs;

import java.io.Serializable;
import java.util.List;
import java.util.HashMap;

/**
 * Class of Topics which are associated with Events
 */
public class Topic implements Serializable {
	private List<String> keywords;
	private String name;
	private HashMap<String, User> subs;

	/**
	 * Constructor for Topic class
	 *
	 * @param keywords - words to associate with this Topic
	 * @param name - unique identifier
	 */
	public Topic(List<String> keywords, String name){
		this.keywords = keywords;
		this.name = name;
		this.subs = new HashMap<>();
	}

	/**
	 * Getter Method for subs
	 *
	 * @return - HashMap of all subscribers that are subscribed to this Topic
	 */
	public HashMap<String, User> getSubs() {
		return subs;
	}

	/**
	 * Determines if a user is subscribed to this Topic
	 *
	 * @param user - Subscriber instance of User
	 * @return - true if subscribed, false otherwise
	 */
	public boolean hasSub(User user){
		return this.subs.containsKey(user.getId());
	}

	/**
	 * Add subscriber to list of subs subscribed to this Topic
	 *
	 * @param id - User unique id
	 * @param subscriber - subscriber instance of User
	 */
	public void addSub(String id, User subscriber){
		subs.put(id, subscriber);
	}

	/**
	 * Removes a subscriber from the list of subs subscribed
	 *
	 * @param id - User unique id
	 */
	public void removeSub(String id){
		subs.remove(id);
	}

	/**
	 * Getter method for name
	 *
	 * @return - returns nume of the Topic
	 */
	public String getName() {
		return name;
	}

	/**
	 * Getter method of Keywords
	 *
	 * @return - returns the list of keywords use to associate with this Topic
	 */
	public List<String> getKeywords() {
		return keywords;
	}

	/**
	 * toString function
	 *
	 * @return - String representation of Topic
	 */
	@Override
	public String toString(){
		return "Topic: " + this.getName() + "\n\tkeywords: " + this.getKeywords().toString();
	}

	/**
	 * equals function
	 *
	 * @param obj - Object to compare
	 * @return - true if same Object, false otherwise
	 */
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof Topic))
			return false;

		Topic topic = (Topic) obj;
		return this.getName().equals(topic.getName());
	}



}
