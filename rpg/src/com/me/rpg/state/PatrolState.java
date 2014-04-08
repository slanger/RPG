package com.me.rpg.state;

import java.util.ArrayList;

import com.me.rpg.ai.FollowPathAI;
import com.me.rpg.characters.GameCharacter;
import com.me.rpg.state.action.Action;
import com.me.rpg.state.action.WalkAction;
import com.me.rpg.utils.Location;

public class PatrolState extends State
{

	private static final long serialVersionUID = -127716873166546219L;

	private ArrayList<Action> actions = new ArrayList<Action>();
	private WalkAction patrolLoc;
	private Location[] patrolLocations;
	private int idx = 0;

	public PatrolState(HierarchicalState parent, GameCharacter character,
			Location[] patrolLocations)
	{
		super(parent, character);

		if (patrolLocations == null || patrolLocations.length == 0)
			throw new NullPointerException(
					"Can't pass in a null or empty patrol list.");

		for (int i = 0; i < patrolLocations.length; ++i)
		{
			if (patrolLocations[i] == null)
				throw new NullPointerException(
						"Can't have a null location for patrol list: " + i
								+ " " + patrolLocations);
		}

		this.patrolLocations = new Location[patrolLocations.length];
		System.arraycopy(patrolLocations, 0, this.patrolLocations, 0,
				patrolLocations.length);
	}

	@Override
	public ArrayList<Action> doGetEntryActions()
	{
		int closestPatrolIndex = -1;
		float minimumDistance = Float.MAX_VALUE;
		int closestInvalidPatrolIndex = -1;
		float minimumInvalidDistance = Float.MAX_VALUE;
		for (int i = 0; i < patrolLocations.length; i++)
		{
			FollowPathAI pathAI = new FollowPathAI(character, patrolLocations[i]);
			float dist = pathAI.getTotalPathDistance();

			if (closestInvalidPatrolIndex == -1
					|| dist < minimumInvalidDistance)
			{
				closestInvalidPatrolIndex = i;
				minimumInvalidDistance = dist;
			}

			if (pathAI.isDestinationReachable())
			{
				if (closestPatrolIndex == -1 || dist < minimumDistance)
				{
					closestPatrolIndex = i;
					minimumDistance = dist;
				}
			}
		}

		// if we did not find any valid patrol point, chose closest invalid one
		if (closestPatrolIndex == -1)
		{
			closestPatrolIndex = closestInvalidPatrolIndex;
			minimumDistance = minimumInvalidDistance;
		}

		idx = closestPatrolIndex;
		actions.remove(patrolLoc);
		patrolLoc = new WalkAction(character, patrolLocations[idx]);
		actions.add(patrolLoc);

		return super.doGetEntryActions();
	}

	@Override
	public ArrayList<Action> doGetActions()
	{
		if (character.getCurrentMap().equals(patrolLocations[idx].getMap())
				&& character.isCenterNear(patrolLocations[idx].getCenter()))
		{
			idx++;
			idx %= patrolLocations.length;
			actions.remove(patrolLoc);
			patrolLoc = new WalkAction(character, patrolLocations[idx]);
			actions.add(patrolLoc);
		}
		return actions;
	}

}
