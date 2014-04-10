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

	

	private void initializeTemplates() {
		eventTemplateList.add(new EventTemplate("attacked", "small_damage", -10));
		eventTemplateList.add(new EventTemplate("attacked", "medium_damage", -30));
		eventTemplateList.add(new EventTemplate("attacked", "large_damage", -50));
		eventTemplateList.add(new EventTemplate("attacked", "killed", -100));
		
		eventTemplateList.add(new EventTemplate("talked_to_npc", "small_bonus", 1));
		eventTemplateList.add(new EventTemplate("talked_to_npc", "medium_bonus", 3));
		eventTemplateList.add(new EventTemplate("talked_to_npc", "large_bonus", 5));

		eventTemplateList.add(new EventTemplate("completed_quest", "small_bonus", 10));
		eventTemplateList.add(new EventTemplate("completed_quest", "medium_bonus", 30));
		eventTemplateList.add(new EventTemplate("completed_quest", "large_bonus", 50));
	}

	private void initializeGroupRelations()
	{
		groupRelations.add(new GroupRelation("villager_group", "villain_group", "hate"));
	}
	
	public void update()
	{
		ArrayList<GameCharacter> charactersInWorld = world.getCharactersInWorld();
		
		for(GameCharacter temp : charactersInWorld)
		{
			if(temp.getGroup()!= "player_group")
			{
				
				
				//if sharing
				shareKnowledge(temp);
			}
		}
	}
	
	private void shareKnowledge(GameCharacter sharingCharacter)
	{
		ArrayList<GameCharacter> receivingCharacters = sharingCharacter.getCurrentMap().canHearCharacters(sharingCharacter);
		
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
		
		//In case groups arent found,   should never happen though
		System.out.println("ReputationSystem -> getRelationsBetweenCharacters():    Group not found");
		return "neutral";
	}
	
	public void addNewEvent(String eventType, GameCharacter characterAffected) {
		
		String groupAffected = characterAffected.getGroup();
		long timeEventOccurred = System.currentTimeMillis();
		Coordinate coordinate = Coordinate.copy(characterAffected.getCenter());
		
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
				ReputationEvent reputationEvent = new ReputationEvent(eventType, eventTemplateList.get(i).getMagnitude(), groupAffected, characterAffected, coordinate, timeEventOccurred);
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

	private void CheckForWitnesses(ReputationEvent reputationEvent, Coordinate coordinate) 
	{
		ArrayList<GameCharacter> charactersOnMap = world.getCurrentMap().getCharactersOnMap();
		
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
	
	public ArrayList<ReputationEvent> getMasterEventList()
	{
		return MasterEventList;
	}
	
	private class EventTemplate implements Serializable
	{

		private static final long serialVersionUID = 2152772298032083532L;

		private String eventName; 
		private String eventSpecifier;
		private int magnitude;
		
		EventTemplate(String eventName, String eventSpecifier, int magnitude)
		{
			this.eventName = eventName;
			this.eventSpecifier = eventSpecifier;
			this.magnitude = magnitude;
		}

		public String getEventName() {
			return eventName;
		}

		public String getEventSpecifier()
		{
			return eventSpecifier;
		}
		
		public int getMagnitude()
		{
			return magnitude;
		}
		
	}
	
	private class GroupRelation implements Serializable
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
