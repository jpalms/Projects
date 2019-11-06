package edu.rit.cs.model;

import java.io.Serializable;

/**
 * Class of Events that Publisher's publish and Subscriber's read
 */
public class Event implements Serializable {
	private Topic topic;
	private String title;
	private String content;

	/**
	 * Constructor for Event class
	 *
	 * @param topic - topic associated with an Event
	 * @param title - title of an Event
	 * @param content - the content of an Event
	 */
    public Event(Topic topic, String title, String content) {
        this.topic = topic;
        this.title = title;
        this.content = content;
    }

	/**
	 * Getter method for Topic
	 *
	 * @return - returns the Topic associated with the Event
	 */
	public Topic getTopic() {
        return topic;
    }

	/**
	 * Getter method for title
	 *
	 * @return - returns the String of the Event's title
	 */
	public String getTitle() {
        return title;
    }

	/**
	 * Getter method for content
	 *
	 * @return - returns a String that contain the Event's information
	 */
	public String getContent() {
        return content;
    }

	/**
	 * Setter method for content
	 *
	 * @param content - information about the Event
	 */
	public void setContent(String content) {
        this.content = content;
    }

    /**
     * toString function
     *
     * @return - String representation of Topic
     */
	@Override
	public String toString() {
		return "Event: " + this.getTitle() +
				"\n\t" + this.getTopic() +
				"\n\tcontent: " + this.getContent();
		}
}
