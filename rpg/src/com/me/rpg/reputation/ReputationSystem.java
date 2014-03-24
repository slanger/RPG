package com.me.rpg.reputation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.badlogic.gdx.math.Circle;
import com.me.rpg.World;
import com.me.rpg.characters.GameCharacter;
import com.me.rpg.utils.Coordinate;

public class ReputationSystem implements Serializable
{

	private static final long serialVersionUID = 344768799531050144L;

	private World world;
	private ArrayList<ReputationEvent> MasterEventList;
	private EventTemplate[] EventTemplateList = new EventTemplate[10];

	public ReputationSystem(World world) {
		this.world = world;
		initializeTemplates();
		MasterEventList = new ArrayList<ReputationEvent>();
	}

	public ArrayList<ReputationEvent> getMasterEventList() {
		return MasterEventList;
	}

	private void initializeTemplates() {
		HashMap<String, Integer> eventEffectsByGroup;
	
		eventEffectsByGroup = new HashMap<String, Integer>();
		eventEffectsByGroup.put("testGroup1", -10);
		eventEffectsByGroup.put("testGroup2", 10);
		EventTemplateList[0] = new EventTemplate("Attacked", 10, eventEffectsByGroup);
		
		
		eventEffectsByGroup = new HashMap<String, Integer>();
		eventEffectsByGroup.put("testGroup1", -10);
		eventEffectsByGroup.put("testGroup2", 10);
		EventTemplateList[1] = new EventTemplate("Killed", 50, eventEffectsByGroup);
		
		eventEffectsByGroup = new HashMap<String, Integer>();
		eventEffectsByGroup.put("testGroup1", -10);
		eventEffectsByGroup.put("testGroup2", 10);
		EventTemplateList[2] = new EventTemplate("Stole From", 5, eventEffectsByGroup);
		
		eventEffectsByGroup = new HashMap<String, Integer>();
		eventEffectsByGroup.put("testGroup1", -10);
		eventEffectsByGroup.put("testGroup2", 10);
		EventTemplateList[3] = new EventTemplate("Completed Easy Quest For", 10, eventEffectsByGroup);
		
		eventEffectsByGroup = new HashMap<String, Integer>();
		eventEffectsByGroup.put("testGroup1", -10);
		eventEffectsByGroup.put("testGroup2", 10);
		EventTemplateList[4] = new EventTemplate("Completed Medium Quest For", 30, eventEffectsByGroup);
		
		eventEffectsByGroup = new HashMap<String, Integer>();
		eventEffectsByGroup.put("testGroup1", -10);
		eventEffectsByGroup.put("testGroup2", 10);
		EventTemplateList[5] = new EventTemplate("Completed Hard Quest For", 50, eventEffectsByGroup);
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
			if (EventTemplateList[i].getEventName().equals(eventType)) {
				ReputationEvent reputationEvent = new ReputationEvent(
						eventType, groupAffected, characterAffected,
						EventTemplateList[i], coordinate, date);
				MasterEventList.add(reputationEvent);
				System.out.println("Event Added To MasterEventList");
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
				Circle circle = new Circle(tempCharacter.getCenterX(),
						tempCharacter.getCenterY(), 150.0f);
				if (tempCharacter.checkCoordinateInVision(coordinate.getX(),
						coordinate.getY()) || circle.contains(coordinate.getX(), coordinate.getY())) 
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
}
