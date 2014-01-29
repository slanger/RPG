package com.me.rpg;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Timer;

public class NonplayableCharacter extends Character
{

	private boolean isHappy = false;

	public NonplayableCharacter(String name, Texture spritesheet, int width,
			int height, int tileWidth, int tileHeight, float animationDuration)
	{
		super(name, spritesheet, width, height, tileWidth, tileHeight,
				animationDuration);
		Timer.schedule(new moveTask(), 1, 1);
	}

	@Override
	public void render(SpriteBatch batch)
	{
		if (isHappy)
		{
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
		addToStateTime(deltaTime);

		TextureRegion currentFrame = null;
		switch (getDirection())
		{
		case RIGHT:
			x += speed * deltaTime;
			currentFrame = isMoving() ? getRightWalkAnimation().getKeyFrame(
					getStateTime(), true) : getRightIdle();
			break;
		case LEFT:
			x -= speed * deltaTime;
			currentFrame = isMoving() ? getLeftWalkAnimation().getKeyFrame(
					getStateTime(), true) : getLeftIdle();
			break;
		case UP:
			y += speed * deltaTime;
			currentFrame = isMoving() ? getUpWalkAnimation().getKeyFrame(
					getStateTime(), true) : getUpIdle();
			break;
		case DOWN:
			y -= speed * deltaTime;
			currentFrame = isMoving() ? getDownWalkAnimation().getKeyFrame(
					getStateTime(), true) : getDownIdle();
			break;
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
		Coordinate newCoordinate = checkCollision(x, y, oldX, oldY,
				spriteWidth, spriteHeight, currentMap.getObjectsOnMap(),
				currentMap.getCharactersOnMap());
		x = newCoordinate.getX();
		y = newCoordinate.getY();

		if (isMoving())
		{
			currentLocation.setX(x + spriteWidth / 2);
			currentLocation.setY(y + spriteHeight / 2);
		}
		if (currentFrame != null)
		{
			getSprite().setRegion(currentFrame);
		}
	}

	private class moveTask extends Timer.Task
	{

		public void run()
		{
			// generate a random int in the range [0, 4] and convert to a
			// direction
			// 4 means the NPC won't move this update
			int rand = (int) (Math.random() * 5);
			if (rand > 3)
			{
				setMoving(false);
			}
			else
			{
				setMoving(true);
				setDirection(Direction.getDirection(rand));
			}
		}

	}

	private class changeColorTask extends Timer.Task
	{

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

	public void acceptGoodAction(Character characterDoingAction)
	{
		isHappy = true;
		Timer.schedule(new changeColorTask(), 1, 0.5f);
	}

}
