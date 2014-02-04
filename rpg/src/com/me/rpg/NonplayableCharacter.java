package com.me.rpg;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;
import com.me.rpg.ai.RandomWalkAI;
//import com.me.rpg.ai.StandStillAI;
import com.me.rpg.ai.WalkAI;
import com.me.rpg.maps.Map;

public class NonplayableCharacter extends Character
{

	private WalkAI walkAI;
	private boolean isHappy = false;

	public NonplayableCharacter(String name, Texture spritesheet, int width,
			int height, int tileWidth, int tileHeight, float animationDuration,
			Rectangle walkingBounds)
	{
		super(name, spritesheet, width, height, tileWidth, tileHeight,
				animationDuration);

		walkAI = new RandomWalkAI(this, 1, 1, walkingBounds);
		//walkAI = new StandStillAI(this, 0, 0, walkingBounds);
		walkAI.start();
	}

	@Override
	public void render(SpriteBatch batch)
	{
		if (isHappy)
		{
			// add green tint
			Color oldColor = new Color(getSprite().getColor());
			getSprite().setColor(new Color(0, 1, 0, oldColor.a));
			getSprite().draw(batch);
			getSprite().setColor(oldColor);
		}
		else
		{
			getSprite().draw(batch);
		}
	}

	@Override
	public void update(float deltaTime, Map currentMap)
	{
		addToStateTime(deltaTime);

		// update texture
		TextureRegion currentFrame = null;
		switch (getDirection())
		{
		case RIGHT:
			currentFrame = isMoving() ? getRightWalkAnimation().getKeyFrame(
					getStateTime(), true) : getRightIdle();
			break;
		case LEFT:
			currentFrame = isMoving() ? getLeftWalkAnimation().getKeyFrame(
					getStateTime(), true) : getLeftIdle();
			break;
		case UP:
			currentFrame = isMoving() ? getUpWalkAnimation().getKeyFrame(
					getStateTime(), true) : getUpIdle();
			break;
		case DOWN:
			currentFrame = isMoving() ? getDownWalkAnimation().getKeyFrame(
					getStateTime(), true) : getDownIdle();
			break;
		}

		if (currentFrame != null)
		{
			getSprite().setRegion(currentFrame);
		}
		
		// update movement
		Coordinate newLocation = walkAI.update(deltaTime, currentMap);
		if (newLocation != null)
		{
			setLocation(newLocation);
		}
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

	@Override
	public void acceptGoodAction(Character characterDoingAction)
	{
		isHappy = true;
		Timer.schedule(new ChangeColorTask(), 1, 0.5f);
	}

}
