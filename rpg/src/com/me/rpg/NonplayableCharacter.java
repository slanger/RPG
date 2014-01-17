package com.me.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Timer;

public class NonplayableCharacter extends Character
{
	
	public NonplayableCharacter(String name, Texture spritesheet, int width, int height, int tileWidth, int tileHeight, int startX, int startY, float animationDuration)
	{
		super(name, spritesheet, width, height, tileWidth, tileHeight, startX, startY, animationDuration);
		Timer.schedule(new moveTask(), 1, 1);
	}
	
	protected void update()
	{
		float deltaTime = Gdx.graphics.getDeltaTime();
		float x = sprite.getX();
		float y = sprite.getY();
		stateTime += deltaTime;
		
		TextureRegion currentFrame = null;
		switch (direction)
		{
		case RIGHT:
			x += 100 * deltaTime;
			currentFrame = moving ? rightWalkAnimation.getKeyFrame(stateTime, true) : rightIdle;
			break;
		case LEFT:
			x -= 100 * deltaTime;
			currentFrame = moving ? leftWalkAnimation.getKeyFrame(stateTime, true) : leftIdle;
			break;
		case UP:
			y += 100 * deltaTime;
			currentFrame = moving ? upWalkAnimation.getKeyFrame(stateTime, true) : upIdle;
			break;
		case DOWN:
			y -= 100 * deltaTime;
			currentFrame = moving ? downWalkAnimation.getKeyFrame(stateTime, true) : downIdle;
			break;
		}
		
		// clamp x
		if (x < 0)
		{
			x = 0;
		}
		if (x > RPG.camera.viewportWidth - sprite.getWidth())
		{
			x = RPG.camera.viewportWidth - sprite.getWidth();
		}
		
		// clamp y
		if (y < 0)
		{
			y = 0;
		}
		if (y > RPG.camera.viewportHeight - sprite.getHeight())
		{
			y = RPG.camera.viewportHeight - sprite.getHeight();
		}
		
		if (moving)
		{
			sprite.setPosition(x, y);
		}
		if (currentFrame != null)
		{
			sprite.setRegion(currentFrame);
		}
	}
	
	private class moveTask extends Timer.Task
	{
		
		public void run()
		{
			// generate a random int in the range [0, 4] and convert to a direction
			// 4 means the NPC won't move this update
			int rand = (int)(Math.random() * 5);
			if (rand > 3)
			{
				moving = false;
			}
			else
			{
				moving = true;
				direction = Direction.getDirection(rand);
			}
		}
		
	}
	
}
