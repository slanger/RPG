package com.me.rpg.state;

import java.util.ArrayList;
import java.util.Random;

import com.me.rpg.World;
import com.me.rpg.characters.GameCharacter;
import com.me.rpg.maps.Map;
import com.me.rpg.state.action.Action;
import com.me.rpg.state.action.WalkAction;
import com.me.rpg.utils.Coordinate;
import com.me.rpg.utils.Location;

public class WalkToRandomCharacterState extends State
{
	private static final long serialVersionUID = -4974043183716662447L;
	private WalkAction action;
	private ArrayList<Action> actions;

	public WalkToRandomCharacterState(HierarchicalState parent, GameCharacter character, Map currentMap)
	{
		super(parent, character);
		
		Location targetLocation = null;
		
		ArrayList<GameCharacter> tempCharacters = new ArrayList<GameCharacter>(currentMap.getCharactersOnMap());
		Random rand = new Random();
		int random = rand.nextInt(tempCharacters.size());
		GameCharacter tempCharacter = tempCharacters.get(random);
		
		targetLocation = new Location(currentMap,tempCharacter.getCenter());
		
		if(targetLocation != null)
		{
			action = new WalkAction(character, targetLocation);
			actions = new ArrayList<Action>();
			actions.add(action);
		}
	}

	public ArrayList<Action> doGetEntryActions()
	{
		action.start();
		return super.doGetEntryActions();
	}

	public ArrayList<Action> doGetActions()
	{
		return actions;
	}

	public ArrayList<Action> doGetExitActions()
	{
		action.stop();
		return super.doGetExitActions();
	}

}
