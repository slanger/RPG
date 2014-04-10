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
	private int referenceCount;

	public ReputationEvent(String eventType, int magnitude, String groupAffected, GameCharacter characterAffected, Coordinate coordinate, Date date)
	{
		id = new EventID(eventType, groupAffected, characterAffected);
		this.coordinate = coordinate;
		this.date = date;
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
