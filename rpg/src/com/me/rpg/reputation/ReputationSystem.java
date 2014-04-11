package com.me.rpg.reputation;

import java.io.Serializable;
import java.util.ArrayList;
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
	
	private long timeTicks = 0;

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
		timeTicks ++;
		ArrayList<GameCharacter> charactersInWorld = world.getCharactersInWorld();
		
		for(GameCharacter temp : charactersInWorld)
		{
			if(temp.getGroup() != "player_group")
			{
				temp.getNPCMemory().updatePriority(timeTicks);
				if(temp.getWantsToShareKnowledge())
				{
					ArrayList<GameCharacter> receivingCharacters = temp.getCurrentMap().canHearCharacters(temp);
					if(receivingCharacters != null)
					{
						shareKnowledge(temp, receivingCharacters);
					}
				}
			}
		}
	}
	
	private void shareKnowledge(GameCharacter sharingCharacter, ArrayList<GameCharacter> receivingCharacters)
	{
		ReputationEvent repEvent = sharingCharacter.getNPCMemory().getHighestPriorityEvent();
		for(GameCharacter temp : receivingCharacters)
		{
			// the timeTicks for when the npc learns of the event, not when the event actually occurred
			temp.getNPCMemory().addMemory(repEvent, timeTicks); 
		}
	}
	
	public void addNewEvent(String eventType, String eventSpecifier, GameCharacter characterAffected) {
		
		String groupAffected = characterAffected.getGroup();
		Coordinate coordinate = Coordinate.copy(characterAffected.getCenter());
		ArrayList<GameCharacter> witnesses = characterAffected.getCurrentMap().canSeeOrHearCharacters(characterAffected);
		witnesses.add(characterAffected);
		System.out.println(witnesses.size());
		
		for (int i = 0; i < eventTemplateList.size(); i++)
		{
			if (eventTemplateList.get(i).getEventName().equalsIgnoreCase(eventType) && 
					eventTemplateList.get(i).getEventSpecifier().equalsIgnoreCase(eventSpecifier)) 
			{
				ReputationEvent repEvent = new ReputationEvent(eventType, eventTemplateList.get(i).getMagnitude(), groupAffected, characterAffected, coordinate, timeTicks);
				addEventToMasterEventList(repEvent);
				
				world.pushMessage("Event: "+eventType+"	Magnitude: "+eventTemplateList.get(i).getMagnitude()+"  Group: "+groupAffected+"   NPC: "+characterAffected.getName()+
						"  Location: "+(int)coordinate.getX()+", "+(int)coordinate.getY());
				
				for(GameCharacter tempChar : witnesses)
				{
					if(!tempChar.getGroup().equalsIgnoreCase("player_group"))
					{
						tempChar.getNPCMemory().addMemory(repEvent, timeTicks);
						world.pushMessage("Event seen by: "+tempChar.getName());
					}
				}
			}
		}
//		for(ReputationEvent temp : MasterEventList)
//		{
//			if(temp.getEventType().equalsIgnoreCase(eventType) && temp.getCharacterAffected() == characterAffected)
//			{
//				world.pushErrorMessage("Event already in master event list!");
//				//separate events by times.  E.G. if same event type happens to same character,
//				//but event occurs 30 seconds after first, then create a new event
//				if((timeTicks - temp.getTimeEventOccurred()) < 1800)
//				{
//					System.out.println("Similar event already exists...checking if happened long ago");
//					updateExistingEvent();
//					return;
//				}
//			}
//		}
		
		
	}
	
	
	private void addEventToMasterEventList(ReputationEvent repEvent)
	{
		
	}
	private void updateExistingEvent()
	{
		
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
