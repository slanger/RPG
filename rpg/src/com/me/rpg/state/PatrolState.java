package com.me.rpg.state;

import java.util.ArrayList;

import com.me.rpg.ai.FollowPathAI;
import com.me.rpg.characters.GameCharacter;
import com.me.rpg.maps.Map;
import com.me.rpg.state.action.Action;
import com.me.rpg.state.action.WalkAction;
import com.me.rpg.utils.Coordinate;

public class PatrolState extends State
{

	private static final long serialVersionUID = -127716873166546219L;

	private ArrayList<Action> actions = new ArrayList<Action>();
	private WalkAction patrolLoc;
	private Coordinate[] patrolCoordinates;
	private Map[] patrolMaps;
	private int idx = 0;

	public PatrolState(HierarchicalState parent, GameCharacter character,
			Coordinate[] patrolCoordinates, Map[] patrolMaps)
	{
		super(parent, character);

		if (patrolCoordinates == null || patrolCoordinates.length == 0)
			throw new NullPointerException(
					"Can't pass in a null or empty patrol list.");
		if (patrolMaps == null || patrolCoordinates.length == 0)
			throw new NullPointerException(
					"Can't pass in a null or empty map list.");
		if (patrolCoordinates.length != patrolMaps.length)
			throw new RuntimeException(
					"Coordinate list must be the same size as map list.");
		for (int i = 0; i < patrolCoordinates.length; ++i)
		{
			if (patrolCoordinates[i] == null)
				throw new NullPointerException(
						"Can't have a null location for patrol list: " + i
								+ " " + patrolCoordinates);
		}
		for (int i = 0; i < patrolMaps.length; i++)
		{
			if (patrolMaps[i] == null)
				throw new NullPointerException(
						"Can't have a null map for patrol list: " + i + " "
								+ patrolMaps);
		}

		this.patrolCoordinates = new Coordinate[patrolCoordinates.length];
		System.arraycopy(patrolCoordinates, 0, this.patrolCoordinates, 0,
				patrolCoordinates.length);
		this.patrolMaps = new Map[patrolMaps.length];
		System.arraycopy(patrolMaps, 0, this.patrolMaps, 0, patrolMaps.length);

		patrolLoc = new WalkAction(character, patrolCoordinates[0],
				patrolMaps[0]);
		actions.add(patrolLoc);
	}

	@Override
	public ArrayList<Action> doGetEntryActions()
	{
		int closestPatrolIndex = -1;
		float minimumDistance = Float.MAX_VALUE;
		int closestInvalidPatrolIndex = -1;
		float minimumInvalidDistance = Float.MAX_VALUE;
		for (int i = 0; i < patrolCoordinates.length; i++)
		{
			FollowPathAI pathAI = new FollowPathAI(character,
					patrolCoordinates[i].getSmallCenteredRectangle(),
					patrolMaps[i]);
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
		patrolLoc = new WalkAction(character, patrolCoordinates[idx],
				patrolMaps[idx]);
		actions.add(patrolLoc);

		return super.doGetEntryActions();
	}

	@Override
	public ArrayList<Action> doGetActions()
	{
		if (character.getCurrentMap().equals(patrolMaps[idx])
				&& character.isCenterNear(patrolCoordinates[idx]))
		{
			idx++;
			idx %= patrolCoordinates.length;
			actions.remove(patrolLoc);
			patrolLoc = new WalkAction(character, patrolCoordinates[idx],
					patrolMaps[idx]);
			actions.add(patrolLoc);
		}
		return actions;
	}

}
