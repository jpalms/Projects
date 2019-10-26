package edu.rit.cs.model;

import edu.rit.cs.model.Event;
import edu.rit.cs.model.Topic;

public interface Publisher {
	/*
	 * publish an event of a specific topic with title and content
	 */
	public void publish(Event event);
	
	/*
	 * advertise new topic
	 */
	public void advertise(Topic newTopic);
}
