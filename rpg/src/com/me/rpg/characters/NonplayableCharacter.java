package com.me.rpg.characters;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.ai.FollowPathAI;
import com.me.rpg.ai.RandomWalkAI;
import com.me.rpg.maps.Map;
import com.me.rpg.maps.MapType;
import com.me.rpg.state.State;
import com.me.rpg.state.UpdateResult;
import com.me.rpg.state.action.Action;
import com.me.rpg.utils.Timer;

public class NonplayableCharacter extends GameCharacter
{

	private boolean isHappy = false;
	private boolean enableAttack = false;
	private Color oldColor = null;
	private MoveToOtherTownTask moveToOtherTownTask;
	private State stateMachine;

	public NonplayableCharacter(String name, Texture spritesheet, int width,
			int height, int tileWidth, int tileHeight, float animationDuration,
			Rectangle walkingBounds)
	{
		super(name, spritesheet, width, height, tileWidth, tileHeight,
				animationDuration);

		setWalkAI(new RandomWalkAI(this, 1, 1, walkingBounds));

		moveToOtherTownTask = new MoveToOtherTownTask(this);
		stateMachine = null;
	}
	
	public void setStateMachine(State s) {
		stateMachine = s;
	}

	@Override
	public void addedToMap(Map map)
	{
		super.addedToMap(map);
		startMoveTask();
	}

	private void startMoveTask()
	{
		currentMap.getTimer().scheduleTask(moveToOtherTownTask, 0, 5);
	}

	@Override
	protected void doRenderBefore(SpriteBatch batch)
	{
		if (isHappy)
		{
			// add green tint
			oldColor = new Color(getSprite().getColor());
			getSprite().setColor(new Color(0, 1, 0, oldColor.a));
		}
		else
		{
			oldColor = getSprite().getColor();
		}
	}

	@Override
	protected void doRenderAfter(SpriteBatch batch)
	{
		getSprite().setColor(oldColor);
	}

	@Override
	public void doUpdate(float deltaTime, Map currentMap)
	{
		// update movement
		if (stateMachine != null) {
			UpdateResult result = stateMachine.update(deltaTime);
			for (int i = 0; i < result.actions.size(); ++i) {
				Action a  = result.actions.get(i);
				a.doAction(deltaTime);
			}
		}
		else
			walkAI.update(deltaTime, currentMap);

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
	public void doneFollowingPath()
	{
		walkAI.stop();
		walkAI = new RandomWalkAI(this, 1, 1,
				currentMap.getEnclosingWalkingBounds(getBoundingRectangle()));
		walkAI.start();
		startMoveTask();
	}

	private class ChangeColorTask extends Timer.Task
	{

		@Override
		public void run()
		{
			Color c = getSprite().getColor();
			float a = c.a;
			a -= 0.1f;
			if (a <= 0f)
			{
				isHappy = false;
				a = 1f;
				this.cancel();
			}
			getSprite().setColor(new Color(c.r, c.g, c.b, a));
		}

	}

	private class MoveToOtherTownTask extends Timer.Task
	{

		private GameCharacter character;

		MoveToOtherTownTask(GameCharacter character)
		{
			this.character = character;
		}

		@Override
		public void run()
		{
			int randomInt = (int) (Math.random() * 20);
			if (randomInt == 0)
			{
				if (currentMap.getMapType() == MapType.PROTOTYPE)
				{
					walkAI.stop();
					walkAI = new FollowPathAI(character,
							currentMap.getTestPath());
					walkAI.start();
					this.cancel();
				}
			}
		}

	}

	@Override
	public void acceptGoodAction(GameCharacter characterDoingAction)
	{
		isHappy = true;
		currentMap.getTimer().scheduleTask(new ChangeColorTask(), 0, 0.5f);
	}

}
