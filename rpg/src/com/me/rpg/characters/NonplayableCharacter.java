package com.me.rpg.characters;

import com.me.rpg.World;
import com.me.rpg.maps.Map;
import com.me.rpg.maps.MapType;
import com.me.rpg.state.State;
import com.me.rpg.state.UpdateResult;
import com.me.rpg.state.action.Action;
import com.me.rpg.utils.Coordinate;

public class NonplayableCharacter extends GameCharacter
{

	private static final long serialVersionUID = 5970053678208573292L;

	private boolean enableAttack = false;
	private State stateMachine;

	public NonplayableCharacter(String name, String spritesheetPath, int width,
			int height, int tileWidth, int tileHeight, float animationDuration,
			World world)
	{
		super(name, spritesheetPath, width, height, tileWidth, tileHeight,
				animationDuration, world);

		stateMachine = null;
	}
	
	public void setStateMachine(State s) {
		stateMachine = s;
	}

	@Override
	public void addedToMap(Map map)
	{
		super.addedToMap(map);
	}

	@Override
	public void doUpdate(float deltaTime, Map currentMap)
	{
		// update movement
		UpdateResult result = stateMachine.update(deltaTime);
		//System.out.printf("%s name: stateMachine's state: %s\n", getName(), stateMachine.getStates());
		for (int i = 0; i < result.actions.size(); ++i) {
			Action a  = result.actions.get(i);
			a.doAction(deltaTime);
		}

		// auto attack
		// attack
		if (weaponSlot != null && enableAttack)
		{
			weaponSlot.attack(currentMap, getFaceDirection(), getBoundingRectangle());
			enableAttack = false;
		}
		else
		{
			enableAttack = true;
		}

		updateTexture();
	}

	@Override
	public void moveToOtherMap(MapType mapType, Coordinate newLocation)
	{
		// TODO move NPC to other map 'quietly'--without interrupting the player
	}

	@Override
	public void warpToOtherMap(MapType mapType, Coordinate newLocation)
	{
		// just use moveToOtherMap(); nothing flashy for NPC
		moveToOtherMap(mapType, newLocation);
	}

}
