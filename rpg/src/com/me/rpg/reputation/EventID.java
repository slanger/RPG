package com.me.rpg.reputation;

import com.me.rpg.GameCharacter;

public class EventID {
	private String eventType;
	private String groupAffected;
	private GameCharacter characterAffected; 
	private int id;
	
	public EventID(String eventType, String groupAffected, GameCharacter characterAffected){
		this.eventType=eventType;
		this.groupAffected=groupAffected;
		this.characterAffected=characterAffected;
	}
	
	public String getEventType()
	{
		return eventType;
	}
	public String getGroupAffected()
	{
		return groupAffected;
	}
	public GameCharacter getCharacterAffected(){
		return characterAffected;
	}
	public int getID()
	{
		return id;
	}
}
