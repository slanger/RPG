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
	
	public void update(float deltaTime, Map currentMap)
	{
		float spriteWidth = getSpriteWidth();
		float spriteHeight = getSpriteHeight();
		Coordinate currentLocation = getLocation();
		float oldX = currentLocation.getX() - spriteWidth / 2;
		float oldY = currentLocation.getY() - spriteHeight / 2;
		float x = oldX;
		float y = oldY;
		float speed = getSpeed();
		int mapWidth = currentMap.getWidth();
		int mapHeight = currentMap.getHeight();
		setMoving(false);
		addToStateTime(deltaTime);
		
		// update x
		if (Gdx.input.isKeyPressed(Keys.LEFT))
		{
			x -= speed * deltaTime;
			setDirection(Direction.LEFT);
			setMoving(true);
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT))
		{
			x += speed * deltaTime;
			setDirection(Direction.RIGHT);
			setMoving(true);
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
			setDirection(Direction.UP);
			setMoving(true);
		}
		if (Gdx.input.isKeyPressed(Keys.DOWN))
		{
			y -= speed * deltaTime;
			setDirection(Direction.DOWN);
			setMoving(true);
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
		if (isMoving())
		{
			currentLocation.setX(x + spriteWidth / 2);
			currentLocation.setY(y + spriteHeight / 2);
		}
		switch (getDirection())
		{
		case RIGHT:
			currentFrame = isMoving() ? getRightWalkAnimation().getKeyFrame(getStateTime(), true) : getRightIdle();
			break;
		case LEFT:
			currentFrame = isMoving() ? getLeftWalkAnimation().getKeyFrame(getStateTime(), true) : getLeftIdle();
			break;
		case UP:
			currentFrame = isMoving() ? getUpWalkAnimation().getKeyFrame(getStateTime(), true) : getUpIdle();
			break;
		case DOWN:
			currentFrame = isMoving() ? getDownWalkAnimation().getKeyFrame(getStateTime(), true) : getDownIdle();
			break;
		}
		if (currentFrame != null)
		{
			getSprite().setRegion(currentFrame);
		}
	}
	
}
