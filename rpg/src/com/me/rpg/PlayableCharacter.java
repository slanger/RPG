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
	
	protected void update()
	{
		float deltaTime = Gdx.graphics.getDeltaTime();
		float x = sprite.getX();
		float y = sprite.getY();
		moving = false;
		stateTime += deltaTime;
		
		// update x
		if (Gdx.input.isKeyPressed(Keys.LEFT))
		{
			x -= 100 * deltaTime;
			direction = Direction.LEFT;
			moving = true;
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT))
		{
			x += 100 * deltaTime;
			direction = Direction.RIGHT;
			moving = true;
		}
		if (x < 0)
		{
			x = 0;
		}
		if (x > RPG.camera.viewportWidth - sprite.getWidth())
		{
			x = RPG.camera.viewportWidth - sprite.getWidth();
		}
		
		// update y
		if (Gdx.input.isKeyPressed(Keys.UP))
		{
			y += 100 * deltaTime;
			direction = Direction.UP;
			moving = true;
		}
		if (Gdx.input.isKeyPressed(Keys.DOWN))
		{
			y -= 100 * deltaTime;
			direction = Direction.DOWN;
			moving = true;
		}
		if (y < 0)
		{
			y = 0;
		}
		if (y > RPG.camera.viewportHeight - sprite.getHeight())
		{
			y = RPG.camera.viewportHeight - sprite.getHeight();
		}
		
		TextureRegion currentFrame = null;
		if (moving)
		{
			sprite.setPosition(x, y);
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
