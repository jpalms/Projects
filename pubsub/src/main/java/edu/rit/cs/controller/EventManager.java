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

public class EventManager {

	private HashMap<String, User> subscribers;
	private HashMap<String, User> allUsers;
	private HashMap<String, Topic> topics;
	private HashMap<String, ArrayList<Object>> unNotified;
	private HashMap<String, List<Topic>> keyToTopics;
	private List<Event> newEvents;
	private List<Topic> advertise;
	private HashMap<String, Handler.Worker> onlineUsers, onlinePublishers;


	public EventManager() {
		subscribers = new HashMap<>();
		allUsers = new HashMap<>();
		topics = new HashMap<>();
		unNotified = new HashMap<>();
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
		Handler handler = new Handler();
		handler.start();
		NotifySubs notifySubs = new NotifySubs(handler);
		notifySubs.start();
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
	private synchronized void addTopic(Topic topic) {
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

	private synchronized void add_removeSub(User user, boolean addOrRemove) {
		if (addOrRemove) {
			addSubscriber(user);
		} else {
			removeSubscriber(user);
		}
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

	private synchronized void subUnsubTopic(User user, Object obj, boolean subOrUnsub) {

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
	private synchronized void unSubscribeFromAll(User user) {
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
	private synchronized void showSubscribers(Topic topic) {
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
	private synchronized ArrayList<Topic> getSubscribedTopics(User user) {
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
	private synchronized ArrayList<Topic> getTopicList() {
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
	private synchronized ArrayList<String> getKeywords() {
		ArrayList<String> key = new ArrayList<>();
		for (String obj : keyToTopics.keySet()) {
			key.add(obj);
		}
		return key;
	}

	private synchronized boolean userExists(String id) {
		if (this.allUsers.isEmpty())
			return false;
		return this.allUsers.containsKey(id);
	}

	private synchronized User getUser(String id) {
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
	public class Handler extends Thread {
		// create a thread to look for new logins
		private ArrayList<Worker> workers;
		boolean running;
		private NotifySubs notify;
		private HashMap<String, Worker> sockets;

		/**
		 * Constructor class for Handler, initialize onlineUsers maps
		 **/
		public Handler() {
			running = true;
		}

		/**
		 * Starts a notify thread, and continuously hits new connections
		 **/
		public void run() {
			workers = new ArrayList<>();
			sockets = new HashMap<>();
			try {
				// start server
				int serverPort = 7896;
				ServerSocket listenSocket = new ServerSocket(serverPort);
				//System.out.println("TCP Server is running and accepting client connections...");

				// look for new connections, then pass it to worker thread
				while (running) {
					Socket clientSocket = listenSocket.accept();
					Worker c = new Worker(clientSocket);
					workers.add(c);
					//System.out.println(advertise.size());
				}
			} catch (IOException e) {
				System.out.println("Listen :" + e.getMessage());
			}
		}

		/**
		 * Cleanly stops looking for new connections and closes open socket connections
		 **/
		public void turnOff() {
			this.turnOffWorkers();
			notify.turnOff();
			this.running = false;
		}

		public int getWorkersSize() {
			return workers.size();
		}

		public ArrayList<Worker> getWorkers() {
			ArrayList<Worker> info = new ArrayList<>();
			for (int i = 0; i < workers.size() ; i++) {
				if (!workers.get(i).isAlive()) {
					info.add(workers.get(i));
					workers.remove(workers.get(i));
				}
			}
			return info;
		}

		public int getSocketsSize(){
			return sockets.size();
		}

		public HashMap<String, Worker> getSockets(){
			return sockets;
		}

		/**
		 * Clean turns off all the running worker threads
		 */
		private void turnOffWorkers() {
			for (Worker work : workers) {
				work.turnOff();
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
			ArrayList<Event> eventsToSend = new ArrayList<>();
			List<Object> newTopics = new ArrayList<>();
			private Object info = new Object();

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
					boolean newUser = in.readObject().equals("true");
					if (newUser) {
						newLogin();
					}
					if (login()) {
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
							this.clientSocket.close();
							//workers.remove(this);
						} else {
							if (user.isSub())
								onlineUsers.put(user.getId(), this);//add_removeSub(user, true);
							else
								onlinePublishers.put(user.getId(), this);
							//this.clientSocket.close();
							sockets.put(username, this);
							}
						}
				} catch (EOFException e) {
					System.err.println("EOF:" + e.getMessage());
				} catch (IOException e) {
					System.err.println("IO:" + e.getMessage());
				} catch (ClassNotFoundException e) {
					System.err.println("CLASS:" + e.getMessage());
				} catch (NullPointerException e) {
					System.err.println("NULL: " + e.getMessage());
				} finally {
				}
			}

			/**
			 * Validates a unique username, than adds the new user to list of subscribers
			 *
			 * @throws IOException
			 * @throws ClassNotFoundException
			 */
			public void newLogin() throws IOException, ClassNotFoundException {
				// loop till unique username is generated
				Object obj;
				String id;
				do {
					System.out.println("read obj");
					obj = in.readObject();
					id = (String) obj;
					System.out.println("write bool");
					out.writeObject(userExists(id) + "");
				} while (userExists(id));

				System.out.println("new user");
				obj = in.readObject();
				User user = (User) obj;
				System.out.println("add to list");
				allUsers.put(user.getId(), user);
				if (user.isSub())
					add_removeSub(user, true);
			}

			/**
			 * @return true if username is in allUsers and the password matches the user
			 * @throws IOException
			 * @throws ClassNotFoundException
			 **/
			public boolean login() throws IOException, ClassNotFoundException {
				String id, password;
				Object obj;
				//login
				do {
					obj = in.readObject();
					id = (String) obj;
					out.writeObject(userExists(id) + "");
				} while (!userExists(id));

				obj = in.readObject();
				password = (String) obj;
				setUsername(id);

				return userExists(id) && getUser(id).isCorrectPassord(password);
			}

			/**
			 * Sets the username for this connections user
			 *
			 * @param username - unique String id for a User
			 */
			public void setUsername(String username) {
				this.username = username;
			}

			public synchronized Object newInfo() {
				return info;
			}

			/**
			 * adds events to a list of events to send
			 *
			 * @param events - list of events to send to Subscriber
			 * @throws IOException
			 **/
			public synchronized void queueEvents(ArrayList<Event> events) throws IOException {
				eventsToSend = events;
			}

			/**
			 * adds events to a list of events to send
			 *
			 * @param topics - list of Topics to advertise
			 * @throws IOException
			 **/
			public synchronized void queueTopics(ArrayList<Object> topics) throws IOException {
				newTopics = topics;
			}

			public synchronized void queueBoth(ArrayList<Object> objects) throws IOException {
				ArrayList<Event> events = new ArrayList<>();
				ArrayList<Object> topicArrayList = new ArrayList<>();
				for (Object obj : objects) {
					if (obj instanceof Event) {
						Event e = (Event) obj;
						events.add(e);
					} else {
						Topic t = (Topic) obj;
						topicArrayList.add(t);
					}
				}

				queueEvents(events);
				queueTopics(topicArrayList);
			}

			/**
			 * Reading input from a Publisher
			 *
			 * @throws IOException
			 * @throws ClassNotFoundException
			 */
			public void receivedFromPub() throws IOException, ClassNotFoundException {
				Object obj;
				obj = in.readObject();
				if (obj instanceof Event) {
					Event e = (Event) obj;
					notifySubscribers(e);
					this.info = e;
				} else if (obj instanceof Topic) {
					Topic t = (Topic) obj;
					addTopic(t);
					this.info = t;
				}
			}

			/**
			 * Reading input from Subscriber
			 *
			 * @param user
			 * @throws IOException
			 * @throws ClassNotFoundException
			 */
			public void receivedFromSub(User user) throws IOException, ClassNotFoundException {
				Object obj;
				obj = in.readObject();
				boolean subOrUnsubAction = in.readObject().equals("true");
				boolean listOrUnsubAll = in.readObject().equals("true");
				if (obj instanceof Topic) {
					Topic t = (Topic) obj;
					if (subOrUnsubAction) {
						if (listOrUnsubAll) {
							System.out.println("send to Sub");
							out.writeObject(getSubscribedTopics(user));
						} else {
							subUnsubTopic(user, t, true);
						}
					} else {
						if (listOrUnsubAll) {
							subUnsubTopic(user, "", false);
							unSubscribeFromAll(user);
						} else {
							subUnsubTopic(user, t, false);
						}
					}
				} else if (obj instanceof String) {
					String key = (String) obj;
					subUnsubTopic(user, key, true);
				}
			}

			public void sendObj() throws IOException {
				while(!eventsToSend.isEmpty()){
					out.writeObject(eventsToSend.remove(0));
				}
				while(!newTopics.isEmpty()){
					out.writeObject(newTopics.remove(0));
				}
			}
			/**
			 * Turns off the connection and removes itself from the list of active workers
			 */
			public void turnOff() {
				try {
					workers.remove(this);
					onlinePublishers.remove(username);
					onlineUsers.remove(username);
					clientSocket.close();
					running = false;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			public void notifySoc() {
				this.notify();
			}
		}
	}

	/**
	 * Class of Notifying Subscribers about events
	 **/
	private class NotifySubs extends Thread {
		private boolean running;
		private EventManager.Handler handler;

		/**
		 * Constructor Class for NotifySubs
		 **/
		private NotifySubs(Handler handler) {
			this.running = true;
			this.handler = handler;
		}

		/**
		 * Continuously checks if there are events to send to online subscribers
		 * Then tells the workers what to send
		 **/
		public void run() {
			System.out.println("Server is running ...");
			while (running) {
                System.out.print("");
				if (handler.getSocketsSize() > 0 && handler.getWorkersSize() > 0) {
					ArrayList<Handler.Worker> workers = handler.getWorkers();
					ArrayList<Object> infoToSend = new ArrayList<>();
					HashMap<String, Handler.Worker> sockets = handler.getSockets();

					//populate infoToSend
					for (Handler.Worker worker : workers) {
						if (worker.newInfo() instanceof Event || worker.newInfo() instanceof Topic) {
							infoToSend.add(worker.newInfo());
							System.out.println((Topic)infoToSend.get(0));
						}
					}
					if(!infoToSend.isEmpty()){
                        System.out.println("info");
                        ArrayList<Object> topicArrayList = new ArrayList<>();
                        for(Object obj: infoToSend){
                            if(obj instanceof Topic){
                                topicArrayList.add(obj);
                            }
                        }
						for(String id: allUsers.keySet()){
							// online
							if(sockets.containsKey(id)){
								if(allUsers.get(id).isSub()){
									try {
                                        System.out.println("Send to Sub");
										sockets.get(id).queueBoth(infoToSend);

										if(unNotified.containsKey(id)){
										    sockets.get(id).queueBoth(unNotified.remove(id));
                                        }
										sockets.get(id).sendObj();
									} catch (IOException e) {
										sockets.get(id).turnOff();
										sockets.remove(id);
										//unNotified
										unNotified(id, infoToSend, topicArrayList);
									}
								}
								else if(allUsers.get(id).isPub()){
									try {
                                        System.out.println("Send to Pub");
                                        sockets.get(id).queueTopics(infoToSend);

                                        if(unNotified.containsKey(id)){
                                            sockets.get(id).queueBoth(unNotified.remove(id));
                                        }

										sockets.get(id).sendObj();
									} catch (IOException e) {
										sockets.get(id).turnOff();
										sockets.remove(id);
										//unNotified
										unNotified(id, infoToSend, topicArrayList);
									}
								}
							}
							// offline
							else {
								//unNotified
								unNotified(id, infoToSend, topicArrayList);
							}
						}
					}
				}
			}

		}

			public void unNotified(String id, ArrayList<Object> infoToSend, ArrayList<Object> topicArrayList){
				if(unNotified.containsKey(id)){
					if(allUsers.get(id).isSub()) {
						for (Object obj : infoToSend)
							unNotified.get(id).add(obj);
					}
					else{
						for (Object topic: topicArrayList)
							unNotified.get(id).add(topic);
					}
				}
				else{
					if(allUsers.get(id).isSub())
						unNotified.put(id, infoToSend);
					else
						unNotified.put(id, topicArrayList);
				}
			}
			// stops the loop
			public void turnOff () {
				running = false;
			}
		}
	}