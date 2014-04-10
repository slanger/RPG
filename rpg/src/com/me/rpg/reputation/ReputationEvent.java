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
	private long timeEventOccurred;
	private int repImpactMagnitude;
	private int referenceCount;

	public ReputationEvent(String eventType, int magnitude, String groupAffected, GameCharacter characterAffected, Coordinate coordinate, long timeEventOccurred)
	{
		id = new EventID(eventType, groupAffected, characterAffected);
		this.coordinate = coordinate;
		this.timeEventOccurred = timeEventOccurred;
		repImpactMagnitude = -20; //temp placeholder
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

	public int incrementReferenceCount()
	{
		return 0;
	}

	public int decrementReferenceCount()
	{
		return 0;
	}

}
