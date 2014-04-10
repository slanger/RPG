package com.me.rpg.reputation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.me.rpg.World;
import com.me.rpg.characters.GameCharacter;
import com.me.rpg.utils.Coordinate;

public class ReputationSystem implements Serializable, ReputationInterface
{

	private static final long serialVersionUID = 344768799531050144L;

	private World world;
	
	private ArrayList<ReputationEvent> MasterEventList;
	private ArrayList<EventTemplate> eventTemplateList = new ArrayList<EventTemplate>();
	private ArrayList<GroupRelation> groupRelations = new ArrayList<GroupRelation>();

	public ReputationSystem(World world) {
		this.world = world;
		initializeTemplates();
		initializeGroupRelations();
		MasterEventList = new ArrayList<ReputationEvent>();
	}

	public ArrayList<ReputationEvent> getMasterEventList() {
		return MasterEventList;
	}

	private void initializeTemplates() {
		eventTemplateList.add(new EventTemplate("Attacked", 10));
		eventTemplateList.add(new EventTemplate("Killed", 50));

//		EventTemplateList[0] = new EventTemplate("Attacked", 10);
//		EventTemplateList[1] = new EventTemplate("Killed", 50);
//		EventTemplateList[2] = new EventTemplate("Stole From", 5);
//		EventTemplateList[3] = new EventTemplate("Completed Easy Quest For", 10);
//		EventTemplateList[4] = new EventTemplate("Completed Medium Quest For", 30);
//		EventTemplateList[5] = new EventTemplate("Completed Hard Quest For", 50);
	}

	public void initializeGroupRelations()
	{
		groupRelations.add(new GroupRelation("villager_group", "villain_group", "hate"));
	}
	
	public String getRelationsBetweenCharacters(GameCharacter character1, GameCharacter character2)
	{
		//if player is involved
		if(character1.getGroup().equalsIgnoreCase("player_group"))
		{
			return character2.getViewOfPlayer();
		}
		if(character2.getGroup().equalsIgnoreCase("player_group"))
		{
			return character1.getViewOfPlayer();
		}
		
		//if player not involved
		for(GroupRelation temp : groupRelations)
		{
			if(temp.groupMatch(character1.getGroup(),character2.getGroup()))
			{
				return temp.getRelation();
			}
		}
		//if player is not involved
		throw new RuntimeException("Illegal group");
		// will return like, neutral, or hate
	}
	
	public void addNewEvent(String eventType, String groupAffected,
			GameCharacter characterAffected, Coordinate coordinate, Date date) {
		Iterator<ReputationEvent> iter = MasterEventList.iterator();
		while (iter.hasNext()) {
			ReputationEvent tempRepEvent = iter.next();
			if (tempRepEvent.getEventID().getEventType().equals(eventType)
					&& tempRepEvent.getEventID().getGroupAffected()
							.equals(groupAffected)
					&& tempRepEvent.getEventID().getCharacterAffected()
							.equals(characterAffected)) {
				System.out.println("Event already exists");
				return;
			}
		}

		for (int i = 0; i < 6; i++) {
			if (eventTemplateList.get(i).getEventName().equals(eventType)) {
				ReputationEvent reputationEvent = new ReputationEvent(eventType, eventTemplateList.get(i).getMagnitude(), groupAffected, characterAffected, coordinate, date);
				MasterEventList.add(reputationEvent);
				System.out.println("Event Added To MasterEventList");
				world.pushMessage("Event: "+eventType+"  Group: "+groupAffected+"   NPC: "+characterAffected.getName()+
						"  Location: "+(int)coordinate.getX()+", "+(int)coordinate.getY());
				int masterListIndex = MasterEventList.indexOf(reputationEvent);
				if (masterListIndex >= 0) {
					CheckForWitnesses(reputationEvent, coordinate);
				}
				if (masterListIndex < 0) {
					System.out.println("Event not found in MasterEventList");
				}
				break;
			}

		}
	}

	public void CheckForWitnesses(ReputationEvent reputationEvent,
			Coordinate coordinate) {
		//this method called from addNewEvent after event has been seen by an npc
		// for every alerted NPC, increment the reference count for the RepEvent

		// check coordinate of repEvent, check what characters are around,
		// For each character within radius,
		// character.getNPCMemory.addNewMemoryElement();

		ArrayList<GameCharacter> charactersOnMap = world.getCurrentMap()
				.getCharactersOnMap();
		Iterator<GameCharacter> iterator1 = charactersOnMap.iterator();
		while (iterator1.hasNext()) {
			GameCharacter tempCharacter = iterator1.next();
			if (tempCharacter.getName() != "Player") {
				if (tempCharacter.checkCoordinateInVision(coordinate.getX(),
						coordinate.getY()) || tempCharacter.checkCoordinateWithinHearing(coordinate.getX(), coordinate.getY())) 
				{
					if (tempCharacter.getNPCMemory() != null) {
						//tempCharacter.setFaceDirection(Direction.RIGHT);
						//add feature:  change face direction to look at event 
						//
						tempCharacter.getNPCMemory().addMemory(reputationEvent);
						System.out.println("Event seen by: "
								+ tempCharacter.getName());
					}
				}
			}
		}
	}
	
	public class EventTemplate implements Serializable
	{

		private static final long serialVersionUID = 2152772298032083532L;

		private String eventName; 
		private int magnitude;
		
		EventTemplate(String eventName, int magnitude)
		{
			this.eventName = eventName;
			this.magnitude = magnitude;
		}

		public String getEventName() {
			return eventName;
		}

		public int getMagnitude()
		{
			return magnitude;
		}
		
	}
	
	public class GroupRelation implements Serializable
	{
		private static final long serialVersionUID = 4866802809019077495L;
		
		private String group1;
		private String group2;
		private String relation;
		
		public GroupRelation(String group1, String group2, String relation)
		{
			this.group1 = group1;
			this.group2 = group2;
			this.relation = relation;
		}
		
		public boolean groupMatch(String group1, String group2)
		{
			if((this.group1.equalsIgnoreCase(group1) || this.group1.equalsIgnoreCase(group2)) && 
					(this.group2.equalsIgnoreCase(group1) || this.group2.equalsIgnoreCase(group2)))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		
		public String getGroup1()
		{
			return group1;
		}
		
		public String getGroup2()
		{
			return group2;
		}
		
		public String getRelation()
		{
			return relation;
		}
	}
}
