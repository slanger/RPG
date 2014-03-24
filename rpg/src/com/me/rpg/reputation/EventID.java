package com.me.rpg.reputation;

import java.io.Serializable;

import com.me.rpg.characters.GameCharacter;

public class EventID implements Serializable
{

	private static final long serialVersionUID = 3687381762524396051L;

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
