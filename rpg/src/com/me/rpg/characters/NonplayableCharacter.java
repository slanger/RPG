package com.me.rpg.characters;

import com.badlogic.gdx.graphics.Texture;
import com.me.rpg.maps.Map;
import com.me.rpg.state.State;
import com.me.rpg.state.UpdateResult;
import com.me.rpg.state.action.Action;

public class NonplayableCharacter extends GameCharacter
{

	private boolean enableAttack = false;
	private State stateMachine;

	public NonplayableCharacter(String name, Texture spritesheet, int width,
			int height, int tileWidth, int tileHeight, float animationDuration)
	{
		super(name, spritesheet, width, height, tileWidth, tileHeight,
				animationDuration);

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
	
}
