package com.me.rpg.reputation;

import com.me.rpg.characters.GameCharacter;

public class EventID {
	private String eventType;
	private String groupAffected;
	private GameCharacter characterAffected; 
	
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
}