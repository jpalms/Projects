package edu.rit.cs;


import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventManager{

	private HashMap<String, User> subscribers;
	private HashMap<String, Topic> topics;
	private HashMap<String, ArrayList<Event>> unNotified;
	private List<Event> newEvents;

	//@todo fill unotified
	//
	/*
	 * Start the repo service
	 */
	private void startService() {
		Handler handler = new Handler();
		handler.start();
		// run command line interface
	}

	/*
	 * notify all subscribers of new event 
	 */
	private synchronized void notifySubscribers(Event event) {
		//topics.get(event.getTopic().getId()).getSubs();
		newEvents.add(event);
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
		System.out.println("Subscribers:");
		for (String id: topic.getSubs().keySet()){
		    System.out.println("\t" + subscribers.get(id));
        }
	}

	private synchronized ArrayList<Event> notifyMissedSubscriber(User user){
		if(unNotified.containsKey(user)){
			return unNotified.remove(user);
		}
		return new ArrayList<>();
	}

	private synchronized void subscribeToTopic(User user, Topic topic){
		this.topics.get(topic.getId()).addSub(user.getId(), user);
	}

	private synchronized void unSubscribeFromTopic(User user, Topic topic){
		this.topics.get(topic.getId()).removeSub(user.getId());
	}

	public static void main(String[] args) {
		new EventManager().startService();
	}

	public class Handler extends Thread{
		// create a thread to look for new logins
		private HashMap<String, Worker> onlineUsers;
		private ArrayList<Worker> workers;
		boolean running;
		private NotifySubs notify;
		public Handler(){
			running = true;
			onlineUsers = new HashMap<>();
		}

		public void run() {
			workers = new ArrayList<>();
			try {
				// start server
				int serverPort = 7896;
				ServerSocket listenSocket = new ServerSocket(serverPort);
				//System.out.println("TCP Server is running and accepting client connections...");

				notify = new NotifySubs();
				notify.start();

				while (running) {
					Socket clientSocket = listenSocket.accept();
					Worker c = new Worker(clientSocket);
					workers.add(c);
				}
			} catch (IOException e) {
				System.out.println("Listen :" + e.getMessage());
			}
		}

		public void turnOff(){
			this.turnOffWorkers();
			notify.turnOff();
			this.running = false;
		}
		private void turnOffWorkers(){
			for (Worker work:workers) {
				work.turnOff();
			}
		}

		public class NotifySubs extends Thread {
			boolean running;

			public NotifySubs(){
				running = true;
			}

			public void run(){
				while(running){
					if(!newEvents.isEmpty()){
						for (Event e:newEvents) {
							for(User user: topics.get(e.getTopic()).getSubs()){
								if(onlineUsers.containsKey(user.getId())){
									try {
										onlineUsers.get(user.getId()).sendNewEvent(e);
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

			public void turnOff(){
				running = false;
			}
		}

		public class Worker extends Thread {
			ObjectInputStream in;
			ObjectOutputStream out;
			Socket clientSocket;
			boolean notify = false, running = false;
			String username = "";

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

			@Override
			public void run() {
				try {
					Object obj;
					boolean newUser = in.readBoolean();
					if(newUser){
						newLogin();
					}
					if(login()) {
						boolean sending = in.readBoolean();
						User user = subscribers.get(username);
						if (sending) {
							user = subscribers.get(username);//(User) in.readObject();

							if (user.isPub()) {
								recieveFromPub();
							} else if (user.isSub()) {
								recievedFromSub(user);
							}
						}
						else{
							onlineUsers.put(user.getId(), this);
							running = true;
							while(running){
								wait();
							}
							// wait till get info to send
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

			public void newLogin() throws IOException, ClassNotFoundException{
				// loop till unique username is generated
				Object obj;
				String id;
				do {
					obj = in.readObject();
					id = (String) obj;
				}while(!subscribers.containsKey(id));

				obj = in.readObject();
				User user = (User)obj;

				subscribers.put(user.getId(), user);
			}

			public boolean login() throws IOException, ClassNotFoundException{
				String id, password;
				Object obj;
				//login
				obj = in.readObject();
				id = (String) obj;
				obj = in.readObject();
				password = (String) obj;
				setUsername(id);
				// ask partner if this should loop till password is correct

				return subscribers.containsKey(id) && subscribers.get(id).isCorrectPassord(password);
			}

			public void setUsername(String username) {
				this.username = username;
			}

			public void sendMissedEvents(User user) throws IOException{
				// notify missed events
				ArrayList<Event> events = notifyMissedSubscriber(user);
				while(!events.isEmpty()){
					out.writeObject(events.remove(0));
				}
				out.writeObject("DONE");
			}

			public void sendNewEvent(Event e) throws IOException{
				out.writeObject(e);
			}

			public void recieveFromPub() throws IOException, ClassNotFoundException{
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

			public void recievedFromSub(User user) throws IOException, ClassNotFoundException{
				Object obj;
				obj = in.readObject();
				Topic t = (Topic) obj;
				boolean subscribe = in.readBoolean();
				if(subscribe){
					subscribeToTopic(user, t);
				}
				else{
					unSubscribeFromTopic(user, t);
				}

			}

			public void notifySub(){
				notify = true;
			}

			public void turnOff(){
				try {
					running = true;
					workers.remove(this);
					clientSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
