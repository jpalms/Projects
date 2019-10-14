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
import java.util.Map;

public class EventManager{

	private HashMap<String, User> subscribers;
	private HashMap<String, Topic> topics;
	/*
	 * Start the repo service
	 */
	private void startService() {
		// create a thread to look for new logins
		ArrayList<Connection> connections = new ArrayList<>();
		try {
			// start server
			int serverPort = 7896;
			ServerSocket listenSocket = new ServerSocket(serverPort);
			//System.out.println("TCP Server is running and accepting client connections...");

			// wait till all the partitions have been taken by clients
			while (true) {
				Socket clientSocket = listenSocket.accept();
				Connection c = new Connection(clientSocket);
				connections.add(c);
			}
		} catch (IOException e) {
			System.out.println("Listen :" + e.getMessage());
		}
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

	static class Connection extends Thread {
		ObjectInputStream in;
		ObjectOutputStream out;
		Socket clientSocket;

		public Connection(Socket aClientSocket) {
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

		public void run() {
			char publish = 'p', advertise = 'a';
			try {   // send word to node, retrieve result
				User user = (User)in.readObject();
				if(user.isSub()){
					// notify missed events
				}
				while(true){
					Object obj;
					if(user.isPub()){
						char method = in.readChar();
						obj = in.readObject();
						if(method == publish){
							Event e = (Event) obj;

						}
						else if(method == advertise){
							 Topic t = (Topic) obj;

						}
					}
				}
			} catch (EOFException e) {
				System.out.println("EOF:" + e.getMessage());
			} catch (IOException e) {
				System.out.println("IO:" + e.getMessage());
			} catch (ClassNotFoundException e){
				System.out.println("CLASS:" + e.getMessage());
			} catch (NullPointerException e){
				System.out.println("NULL: " + e.getMessage());
			} finally {
				try {
					clientSocket.close();
				} catch (IOException e) {/*close failed*/}
			}
		}
	}

}
