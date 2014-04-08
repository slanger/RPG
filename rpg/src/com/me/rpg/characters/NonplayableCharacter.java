package com.me.rpg.characters;

import com.me.rpg.World;
import com.me.rpg.state.State;
import com.me.rpg.state.UpdateResult;
import com.me.rpg.state.action.Action;
import com.me.rpg.utils.Location;

public class NonplayableCharacter extends GameCharacter
{

	private static final long serialVersionUID = 5970053678208573292L;

	private State stateMachine;

	public NonplayableCharacter(String name, String group, String spritesheetPath,
			int width, int height, int tileWidth, int tileHeight,
			float animationDuration, World world)
	{
		super(name, group, spritesheetPath, width, height, tileWidth,
				tileHeight, animationDuration, world);

		stateMachine = null;
	}
	
	public State getStateMachine()
	{
		return stateMachine;
	}
	
	public void setStateMachine(State s) {
		stateMachine = s;
	}

	@Override
	public void doUpdate(float deltaTime)
	{
		UpdateResult result = stateMachine.update(deltaTime);
		for (int i = 0; i < result.actions.size(); ++i) {
			Action a  = result.actions.get(i);
			a.doAction(deltaTime);
		}

		updateTexture();
	}

	/**
	 * Move NPC to other map "quietly"--without interrupting the player.
	 */
	@Override
	public void moveToOtherMap(Location newLocation)
	{
		nextLocation = newLocation;
		currentLocation.getMap().removeCharacterFromMap(this);
	}

	/**
	 * Move NPC to other map "quietly"--without interrupting the player.
	 */
	@Override
	public void warpToOtherMap(Location newLocation)
	{
		// Nothing flashy for NPC--just use moveToOtherMap()
		moveToOtherMap(newLocation);
	}

}
