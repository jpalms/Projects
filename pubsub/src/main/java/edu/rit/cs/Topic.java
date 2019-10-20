package edu.rit.cs;

import java.util.List;
import java.util.HashMap;

public class Topic {
	private int id;
	private List<String> keywords;
	private String name;
	private HashMap<String, User> subs;

	public Topic(int id, List<String> keywords, String name){
		this.id = id;
		this.keywords = keywords;
		this.name = name;
		this.subs = subs;
	}

	public HashMap<String, User> getSubs() {
		return subs;
	}

	public void setSubs(HashMap<String, User> subs) {
		this.subs = subs;
	}

	public boolean hasSub(User user){
		return this.subs.containsKey(user.getId());
	}

	public void addSub(String ident, User subscriber){
		subs.put(ident, subscriber);
	}

	public void removeSub(String ident){
		subs.remove(ident);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	@Override
	public String toString(){
		return "Topic: " + this.getName() + "\n\tid: " + this.getId() + "\n\tkeywords: " + this.getKeywords().toString();
	}
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof Topic))
			return false;

		Topic topic = (Topic) obj;
		return this.getId() == topic.getId();
	}



}
