package com.me.rpg.reputation;

import java.util.HashMap;

public class EventTemplate {
	
	private String eventName; 
	private HashMap<String, Integer> eventEffectsByGroup;
	
	EventTemplate(String eventName, int magnitude, HashMap<String, Integer> eventEffectsByGroup)
	{
		this.eventName=eventName;
		this.eventEffectsByGroup=eventEffectsByGroup;
	}

	public String getEventName() {
		return eventName;
	}

	public int getEffectOnGroup(String group) {
		return eventEffectsByGroup.get(group);
	}
	
}
