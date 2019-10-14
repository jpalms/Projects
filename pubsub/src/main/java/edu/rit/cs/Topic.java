package edu.rit.cs;

import java.util.List;

public class Topic {
	private int id;
	private List<String> keywords;
	private String name;

	public Topic(int id, List<String> keywords, String name){
		this.id = id;
		this.keywords = keywords;
		this.name = name;
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
