//Each NPC will have an instance of this class 

package com.me.rpg.reputation;

import java.util.ArrayList;

public class NPCMemory {
	private ArrayList<ReputationEvent> MasterEventList;
	private ArrayList<NPCMemoryElement> PerNPCLongTermMemory;
	
	NPCMemory(ArrayList<ReputationEvent> MasterEventList)
	{
		this.MasterEventList = MasterEventList;
		PerNPCLongTermMemory = new ArrayList<NPCMemoryElement>();
	}
	
	public void addMemory(ReputationEvent reputationEvent)
	{
		NPCMemoryElement temp = new NPCMemoryElement(reputationEvent);
		PerNPCLongTermMemory.add(temp);
	}
	
	public void update(ReputationEvent reputationEvent)
	{
		
	}
	
}
