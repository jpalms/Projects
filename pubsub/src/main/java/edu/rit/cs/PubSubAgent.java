package edu.rit.cs;


public abstract class PubSubAgent implements Publisher, Subscriber{

	@Override
	public void subscribe(Topic topic) {

		/*
		topic.
		 */
		
	}

	@Override
	public void subscribe(String keyword) {

	}

	@Override
	public void unsubscribe(Topic topic) {
	}

	@Override
	public void unsubscribe() {
	}

	@Override
	public void listSubscribedTopics() {

	}

	@Override
	public void publish(Event event) {

	}

	@Override
	public void advertise(Topic newTopic) {

	}


	
}
