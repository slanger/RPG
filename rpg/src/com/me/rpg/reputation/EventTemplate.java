package com.me.rpg.reputation;

import java.io.Serializable;
import java.util.HashMap;

public class EventTemplate implements Serializable
{

	private static final long serialVersionUID = 2152772298032083532L;

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
