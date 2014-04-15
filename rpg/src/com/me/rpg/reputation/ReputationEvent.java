package com.me.rpg.reputation;

import java.io.Serializable;

import com.me.rpg.characters.GameCharacter;
import com.me.rpg.utils.Coordinate;

public class ReputationEvent implements Serializable
{
	
	private static final long serialVersionUID = 3012016206183543799L;

	private String eventType; 		//didDamageTo, completedQuest, etc
	private int highestKnownMagnitude;    		//highest magnitude known
	private int eventID;			
	private String groupAffected;
	private GameCharacter characterAffected;
	private Coordinate coordinate;
	private long timeEventOccurred;
	private int referenceCount;

	public ReputationEvent(String eventType, int highestKnownMagnitude, String groupAffected, GameCharacter characterAffected, Coordinate coordinate, long timeEventOccurred)
	{
		this.eventType = eventType;
		this.highestKnownMagnitude = highestKnownMagnitude;
		this.groupAffected = groupAffected;
		this.characterAffected = characterAffected;
		this.coordinate = coordinate;
		this.timeEventOccurred = timeEventOccurred;
		referenceCount = 0;
	}

	public String getEventType()
	{
		return eventType;
	}
	
	public int getMagnitude()
	{
		return highestKnownMagnitude;
	}
	
	public int getEventID()
	{
		return eventID;
	}
	
	public String getGroupAffected()
	{
		return groupAffected;
	}
	
	public GameCharacter getCharacterAffected()
	{
		return characterAffected;
	}
	
	public Coordinate getCoordinate()
	{
		return coordinate;
	}
	
	public long getTimeEventOccurred()
	{
		return timeEventOccurred;
	}

	public int getReferenceCount()
	{
		return referenceCount;
	}
	
	public void incrementReferenceCount()
	{
		referenceCount++;
	}

	public void decrementReferenceCount()
	{
		referenceCount--;
	}
	
	public void setHighestKnownMagnitude(int highestKnownMagnitude)
	{
		this.highestKnownMagnitude = highestKnownMagnitude;
	}
}
