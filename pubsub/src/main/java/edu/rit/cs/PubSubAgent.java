package edu.rit.cs;


import java.util.ArrayList;

public abstract class PubSubAgent implements Publisher, Subscriber{

	/**
	 * Function to get a Topic based on a given string.
	 *
	 * @param check_string - string to check against Topic names
	 * @return Topic - matching topic, or null
	 */
	public static Topic topicExist(String check_string){
		return new Topic(new ArrayList<>(), "");
	};


	/**
	 * Function to subscribe to a given Topic.
	 * Most of the heavy work is handled in EventManager.
	 *
	 * @param topic - topic to subscribe to.
	 */
	@Override
	public void subscribe(Topic topic) {};

	/**
	 * Function to subscribe to a Topic based on a given keyword.
	 * Requires user input, as a keyword may have multiple Topics associated with it.
	 *
	 * @param keyword - keyword string to check for.
	 */
	@Override
	public void subscribe(String keyword) {};

	/**
	 * Function to unsubscribe from a Topic that has already been subscribed to.
	 *
	 * @param topic - topic to unsubscribe from
	 */
	@Override
	public void unsubscribe(Topic topic) {};

	/**
	 * Function to unsubscribe from all topics.
	 * Takes no parameters and has no return.
	 * Most of the heavy lifting is taken care of in EventManager.
	 */
	@Override
	public void unsubscribe() {};

	/**
	 * Function to list Topics that a User is subscribed to.
	 * Takes no parameters and has no returns.
	 * Receives information from EventManager to perform the operation.
	 */
	@Override
	public void listSubscribedTopics() {};

	/**
	 * Function to publish an Event.
	 * No return, but sends an Event to the EventManager to manage.
	 *
	 * @param event - Event object sent to the EventManager
	 */
	@Override
	public void publish(Event event) {};

	/**
	 * Function to advertise a new Topic.
	 * No return, but sends a Topic to be added to the list in EventManager.
	 *
	 * @param newTopic - Topic to be sent to EventManager
	 */
	@Override
	public void advertise(Topic newTopic) {};

}
