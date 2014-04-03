package com.me.rpg.state;

import java.util.ArrayList;

import com.me.rpg.characters.GameCharacter;
import com.me.rpg.state.action.Action;
import com.me.rpg.state.action.ModifySpeedAction;
import com.me.rpg.state.action.WalkAction;
import com.me.rpg.utils.Coordinate;

public class RunAwayState extends State
{

	private static final long serialVersionUID = 1133256009260084236L;

	private float lastSeen = 0f;
	private ModifySpeedAction speedAction;
	private ArrayList<Action> entryExitActions;

	public RunAwayState(HierarchicalState parent, GameCharacter character)
	{
		super(parent, character);
		speedAction = new ModifySpeedAction(character, 0.2f);
		entryExitActions = new ArrayList<Action>();
		entryExitActions.add(speedAction);
	}

	protected ArrayList<Action> doGetEntryActions()
	{
		return entryExitActions;
	}

	protected ArrayList<Action> doGetExitActions()
	{
		return entryExitActions;
	}

	protected ArrayList<Action> doGetActions()
	{
		ArrayList<GameCharacter> people = character.getCurrentMap()
				.canSeeOrHearCharacters(character);
		if (people.size() == 0 && lastSeen > 3f)
		{
			character.setMoving(false);
			return super.doGetActions();
		}
		else if (people.size() == 0)
		{
			return new ArrayList<Action>();
		}

		lastSeen = 0f;
		GameCharacter near = people.get(0);
		Coordinate pCenter = near.getCenter();
		Coordinate meCenter = character.getCenter();
		meCenter.setX(2 * meCenter.getX() - pCenter.getX());
		meCenter.setY(2 * meCenter.getY() - pCenter.getY());

		ArrayList<Action> actions = new ArrayList<Action>();
		actions.add(new WalkAction(character, meCenter));
		return actions;
	}

	@Override
	protected void doUpdate(float delta)
	{
		lastSeen += delta;
	}

}
