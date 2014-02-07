package com.me.rpg;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;
import com.me.rpg.ai.FollowPathAI;
import com.me.rpg.ai.RandomWalkAI;
import com.me.rpg.maps.Map;
import com.me.rpg.maps.MapType;

public class NonplayableCharacter extends Character
{

	private boolean isHappy = false;
	private Color oldColor = null;
	private MoveToOtherTownTask moveToOtherTownTask;

	public NonplayableCharacter(String name, Texture spritesheet, int width,
			int height, int tileWidth, int tileHeight, float animationDuration,
			Rectangle walkingBounds)
	{
		super(name, spritesheet, width, height, tileWidth, tileHeight,
				animationDuration);

		setWalkAI(new RandomWalkAI(this, 1, 1, walkingBounds));

		moveToOtherTownTask = new MoveToOtherTownTask(this);
	}

	@Override
	protected void doRenderBefore()
	{
		if (isHappy)
		{
			// add green tint
			oldColor = new Color(getSprite().getColor());
			getSprite().setColor(new Color(0, 1, 0, oldColor.a));
		} else {
			oldColor = getSprite().getColor();
		}
	}
	
	@Override
	protected void doRenderAfter() {
		getSprite().setColor(oldColor);
	}

	@Override
	public void doUpdate(float deltaTime, Map currentMap)
	{
		// update movement
		Coordinate newLocation = new Coordinate();
		Direction newDirection = walkAI.update(deltaTime, currentMap, newLocation);
		if (isMoving())
		{
			setLocation(newLocation);
		}
		if (newDirection != null)
		{
			setDirection(newDirection);
		}

		updateTexture();
	}

	@Override
	public void doneFollowingPath()
	{
		setWalkAI(new RandomWalkAI(this, 1, 1, currentMap.getEnclosingWalkingBounds(getBoundingRectangle())));
		currentMap.getTimer().scheduleTask(moveToOtherTownTask, 0, 5);
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

		private Character character;

		MoveToOtherTownTask(Character character)
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
					System.out.println(getName() + " going to different town");
					setWalkAI(new FollowPathAI(character, currentMap));
					this.cancel();
				}
			}
		}

	}

	@Override
	public void acceptGoodAction(Character characterDoingAction)
	{
		isHappy = true;
		currentMap.getTimer().scheduleTask(new ChangeColorTask(), 0, 0.5f);
	}

}
