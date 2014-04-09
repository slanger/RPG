// Each NPC will have an instance of this class 

package com.me.rpg.reputation;

import java.io.Serializable;
import java.util.ArrayList;

import com.me.rpg.characters.GameCharacter;

public class NPCMemory implements Serializable
{

	private static final long serialVersionUID = -6449602139129854263L;

	private ArrayList<ReputationEvent> MasterEventList;
	private ArrayList<NPCMemoryElement> PerNPCLongTermMemory;
	private GameCharacter gameCharacter;
	
	public NPCMemory(GameCharacter gameCharacter, ArrayList<ReputationEvent> MasterEventList)
	{
		this.gameCharacter = gameCharacter;
		this.MasterEventList = MasterEventList;
		PerNPCLongTermMemory = new ArrayList<NPCMemoryElement>();
	}
	
	public void addMemory(ReputationEvent reputationEvent)
	{
		
		gameCharacter.updateDispositionValue(reputationEvent.getMagnitude());
		System.out.println("magnitude is: "+reputationEvent.getMagnitude());
		NPCMemoryElement temp = new NPCMemoryElement(reputationEvent);
		PerNPCLongTermMemory.add(temp);
		System.out.println("Added Event to a Single NPC");
	}
	
	public void update(ReputationEvent reputationEvent)
	{
		
	}
	
}
