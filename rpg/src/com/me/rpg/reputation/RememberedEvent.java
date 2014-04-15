package com.me.rpg.reputation;

import java.io.Serializable;

import com.me.rpg.characters.GameCharacter;

public class RememberedEvent implements Serializable
{

	private static final long serialVersionUID = 928305096707093169L;

	private ReputationEvent repEventPointer;
	private int magnitudeKnownByNPC;
	private long timeLearnedOfEvent;
	private int priority;
	
	RememberedEvent(int magnitudeKnownByNPC, ReputationEvent reputationEvent, long timeTicks)
	{
		repEventPointer = reputationEvent;
		timeLearnedOfEvent = timeTicks;
		magnitudeKnownByNPC = reputationEvent.getMagnitude();
	}
	
	public ReputationEvent getRepEventPointer()
	{
		return repEventPointer;
	}
	
	public int getMagnitudeKnownByNPC()
	{
		return magnitudeKnownByNPC;
	}
	
	public void setMagnitudeKnownByNPC(int newMagnitudeKnownByNPC)
	{
		this.magnitudeKnownByNPC = newMagnitudeKnownByNPC;
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
	
	public int getPriority()
	{
		return priority;
	}
	
	public void setPriority(int priority)
	{
		this.priority = priority;
	}
	
}
