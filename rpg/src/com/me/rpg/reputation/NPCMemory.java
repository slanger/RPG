// Each NPC will have an instance of this class 

package com.me.rpg.reputation;

import java.io.Serializable;
import java.util.ArrayList;
import java.lang.Math;

import com.me.rpg.characters.GameCharacter;

public class NPCMemory implements Serializable
{

	private static final long serialVersionUID = -6449602139129854263L;

	private ArrayList<ReputationEvent> MasterEventList;
	private ArrayList<RememberedEvent> rememberedEvents;
	private GameCharacter gameCharacter;
	
	private RememberedEvent currentSharedEvent;
	
	private int timeStartedEventSharing = 0;
	
	private boolean recentlySharedKnowledge = false;
	
	public NPCMemory(GameCharacter gameCharacter, ArrayList<ReputationEvent> MasterEventList)
	{
		this.gameCharacter = gameCharacter;
		this.MasterEventList = MasterEventList;
		rememberedEvents = new ArrayList<RememberedEvent>();
	}
	
	public void update(int timeTicks)
	{
		if((gameCharacter.getWantsToShareKnowledge()==true) &&((timeTicks - timeStartedEventSharing) > 1800))//~ 30 seconds
		{
			System.out.println("interval expired");
			timeStartedEventSharing = 0;
			gameCharacter.setWantsToShareKnowledge(false);
			recentlySharedKnowledge = false;
		}
		
		if(gameCharacter.getWantsToShareKnowledge() == true)
		{
			return;
		}
		
		RememberedEvent currentHighestPriorityEvent = null;
		int currentHighestPriority = 0;
		
		for(RememberedEvent temp : rememberedEvents)
		{
			int newPriority = temp.update(timeTicks);
			if(newPriority > 200 && newPriority > currentHighestPriority)
			{
				currentHighestPriority = newPriority;
				currentHighestPriorityEvent = temp;
			}
		}
		
		if(currentHighestPriorityEvent != null)
		{
			timeStartedEventSharing = timeTicks;
			System.out.println("NPC "+gameCharacter.getName()+" wants to share an event!");
			currentSharedEvent = currentHighestPriorityEvent;
			gameCharacter.setWantsToShareKnowledge(true);
		}
		
	}
	
	public RememberedEvent getSharedEvent()
	{
		return currentSharedEvent;
	}
	
	public void addMemory(int seenMagnitude, ReputationEvent repEvent, int timeTicks)
	{
		for(RememberedEvent temp : rememberedEvents)
		{
			if(temp.getRepEventPointer().getEventType().equalsIgnoreCase(repEvent.getEventType()) && temp.getRepEventPointer().getCharacterAffected() == repEvent.getCharacterAffected())
			{
				if(Math.abs(seenMagnitude) > Math.abs(temp.getMagnitudeKnownByNPC()))
				{
					temp.setMagnitudeKnownByNPC(seenMagnitude);
					gameCharacter.updateDispositionValue(seenMagnitude);
				}
				return;
			}
		}
		RememberedEvent temp = new RememberedEvent(seenMagnitude, repEvent, timeTicks);
		rememberedEvents.add(temp);
		gameCharacter.updateDispositionValue(seenMagnitude);
	}
	
	public ArrayList<RememberedEvent> getRememberedEvents()
	{
		return rememberedEvents;
	}
	
	public boolean getRecentlySharedKnowledge()
	{
		return recentlySharedKnowledge;
	}
	
	public void setRecentlySharedKnowledge(boolean recentlySharedKnowledge)
	{
		this.recentlySharedKnowledge = recentlySharedKnowledge;
	}
}
