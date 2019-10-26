package edu.rit.cs.controller;


import edu.rit.cs.model.Event;
import edu.rit.cs.model.Topic;
import edu.rit.cs.model.User;
import edu.rit.cs.view.EventCLI;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class EventManager{

	private HashMap<String, User> subscribers;
	private HashMap<String, User> allUsers;
	private HashMap<String, Topic> topics;
	private HashMap<String, ArrayList<Event>> unNotified;
	private HashMap<String, List<Topic>> keyToTopics;
	private List<Event> newEvents;
	private List<Topic> advertise;


	public EventManager(){
		subscribers = new HashMap<>();
		allUsers = new HashMap<>();
		topics = new HashMap<>();
		unNotified = new HashMap<>();
		keyToTopics = new HashMap<>();
		newEvents = new ArrayList<>();
		advertise = new ArrayList<>();
	}
	//
	/**
	 * Start the repo service
	 *
	 **/
	private void startService() {
		Handler handler = new Handler();
		handler.start();
		EventCLI cli = new EventCLI();
		cli.startCLI(this, handler);
	}

	public void stopService(Handler h) {
		h.turnOff();
	}
// ___________________________________________________________

	/**
	 * Write Operations
	 */


	/**
	 * notify all subscribers of new event
	 *
	 * @param event - Event component
	 **/
	private synchronized void notifySubscribers(Event event) {
		newEvents.add(event);
	}
	
	/**
	 * add new topic when received advertisement of new topic
	 *
	 * @param topic - Topic component
	 **/
	private synchronized void addTopic(Topic topic){
		topics.put(topic.getName() + "", topic);
		for(String keyword: topic.getKeywords()){
			if(keyToTopics.containsKey(keyword)){
				keyToTopics.get(keyword).add(topic);
			}
			else{
				ArrayList<Topic> list = new ArrayList<>();
				list.add(topic);
				keyToTopics.put(keyword, list);
			}
		}
		advertise.add(topic);
	}

	private synchronized void add_removeSub(User user, boolean addOrRemove){
		if(addOrRemove){
			addSubscriber(user);
		}
		else{
			removeSubscriber(user);
		}
	}
	/**
	 * add subscriber to the internal list
	 *
	 * @param user - Subscriber instance of User component
	 **/
	private synchronized void addSubscriber(User user){
		subscribers.put(user.getId(), user);
	}
	
	/**
	 * remove subscriber from the list
	 *
	 * @param user - Subscriber instance of User
	 **/

	private synchronized void removeSubscriber(User user){
		subscribers.remove(user);
		for (Topic topic: topics.values()){
		    topic.removeSub(user.getId());
        }
	}

	private synchronized void subUnsubTopic(User user, Object obj, boolean subOrUnsub){

		if(obj instanceof Topic)
			if(subOrUnsub)
				subscribeToTopic(user, (Topic) obj);
			else
				unSubscribeFromTopic(user, (Topic) obj);
		else if(obj instanceof String){
			if(subOrUnsub)
				subscribeToTopic(user, (String) obj);
			else
				unSubscribeFromAll(user);
		}
	}

	/**
	 * adds a subscriber to a Topic
	 *
	 * @param user - Subscriber instance of User component
	 * @param topic - Topic component
	 **/

	private synchronized void subscribeToTopic(User user, Topic topic){
		this.topics.get(topic.getName()).addSub(user.getId(), user);
	}

	/**
	 * add a subscriber to Topics with a certain keyword
	 *
	 * @param user - Subscriber instance of User component
	 * @param keyword - keyword associated with Topics that the subscriber subscribes to
	 */
	private synchronized void subscribeToTopic(User user, String keyword){
		List<Topic> topicList = this.keyToTopics.get(keyword);

		for(Topic topic: topicList){
			this.topics.get(topic.getName()).addSub(user.getId(), user);
		}
	}

	/**
	 * Unsubscribe's a user from a Topic
	 *
	 * @param user - Subscriber instance of User component
	 * @param topic - Topic component
	 **/
	private synchronized void unSubscribeFromTopic(User user, Topic topic){
		this.topics.get(topic.getName()).removeSub(user.getId());
	}


	/**
	 * Unsubscribe's a user from all topics
	 * @param user
	 */
	private synchronized void unSubscribeFromAll(User user){
		for(Topic topic:topics.values()){
			if(topic.hasSub(user)){
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
	 * @param  topic - Topic component
	 **/
	private synchronized void showSubscribers(Topic topic){
		System.out.println("Subscribers:");
		for (String id: topic.getSubs().keySet()){
		    System.out.println("\t" + subscribers.get(id));
        }
	}

	public synchronized void showAllSubs(){
	    System.out.println("All Subscribers:");
	    for (User user: subscribers.values()){
            System.out.println("\t " + user);
        }
    }

	/**
	 * Returns a list of Topics that a User is subscribed to
	 *
	 * @param user - Subscriber instance of User
	 * @return - List of Subscribed Topics
	 */
	private synchronized ArrayList<Topic> getSubscribedTopics(User user){
		ArrayList<Topic> topicArrayList = new ArrayList<>();

		for(Topic topic:topics.values()){
			if(topic.hasSub(user)){
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
	private synchronized Collection<Topic> getTopicList(){
		return this.topics.values();
	}

	/**
	 * Returns the HashMap of all Topics
	 *
	 * @return - HashMap of all topics
	 */
	public synchronized HashMap<String, Topic> getTopicMap(){
		return this.topics;
	}

	/**
	 * Returns a list of all keywords
	 *
	 * @return - returns a list of all keywords
	 **/
	private synchronized Collection<String> getKeywords(){
		return this.keyToTopics.keySet();
	}

	private synchronized boolean userExists(String id){
		if(this.allUsers.isEmpty())
			return false;
		return this.allUsers.containsKey(id);
	}

	private synchronized User getUser(String id){
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

	/**
	 * Class to Handle the creations of all threads that communicate with clients
	 **/
	public class Handler extends Thread{
		// create a thread to look for new logins
		private HashMap<String, Worker> onlineUsers, onlinePublishers;
		private ArrayList<Worker> workers;
		boolean running;
		private NotifySubs notify;

		/**
		 * Constructor class for Handler, initialize onlineUsers maps
		 **/
		public Handler(){
			running = true;
			onlineUsers = new HashMap<>();
			onlinePublishers = new HashMap<>();
		}

		/**
		 * Starts a notify thread, and continuously hits new connections
		 *
		 **/
		public void run() {
			workers = new ArrayList<>();
			try {
				// start server
				int serverPort = 7896;
				ServerSocket listenSocket = new ServerSocket(serverPort);
				//System.out.println("TCP Server is running and accepting client connections...");

				// start notify thread
				notify = new NotifySubs();
				notify.start();

				// look for new connections, then pass it to worker thread
				while (running) {
					Socket clientSocket = listenSocket.accept();
					Worker c = new Worker(clientSocket);
					workers.add(c);
				}
			} catch (IOException e) {
				System.out.println("Listen :" + e.getMessage());
			}
		}

		/**
		 * Cleanly stops looking for new connections and closes open socket connections
		 **/
		public void turnOff(){
			this.turnOffWorkers();
			notify.turnOff();
			this.running = false;
		}

		/**
		 * Clean turns off all the running worker threads
		 */
		private void turnOffWorkers(){
			for (Worker work:workers) {
				work.turnOff();
			}
		}

		/**
		 * Class of Notifying Subscribers about events
		 **/
		private class NotifySubs extends Thread {
			boolean running;

			/**
			 * Constructor Class for NotifySubs
			 **/
			private NotifySubs(){
				running = true;
			}

			/**
			 * Continuously checks if there are events to send to online subscribers
			 * Then tells the workers what to send
			 **/
			public void run(){
				while(running){
					if(!advertise.isEmpty()){
						for(String id: onlinePublishers.keySet()){
							try {
								onlinePublishers.get(id).queueTopics(advertise);
								onlinePublishers.get(id).notify();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						for(String id: onlineUsers.keySet()){
							try {
								onlineUsers.get(id).queueTopics(advertise);
								onlineUsers.get(id).notify();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					// Updates a Subscriber with missed affects
					// Takes care of asynchronous event update
					if(!unNotified.isEmpty()){
						for (String id: unNotified.keySet()) {
							if(onlineUsers.containsKey(id)){
								try {
									onlineUsers.get(id).queueEvents(unNotified.get(id));
									onlineUsers.get(id).notify();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
					// Updates an onlineSubscriber when a new Event is publisheded
					if(!newEvents.isEmpty()){
						for (Event e:newEvents) {
							for(User user: topics.get(e.getTopic()).getSubs().values()){
								if(onlineUsers.containsKey(user.getId())){
									try {
										ArrayList<Event> events = new ArrayList<>();
										events.add(e);
										onlineUsers.get(user.getId()).queueEvents(events);
										onlineUsers.get(user.getId()).notify();
									} catch (IOException e1) {
										e1.printStackTrace();
									}
								}
								else{
									if(unNotified.containsKey(user.getId())){
										unNotified.get(user.getId()).add(e);
									}
									else{
										ArrayList<Event> events = new ArrayList<>();
										events.add(e);
										unNotified.put(user.getId(), events);
									}
								}
							}
						}
					}
				}
			}

			// stops the loop
			public void turnOff(){
				running = false;
			}
		}

		/**
		 * Class for Worker threads that handle the communication with clients
		 */
		public class Worker extends Thread {
			ObjectInputStream in;
			ObjectOutputStream out;
			Socket clientSocket;
			boolean running = false;
			String username = "";
			ArrayList<Event> eventsToSend;
			List<Topic> newTopics;

			/**
			 * Constructor class for Worker
			 * Sets up input and output streams, then starts the thread
			 *
			 * @param aClientSocket - socket connection to a Client
			 **/
			public Worker(Socket aClientSocket) {
				// Make a connection
				try {
					//System.out.println("Made a connection");
					clientSocket = aClientSocket;
					in = new ObjectInputStream(clientSocket.getInputStream());
					out = new ObjectOutputStream(clientSocket.getOutputStream());
					this.start();
				} catch (IOException e) {
					System.out.println("Connection:" + e.getMessage());
				}
			}

			/**
			 * Handles communication with client
			 **/
			@Override
			public void run() {
				try {
					boolean newUser = in.readBoolean();
					if(newUser){
						newLogin();
					}
					if(login()) {
						User user = getUser(username);
						out.writeObject(user);
						boolean sending = in.readObject().equals("true");
						if (sending) {

							out.writeObject(getTopicList());
							out.writeObject(getKeywords());

							if (user.isPub()) {
								receivedFromPub();
							} else if (user.isSub()) {
								receivedFromSub(user);
							}
						}
						else{
							if(user.isSub())
								add_removeSub(user, true);
							else
								onlinePublishers.put(user.getId(), this);
							running = true;
							while(running){
								wait();

								while(!eventsToSend.isEmpty()){
									out.writeObject(eventsToSend.remove(0));
								}
								while (!newTopics.isEmpty()) {
									out.writeObject(newTopics.remove(0));
								}
							}
							// wait till notified, then send Events
						}
					}
				} catch (EOFException e) {
					System.err.println("EOF:" + e.getMessage());
				} catch (IOException e) {
					System.err.println("IO:" + e.getMessage());
				} catch (ClassNotFoundException e){
					System.err.println("CLASS:" + e.getMessage());
				} catch (NullPointerException e){
					System.err.println("NULL: " + e.getMessage());
				} catch (InterruptedException e){
					System.err.println("INTERRUPT: " + e.getMessage());
				} finally {
					try {
						workers.remove(this);
						clientSocket.close();
					} catch (IOException e) {/*close failed*/}
				}
			}

			/**
			 * Validates a unique username, than adds the new user to list of subscribers
			 *
			 * @throws IOException
			 * @throws ClassNotFoundException
			 */
			public void newLogin() throws IOException, ClassNotFoundException{
				// loop till unique username is generated
				Object obj;
				String id;
				do {
					System.out.println("read obj");
					obj = in.readObject();
					id = (String) obj;
					System.out.println("write bool");
					out.writeObject(userExists(id));
				}while(userExists(id));

				System.out.println("new user");
				obj = in.readObject();
				User user = (User)obj;
				System.out.println("add to list");
				allUsers.put(user.getId(), user);
				if(user.isSub())
					add_removeSub(user, true);
			}

			/**
			 *
			 * @return true if username is in allUsers and the password matches the user
			 * @throws IOException
			 * @throws ClassNotFoundException
			 **/
			public boolean login() throws IOException, ClassNotFoundException{
				String id, password;
				Object obj;
				//login
				do{
					obj = in.readObject();
					id = (String) obj;
					out.writeObject(userExists(id));
				} while(!userExists(id));

				obj = in.readObject();
				password = (String) obj;
				setUsername(id);

				return userExists(id) && 	allUsers.get(id).isCorrectPassord(password);
			}

			/**
			 * Sets the username for this connections user
			 * @param username - unique String id for a User
			 */
			public void setUsername(String username) {
				this.username = username;
			}

			/**
			 * adds events to a list of events to send
			 *
			 * @param events - list of events to send to Subscriber
			 * @throws IOException
			 **/
			public void queueEvents(ArrayList<Event> events) throws IOException{
				eventsToSend = events;
			}

			/**
			 * adds events to a list of events to send
			 *
			 * @param topics - list of Topics to advertise
			 * @throws IOException
			 **/
			public void queueTopics(List<Topic> topics) throws IOException{
				newTopics = topics;
			}

			/**
			 * Reading input from a Publisher
			 *
			 * @throws IOException
			 * @throws ClassNotFoundException
			 */
			public void receivedFromPub() throws IOException, ClassNotFoundException{
				Object obj;
				obj = in.readObject();
				if (obj instanceof Event) {
					Event e = (Event) obj;
					notifySubscribers(e);
				} else if (obj instanceof Topic) {
					Topic t = (Topic) obj;
					addTopic(t);
				}
			}

			/**
			 * Reading input from Subscriber
			 * @param user
			 * @throws IOException
			 * @throws ClassNotFoundException
			 */
			public void receivedFromSub(User user) throws IOException, ClassNotFoundException{
				Object obj;
				obj = in.readObject();
				boolean subOrUnsubAction = in.readObject().equals("true");
				boolean listOrUnsubAll = in.readObject().equals("true");
				if(obj instanceof Topic) {
					Topic t = (Topic) obj;
					if (subOrUnsubAction) {
						if(listOrUnsubAll){
							out.writeObject(getSubscribedTopics(user));
						}else {
							subUnsubTopic(user, t, true);
						}
					}else {
						if(listOrUnsubAll){
							subUnsubTopic(user, "", false);
							unSubscribeFromAll(user);
						}
						else {
							subUnsubTopic(user, t, false);
						}
					}
				} else if(obj instanceof String){
					String key = (String) obj;
					subUnsubTopic(user, key, true);
				}
			}

			/**
			 * Turns off the connection and removes itself from the list of active workers
			 */
			public void turnOff(){
				try {
					running = false;
					workers.remove(this);
					clientSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}