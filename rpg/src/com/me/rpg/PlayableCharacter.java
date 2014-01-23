package com.me.rpg;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;

public class PlayableCharacter extends Character
{
	
	public PlayableCharacter(String name, Texture spritesheet, int width, int height, int tileWidth, int tileHeight, int startX, int startY, float animationDuration)
	{
		super(name, spritesheet, width, height, tileWidth, tileHeight, startX, startY, animationDuration);
	}
	
	public void update(float deltaTime, Coordinate currentLocation, int mapWidth, int mapHeight, RectangleMapObject[] objects, HashMap<Character, Coordinate> characters)
	{
		float oldX = currentLocation.getX() - getSpriteWidth()/2;
		float oldY = currentLocation.getY() - getSpriteHeight()/2;
		float x = oldX;
		float y = oldY;
		float speed = getSpeed();
		moving = false;
		stateTime += deltaTime;
		
		// update x
		if (Gdx.input.isKeyPressed(Keys.LEFT))
		{
			x -= speed * deltaTime;
			direction = Direction.LEFT;
			moving = true;
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT))
		{
			x += speed * deltaTime;
			direction = Direction.RIGHT;
			moving = true;
		}
		if (x < 0)
		{
			x = 0;
		}
		if (x > mapWidth - sprite.getWidth())
		{
			x = mapWidth - sprite.getWidth();
		}
		
		// update y
		if (Gdx.input.isKeyPressed(Keys.UP))
		{
			y += speed * deltaTime;
			direction = Direction.UP;
			moving = true;
		}
		if (Gdx.input.isKeyPressed(Keys.DOWN))
		{
			y -= speed * deltaTime;
			direction = Direction.DOWN;
			moving = true;
		}
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
		Rectangle boundingBoxWithNewY = new Rectangle(oldX, y, getSpriteWidth(), getSpriteHeight());
		Rectangle boundingBoxWithNewX = new Rectangle(x, oldY, getSpriteWidth(), getSpriteHeight());
		for (RectangleMapObject object : objects)
		{
			Rectangle r = object.getRectangle();
			if (r.overlaps(boundingBox))
			{
				if (r.overlaps(boundingBoxWithNewY))
				{
					y = oldY;
				}
				if (r.overlaps(boundingBoxWithNewX))
				{
					x = oldX;
				}
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
				if (r.overlaps(boundingBoxWithNewY))
				{
					y = oldY;
				}
				if (r.overlaps(boundingBoxWithNewX))
				{
					x = oldX;
				}
			}
		}
		
		TextureRegion currentFrame = null;
		if (moving)
		{
			currentLocation.setX(x + getSpriteWidth()/2);
			currentLocation.setY(y + getSpriteHeight()/2);
		}
		switch (direction)
		{
		case RIGHT:
			currentFrame = moving ? rightWalkAnimation.getKeyFrame(stateTime, true) : rightIdle;
			break;
		case LEFT:
			currentFrame = moving ? leftWalkAnimation.getKeyFrame(stateTime, true) : leftIdle;
			break;
		case UP:
			currentFrame = moving ? upWalkAnimation.getKeyFrame(stateTime, true) : upIdle;
			break;
		case DOWN:
			currentFrame = moving ? downWalkAnimation.getKeyFrame(stateTime, true) : downIdle;
			break;
		}
		if (currentFrame != null)
		{
			sprite.setRegion(currentFrame);
		}
	}
	
}
