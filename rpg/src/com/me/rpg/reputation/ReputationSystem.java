package com.me.rpg.reputation;

import java.util.ArrayList;
import java.util.Iterator;

import com.me.rpg.World;
import com.me.rpg.characters.GameCharacter;

public class ReputationSystem {
	private World world;
	private ArrayList<ReputationEvent> MasterEventList;
	private EventTemplate[] EventTemplateList = new EventTemplate[10];
	
	public ReputationSystem(World world)
	{
		this.world=world;
		initializeTemplates();
		MasterEventList = new ArrayList<ReputationEvent>();
	}

	public ArrayList<ReputationEvent> getMasterEventList() {
		return MasterEventList;
	}
	
	private void initializeTemplates()
	{
		EventTemplateList[0] = new EventTemplate("Attacked", 10);
		EventTemplateList[1] = new EventTemplate("Killed", 50);
		EventTemplateList[2] = new EventTemplate("Stole From", 5);
		EventTemplateList[3] = new EventTemplate("Completed Easy Quest For", 10);
		EventTemplateList[4] = new EventTemplate("Completed Medium Quest For", 30);
		EventTemplateList[5] = new EventTemplate("Completed Hard Quest For", 50);
	}
	
	public void addNewEvent(String eventType, String groupAffected, GameCharacter characterAffected) 
	{
		Iterator<ReputationEvent> iter = MasterEventList.iterator();
		while (iter.hasNext())
		{
			ReputationEvent tempRepEvent = iter.next();
			if(tempRepEvent.getEventID().getEventType().equals(eventType) && tempRepEvent.getEventID().getGroupAffected().equals(groupAffected)
					&& tempRepEvent.getEventID().getCharacterAffected().equals(characterAffected))
			{
				System.out.println("Event already exists");
				return;
			}
		}
		
		for(int i=0;i<6;i++)
		{
			if(EventTemplateList[i].getEventName().equals(eventType))
			{
				ReputationEvent reputationEvent = new ReputationEvent(eventType,groupAffected,characterAffected,EventTemplateList[i]);
				MasterEventList.add(reputationEvent);
				System.out.println("Event Added To MasterEventList");
				int masterListIndex = MasterEventList.indexOf(reputationEvent);
				if(masterListIndex>0)
				{
					AlertWitnessNPCS(masterListIndex,reputationEvent);
				}
				if(masterListIndex <0)
				{
					System.out.println("Event not found in MasterEventList");
				}
				break;
			}
			
		}
	}
	
	private void AlertWitnessNPCS(int masterListIndex,ReputationEvent reputationEvent)
	{
		//for every alerted NPC, increment the reference count for the RepEvent
		
		//check coordinate of repEvent, check what characters are around,
		//For each character within radius, character.getNPCMemory.addNewMemoryElement();
		
		
		Iterator<GameCharacter> iter = world.getMap().getCharactersOnMap().iterator();
		while (iter.hasNext())
		{
			GameCharacter selected = iter.next();
			//Coordinate selectedLocation = selected.getBottomLeftCorner();
			//for now just add to all characters
			if(selected.getNPCMemory() != null){ //player will have a null NPCMemory
				selected.getNPCMemory().addMemory(reputationEvent);
			}
		}
	}
}

