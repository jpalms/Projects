package edu.rit.cs.controller;

import edu.rit.cs.model.Event;
import edu.rit.cs.model.Topic;
import edu.rit.cs.model.User;

import java.util.ArrayList;
import java.util.HashMap;

import java.io.IOException;

/**
 * Class of Notifying Subscribers about events
 **/
public class NotifyAll extends Thread {
    private boolean running;
    private EventManager.Handler handler;
    private HashMap<String, ArrayList<Object>> unNotified;
    private EventManager eventManager;


    /**
     * Constructor Class for NotifySubs
     **/
    public NotifyAll(EventManager em, EventManager.Handler handler) {
        this.running = true;
        this.handler = handler;
        unNotified = new HashMap<>();
        this.eventManager = em;
    }

    /**
     * Continuously checks if there are events to send to online subscribers
     * Then tells the workers what to send
     **/
    public void run() {
        System.out.println("Server is running ...");
        HashMap<String, User> allUsers;
        while (running) {
            System.out.print("");
            allUsers = eventManager.getAllUsers();
            if (handler.getSocketsSize() > 0 && handler.getWorkersSize() > 0) {
                ArrayList<EventManager.Handler.Worker> workers = handler.getWorkers();
                ArrayList<Object> infoToSend = new ArrayList<>();
                HashMap<String, EventManager.Handler.Worker> sockets = handler.getSockets();

                //populate infoToSend
                for (EventManager.Handler.Worker worker : workers) {
                    if (worker.newInfo() instanceof Event || worker.newInfo() instanceof Topic) {
                        infoToSend.add(worker.newInfo());
                    }
                }
                if(true/*!infoToSend.isEmpty() || change*/){
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
                                ArrayList<Object> temp = new ArrayList<>();
                                for(Object obj:infoToSend){
                                    if(obj instanceof Event) {
                                        if (((Event) obj).getTopic().hasSub(allUsers.get(id))) {
                                            temp.add(obj);
                                        }
                                    }
                                }
                                try {
                                    sockets.get(id).queueEvents(temp);
                                    sockets.get(id).queueTopics(topicArrayList);

                                    if(unNotified.containsKey(id)){
                                        sockets.get(id).queueBoth(unNotified.remove(id));
                                    }
                                    sockets.get(id).sendObj();
                                } catch (IOException e) {
                                    sockets.get(id).turnOff();
                                    sockets.remove(id);
                                    //unNotified
                                    unNotified(id, temp, topicArrayList, allUsers);
                                }
                            }
                            else if(allUsers.get(id).isPub()){
                                try {
                                    sockets.get(id).queueTopics(topicArrayList);

                                    if(unNotified.containsKey(id)){
                                        sockets.get(id).queueBoth(unNotified.remove(id));
                                    }

                                    sockets.get(id).sendObj();
                                } catch (IOException e) {
                                    sockets.get(id).turnOff();
                                    sockets.remove(id);
                                    //unNotified
                                    unNotified(id, infoToSend, topicArrayList, allUsers);
                                }
                            }
                        }
                        // offline
                        else {
                            //unNotified
                            unNotified(id, infoToSend, topicArrayList, allUsers);
                        }
                    }
                }
            }
        }
    }

    /**
     * helper function to show User nodes which events / new topics then should receive
     *
     * @param id				name of user
     * @param infoToSend		List of objects to send over, in case of subscriber
     * @param topicArrayList	List of topics to send over, in case of publisher
     */
    public void unNotified(String id, ArrayList<Object> infoToSend, ArrayList<Object> topicArrayList, HashMap<String, User> allUsers){
        if(unNotified.containsKey(id)){
            if(allUsers.get(id).isSub()) {
                for (Object obj : infoToSend)
                    unNotified.get(id).add(obj);
                for (Object topic: topicArrayList)
                    unNotified.get(id).add(topic);
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