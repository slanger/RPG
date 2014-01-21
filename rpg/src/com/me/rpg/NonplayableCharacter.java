package com.me.rpg;

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
	
	public void update(float deltaTime, Coordinate currentLocation, int mapWidth, int mapHeight)
	{
		float x = currentLocation.getX();
		float y = currentLocation.getY();
		float speed = getSpeed();
		stateTime += deltaTime;
		
		TextureRegion currentFrame = null;
		switch (direction)
		{
		case RIGHT:
			x += speed * deltaTime;
			currentFrame = moving ? rightWalkAnimation.getKeyFrame(stateTime, true) : rightIdle;
			break;
		case LEFT:
			x -= speed * deltaTime;
			currentFrame = moving ? leftWalkAnimation.getKeyFrame(stateTime, true) : leftIdle;
			break;
		case UP:
			y += speed * deltaTime;
			currentFrame = moving ? upWalkAnimation.getKeyFrame(stateTime, true) : upIdle;
			break;
		case DOWN:
			y -= speed * deltaTime;
			currentFrame = moving ? downWalkAnimation.getKeyFrame(stateTime, true) : downIdle;
			break;
		}
		
		// clamp x
		if (x < 0)
		{
			x = 0;
		}
		if (x > mapWidth - sprite.getWidth())
		{
			x = mapWidth - sprite.getWidth();
		}
		
		// clamp y
		if (y < 0)
		{
			y = 0;
		}
		if (y > mapHeight - sprite.getHeight())
		{
			y = mapHeight - sprite.getHeight();
		}
		
		if (moving)
		{
			currentLocation.setX(x);
			currentLocation.setY(y);
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
