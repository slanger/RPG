package com.me.rpg;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;

public class NonplayableCharacter extends Character
{
	
	public NonplayableCharacter(String name, Texture spritesheet, int width, int height, int tileWidth, int tileHeight, int startX, int startY, float animationDuration)
	{
		super(name, spritesheet, width, height, tileWidth, tileHeight, startX, startY, animationDuration);
		Timer.schedule(new moveTask(), 1, 1);
	}
	
	public void update(float deltaTime, Coordinate currentLocation, int mapWidth, int mapHeight, RectangleMapObject[] objects, HashMap<Character, Coordinate> characters)
	{
		float oldX = currentLocation.getX() - getSpriteWidth()/2;
		float oldY = currentLocation.getY() - getSpriteHeight()/2;
		float x = oldX;
		float y = oldY;
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
		
		// collision detection with objects on map
		Rectangle boundingBox = new Rectangle(x, y, getSpriteWidth(), getSpriteHeight());
		for (RectangleMapObject object : objects)
		{
			if (object.getRectangle().overlaps(boundingBox))
			{
				x = oldX;
				y = oldY;
			}
		}

		// collision detection with characters
		Iterator<Entry<Character, Coordinate>> iter = characters.entrySet().iterator();
		while (iter.hasNext())
		{
			Entry<Character, Coordinate> entry = iter.next();
			Character selected = entry.getKey();
			if (selected.equals(this))
			{
				continue;
			}
			Coordinate location = entry.getValue();
			Rectangle r = new Rectangle(location.getX() - selected.getSpriteWidth()/2, location.getY() - selected.getSpriteHeight()/2, selected.getSpriteWidth(), selected.getSpriteHeight());
			if (r.overlaps(boundingBox))
			{
				x = oldX;
				y = oldY;
			}
		}
		
		if (moving)
		{
			currentLocation.setX(x + getSpriteWidth()/2);
			currentLocation.setY(y + getSpriteHeight()/2);
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
