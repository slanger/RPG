package com.me.rpg.reputation;

public class EventTemplate {
	
	private String eventName; 
	private int magnitude;
	//private String repEffects;
	
	EventTemplate(String eventName, int magnitude)
	{
		this.setEventName(eventName);
		this.setMagnitude(magnitude);
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public int getMagnitude() {
		return magnitude;
	}

	public void setMagnitude(int magnitude) {
		this.magnitude = magnitude;
	}
	

}
