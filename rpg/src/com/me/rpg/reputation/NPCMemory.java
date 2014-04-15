// Each NPC will have an instance of this class 

package com.me.rpg.reputation;

import java.io.Serializable;
import java.util.ArrayList;

import com.me.rpg.characters.GameCharacter;

public class NPCMemory implements Serializable
{

	private static final long serialVersionUID = -6449602139129854263L;

	private ArrayList<ReputationEvent> MasterEventList;
	private ArrayList<RememberedEvent> rememberedEvents;
	private GameCharacter gameCharacter;
	
	public NPCMemory(GameCharacter gameCharacter, ArrayList<ReputationEvent> MasterEventList)
	{
		this.gameCharacter = gameCharacter;
		this.MasterEventList = MasterEventList;
		rememberedEvents = new ArrayList<RememberedEvent>();
	}
	
	public void updatePriority(long timeTicks)
	{
		
		
	}
	
	public ReputationEvent getHighestPriorityEvent()
	{
		
		return null;
	}
	
	public void addMemory(int seenMagnitude, ReputationEvent repEvent, long timeTicks)
	{
		for(RememberedEvent temp : rememberedEvents)
		{
			if(temp.getRepEventPointer().getEventType().equalsIgnoreCase(repEvent.getEventType()) && temp.getRepEventPointer().getCharacterAffected() == repEvent.getCharacterAffected())
			{
				if(seenMagnitude > temp.getRepEventPointer().getMagnitude())
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
	
	public void update(ReputationEvent reputationEvent)
	{
		
	}
	
	public ArrayList<RememberedEvent> getRememberedEvents()
	{
		return rememberedEvents;
	}
	
}
