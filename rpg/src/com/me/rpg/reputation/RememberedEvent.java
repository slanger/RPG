package com.me.rpg.reputation;

import java.io.Serializable;

import com.me.rpg.characters.GameCharacter;

public class RememberedEvent implements Serializable
{

	private static final long serialVersionUID = 928305096707093169L;

	private ReputationEvent repEventPointer;
	private int magnitudeKnownByNPC;
	private long timeLearnedOfEvent;
	
	RememberedEvent(ReputationEvent reputationEvent)
	{
		repEventPointer = reputationEvent;
		magnitudeKnownByNPC = reputationEvent.getMagnitude();
	}
	
	public ReputationEvent getReputationEventPointer()
	{
		return repEventPointer;
	}
	
	public int getMagnitudeKnownByNPC()
	{
		return magnitudeKnownByNPC;
	}
	
	public long getTimeLearnedOfEvent()
	{
		return timeLearnedOfEvent;
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
