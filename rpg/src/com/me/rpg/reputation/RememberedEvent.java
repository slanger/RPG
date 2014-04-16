package com.me.rpg.reputation;

import java.io.Serializable;
import java.lang.Math;
import java.util.Random;

import com.me.rpg.characters.GameCharacter;

public class RememberedEvent implements Serializable
{

	private static final long serialVersionUID = 928305096707093169L;

	private ReputationEvent repEventPointer;
	private int knownMagnitude;
	private int timeLearnedOfEvent;
	private int priority;
	
	RememberedEvent(int knownMagnitude, ReputationEvent reputationEvent, int timeTicks)
	{
		this.knownMagnitude = knownMagnitude;
		repEventPointer = reputationEvent;
		timeLearnedOfEvent = timeTicks;
	}
	
	public ReputationEvent getRepEventPointer()
	{
		return repEventPointer;
	}
	
	public int getMagnitudeKnownByNPC()
	{
		return knownMagnitude;
	}
	
	public void setMagnitudeKnownByNPC(int newMagnitudeKnownByNPC)
	{
		this.knownMagnitude = newMagnitudeKnownByNPC;
	}
	
	public long getTimeLearnedOfEvent()
	{
		return timeLearnedOfEvent;
	}
	
	public int update(int timeTicks)
	{
		//System.out.println("the time ticks: "+timeTicks);
		int timeComponent = Math.max(0, (int)(100-((timeTicks-timeLearnedOfEvent)/120))); 
		//System.out.println("in computing priority, time component is: "+timeComponent);
		
		Random random = new Random();
		int randomComponent = random.nextInt(150);
		
		priority = knownMagnitude + timeComponent + randomComponent;
		
		//System.out.println("priority is: "+priority);
		
		return priority;
	}
}
