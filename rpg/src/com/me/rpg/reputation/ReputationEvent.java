package com.me.rpg.reputation;

import java.io.Serializable;
import java.util.Date;

import com.me.rpg.characters.GameCharacter;
import com.me.rpg.utils.Coordinate;

public class ReputationEvent implements Serializable
{

	private static final long serialVersionUID = 3012016206183543799L;

	private EventID id;
	private Coordinate coordinate;
	private Date date;
	private int repImpactMagnitude;
	private EventTemplate eventTemplate;
	private int referenceCount;
	
	public ReputationEvent(String eventType, String groupAffected, GameCharacter characterAffected, EventTemplate eventTemplate, Coordinate coordinate, Date date)
	{
		id = new EventID(eventType,groupAffected,characterAffected);
		this.eventTemplate=eventTemplate;
		this.coordinate=coordinate;
		this.date=date;
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
	public int incrementReferenceCount()
	{
		return 0;
	}
	public int decrementReferenceCount()
	{
		return 0;
	}
}
