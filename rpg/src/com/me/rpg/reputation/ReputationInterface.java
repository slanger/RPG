package com.me.rpg.reputation;

import java.util.ArrayList;
import com.me.rpg.characters.GameCharacter;
import java.io.Serializable;


public interface ReputationInterface extends Serializable
{
	/**
	 * Returns a string (like, neutral, hate) depending on the relation between the character parameters
	 * Order does not matter.
	 *
	 */
	String getRelationsBetweenCharacters(GameCharacter character1, GameCharacter character2);

	/**
	 *  Adds an event of that type (attacked, killed, completed quest, stole, etc)
	 *  Just specify the eventType (look in the templates in ReputationSystem.java)
	 */
	void addNewEvent(String eventType, String eventSpecifier, GameCharacter characterAffected);

	/**
	 * 
	 * Returns an arraylist containing all known events 
	 * 
	 */
	ArrayList<ReputationEvent> getMasterEventList();
	
	/**
	 * checks through all the characters, tries to share knowledge if the flag is set, 
	 * updates times and adjusts priority of events to share/sets flag if priority high enough
	 */
	void update();
	
}
