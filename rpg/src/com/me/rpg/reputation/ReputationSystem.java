package com.me.rpg.reputation;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.utils.Coordinate;
import com.me.rpg.utils.Direction;
import com.me.rpg.characters.GameCharacter;
import com.me.rpg.World;

public class ReputationSystem
{
	private World world;
	private ArrayList<ReputationEvent> MasterEventList;
	private EventTemplate[] EventTemplateList = new EventTemplate[10];

	public ReputationSystem(World world)
	{
		this.world = world;
		initializeTemplates();
		MasterEventList = new ArrayList<ReputationEvent>();
	}

	public ArrayList<ReputationEvent> getMasterEventList()
	{
		return MasterEventList;
	}

	private void initializeTemplates()
	{
		EventTemplateList[0] = new EventTemplate("Attacked", 10);
		EventTemplateList[1] = new EventTemplate("Killed", 50);
		EventTemplateList[2] = new EventTemplate("Stole From", 5);
		EventTemplateList[3] = new EventTemplate("Completed Easy Quest For", 10);
		EventTemplateList[4] = new EventTemplate("Completed Medium Quest For",
				30);
		EventTemplateList[5] = new EventTemplate("Completed Hard Quest For", 50);
	}

	public void addNewEvent(String eventType, String groupAffected,
			GameCharacter characterAffected, Coordinate coordinate)
	{
		Iterator<ReputationEvent> iter = MasterEventList.iterator();
		while (iter.hasNext())
		{
			ReputationEvent tempRepEvent = iter.next();
			if (tempRepEvent.getEventID().getEventType().equals(eventType)
					&& tempRepEvent.getEventID().getGroupAffected()
							.equals(groupAffected)
					&& tempRepEvent.getEventID().getCharacterAffected()
							.equals(characterAffected))
			{
				System.out.println("Event already exists");
				return;
			}
		}

		for (int i = 0; i < 6; i++)
		{
			if (EventTemplateList[i].getEventName().equals(eventType))
			{
				ReputationEvent reputationEvent = new ReputationEvent(
						eventType, groupAffected, characterAffected,
						EventTemplateList[i]);
				MasterEventList.add(reputationEvent);
				System.out.println("Event Added To MasterEventList");
				int masterListIndex = MasterEventList.indexOf(reputationEvent);
				if (masterListIndex >= 0)
				{
					CheckForWitnesses(reputationEvent, coordinate);
				}
				if (masterListIndex < 0)
				{
					System.out.println("Event not found in MasterEventList");
				}
				break;
			}

		}
	}

	public void CheckForWitnesses(ReputationEvent reputationEvent,
			Coordinate coordinate)
	{
		// for every alerted NPC, increment the reference count for the RepEvent

		// check coordinate of repEvent, check what characters are around,
		// For each character within radius,
		// character.getNPCMemory.addNewMemoryElement();
		
		ArrayList<GameCharacter> charactersOnMap = world.getMap().getCharactersOnMap();
		Iterator<GameCharacter> iterator1 = charactersOnMap.iterator();
		while (iterator1.hasNext())
		{
			GameCharacter tempCharacter = iterator1.next();
			if(tempCharacter.getName() != "Player")
			{
				tempCharacter.checkCoordinateInVision(world.getMap().getFocusedCharacter().getCenterX(), 
							world.getMap().getFocusedCharacter().getCenterY());
//				if (tempCharacter.getNPCMemory() != null)
//				{
//					if (visionCone.contains(coordinate.getX(), coordinate.getY()))
//					{
//						tempCharacter.getNPCMemory().addMemory(reputationEvent);
//						System.out.println("Event seen by: "
//								+ tempCharacter.getName());
//					}
//				}
			}
		}
			
	}

		
		
		
		
		
		// Circle radius implementation of event checking, maybe use for some
		// type of events (loud ones?)
		// Iterator<GameCharacter> iterator1 = charactersOnMap.iterator();
		// while (iterator1.hasNext())
		// {
		// GameCharacter tempCharacter = iterator1.next();
		// tempX = tempCharacter.getCenterX();
		// tempY = tempCharacter.getCenterY();
		// Circle circle = new Circle(coordinate.getX(), coordinate.getY(),
		// 100);
		// if (tempCharacter.getNPCMemory() != null)
		// {
		// if (circle.contains(tempX, tempY))
		// {
		// tempCharacter.getNPCMemory().addMemory(reputationEvent);
		// System.out.println("Event seen by: "
		// + tempCharacter.getName());
		// }
		// }
		// }
	}

