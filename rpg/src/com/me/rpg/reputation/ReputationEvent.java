package com.me.rpg.reputation;

import com.me.rpg.Coordinate;
import com.me.rpg.GameCharacter;

public class ReputationEvent {
	private EventID id;
	private Coordinate coordinate;
	//private Time time;
	private int repImpactMagnitude;
	private EventTemplate eventTemplate;
	private int referenceCount;
	
	public ReputationEvent(String eventType, String groupAffected, GameCharacter characterAffected, EventTemplate eventTemplate	)
	{
		//this.eventType=eventType;
		//this.
	}
	
	public EventID getEventID()
	{
		return id;
	}
	public Coordinate getCoordinate()
	{
		return coordinate;
	}
	public int getMagnitude()
	{
		return repImpactMagnitude;
	}
	public EventTemplate getEventTemplate()
	{
		return eventTemplate;
	}
	public int IncrementReferenceCount()
	{
		return 0;
	}
	public int DecrementReferenceCount()
	{
		return 0;
	}
}
