package com.me.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PlayableCharacter extends Character
{
	
	public PlayableCharacter(String name, Texture spritesheet, int width, int height, int tileWidth, int tileHeight, float animationDuration)
	{
		super(name, spritesheet, width, height, tileWidth, tileHeight, animationDuration);
	}
	
	public void update(float deltaTime, Map currentMap, Coordinate currentLocation)
	{
		float spriteWidth = getSpriteWidth();
		float spriteHeight = getSpriteHeight();
		float oldX = currentLocation.getX() - spriteWidth / 2;
		float oldY = currentLocation.getY() - spriteHeight / 2;
		float x = oldX;
		float y = oldY;
		float speed = getSpeed();
		int mapWidth = currentMap.getWidth();
		int mapHeight = currentMap.getHeight();
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
		
		// clamp x
		if (x < 0)
		{
			x = 0;
		}
		if (x > mapWidth - spriteWidth)
		{
			x = mapWidth - spriteWidth;
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
		
		// clamp y
		if (y < 0)
		{
			y = 0;
		}
		if (y > mapHeight - spriteHeight)
		{
			y = mapHeight - spriteHeight;
		}
		
		// collision detection with objects on map
		Coordinate newCoordinate = checkCollision(x, y, oldX, oldY, spriteWidth, spriteHeight, currentMap.getObjectsOnMap(), currentMap.getCharactersOnMap());
		x = newCoordinate.getX();
		y = newCoordinate.getY();
		
		TextureRegion currentFrame = null;
		if (moving)
		{
			currentLocation.setX(x + spriteWidth / 2);
			currentLocation.setY(y + spriteHeight / 2);
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
