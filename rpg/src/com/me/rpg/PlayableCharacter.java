package com.me.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PlayableCharacter extends Character
{
	
	public PlayableCharacter(String name, Texture spritesheet, int width, int height, int tileWidth, int tileHeight, int startX, int startY, float animationDuration)
	{
		super(name, spritesheet, width, height, tileWidth, tileHeight, startX, startY, animationDuration);
	}
	
	public void update(float deltaTime, Coordinate currentLocation, int mapWidth, int mapHeight)
	{
		float x = currentLocation.getX() - getSpriteWidth()/2;
		float y = currentLocation.getY() - getSpriteHeight()/2;
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
