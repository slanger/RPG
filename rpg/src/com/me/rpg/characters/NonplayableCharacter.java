package com.me.rpg.characters;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.World;
import com.me.rpg.ai.FollowPathAI;
import com.me.rpg.ai.RandomWalkAI;
import com.me.rpg.maps.Map;
import com.me.rpg.maps.MapType;
import com.me.rpg.utils.Task;

public class NonplayableCharacter extends GameCharacter
{

	private boolean isHappy = false;
	private boolean enableAttack = false;
	private Color oldColor = null;
	private MoveToOtherTownTask moveToOtherTownTask;

	public NonplayableCharacter(String name, Texture spritesheet, int width,
			int height, int tileWidth, int tileHeight, float animationDuration,
			World world, Rectangle walkingBounds)
	{
		super(name, spritesheet, width, height, tileWidth, tileHeight,
				animationDuration, world);

		setWalkAI(new RandomWalkAI(this, 1, 1, walkingBounds));

		moveToOtherTownTask = new MoveToOtherTownTask(this);
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
		walkAI.update(deltaTime, currentMap);

		// auto attack
		// attack
		if (weaponSlot != null && enableAttack)
		{
			weaponSlot.attack(currentMap, getFaceDirection(), getSprite()
					.getBoundingRectangle());
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

	private class ChangeColorTask extends Task
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

	private class MoveToOtherTownTask extends Task
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
					walkAI = new FollowPathAI(character, currentMap, currentMap.getTestPath());
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
