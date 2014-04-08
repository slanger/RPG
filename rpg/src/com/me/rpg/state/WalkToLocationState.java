package com.me.rpg.state;

import java.util.ArrayList;

import com.me.rpg.characters.GameCharacter;
import com.me.rpg.state.action.Action;
import com.me.rpg.state.action.WalkAction;
import com.me.rpg.utils.Location;

public class WalkToLocationState extends State
{

	private static final long serialVersionUID = 706834637955355621L;

	private WalkAction action;
	private ArrayList<Action> actions;

	public WalkToLocationState(HierarchicalState parent, GameCharacter character, Location targetLocation)
	{
		super(parent, character);

		action = new WalkAction(character, targetLocation);
		actions = new ArrayList<Action>();
		actions.add(action);
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
