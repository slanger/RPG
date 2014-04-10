package com.me.rpg.reputation;

import java.util.ArrayList;
import java.util.Date;

import com.me.rpg.characters.GameCharacter;
import com.me.rpg.utils.Coordinate;


public interface ReputationInterface
{
	/**
	 * Returns a string (like, neutral, hate) depending on the relation between the character parameters
	 * Order does not matter.
	 *
	 */
	String getRelationsBetweenCharacters(GameCharacter character1, GameCharacter character2);

	void addNewEvent(String eventType, String groupAffected, GameCharacter characterAffected, Coordinate coordinate, long timeEventOccurred);

	ArrayList<ReputationEvent> getMasterEventList();
	
	
}
