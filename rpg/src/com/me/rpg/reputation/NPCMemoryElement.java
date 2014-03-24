package com.me.rpg.reputation;

import java.io.Serializable;

import com.me.rpg.characters.GameCharacter;

public class NPCMemoryElement implements Serializable
{

	private static final long serialVersionUID = 928305096707093169L;

	private EventID id;
	private int magnitude;
	//private time timestamp;
	
	NPCMemoryElement(ReputationEvent reputationEvent)
	{
		id=reputationEvent.getEventID();
		magnitude=reputationEvent.getMagnitude();
		//time=reputationEvent.getTime();
	}
	
	public EventID getEventID()
	{
		return id;
	}
	public int getMagnitude()
	{
		return magnitude;
	}
	public boolean match(String eventType, int magnitude, String groupAffected, GameCharacter characterAffected)
	{
		return false;
	}
	public void update(ReputationEvent reputationEvent)
	{
		//
	}
	
}
