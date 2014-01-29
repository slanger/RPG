package com.me.rpg.ai;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;
import com.me.rpg.Character;
import com.me.rpg.Coordinate;
import com.me.rpg.Direction;
import com.me.rpg.Map;

public class RandomWalkAI implements WalkAI
{

	private Character character;
	private float delaySeconds;
	private float intervalSeconds;
	private Rectangle walkingBounds;
	private MoveTask moveTask;

	private class MoveTask extends Timer.Task
	{

		@Override
		public void run()
		{
			// generate a random int in the range [0, 4] and convert to a
			// direction
			// 4 means the NPC won't move this update
			int rand = (int) (Math.random() * 5);
			if (rand > 3)
			{
				character.setMoving(false);
			}
			else
			{
				character.setMoving(true);
				character.setDirection(Direction.getDirection(rand));
			}
		}

	}

	public RandomWalkAI(Character character, float delaySeconds,
			float intervalSeconds, Rectangle walkingBounds)
	{
		this.character = character;
		this.delaySeconds = delaySeconds;
		this.intervalSeconds = intervalSeconds;
		this.walkingBounds = walkingBounds;
		this.moveTask = new MoveTask();
	}

	@Override
	public void start()
	{
		Timer.schedule(moveTask, delaySeconds, intervalSeconds);
	}

	@Override
	public void stop()
	{
		moveTask.cancel();
	}

	@Override
	public Coordinate update(float deltaTime, Map currentMap)
	{
		if (!character.isMoving())
		{
			return null;
		}

		float spriteWidth = character.getSpriteWidth();
		float spriteHeight = character.getSpriteHeight();
		Coordinate currentLocation = character.getLocation();
		float oldX = currentLocation.getX() - spriteWidth / 2;
		float oldY = currentLocation.getY() - spriteHeight / 2;
		float x = oldX;
		float y = oldY;
		float speed = character.getSpeed();

		Direction direction = character.getDirection();
		x += speed * deltaTime * direction.getDx();
		y += speed * deltaTime * direction.getDy();

		// clamp x
		float minX = walkingBounds.getX();
		float maxX = minX + walkingBounds.getWidth() - spriteWidth;
		if (x < minX)
		{
			x = minX;
		}
		if (x > maxX)
		{
			x = maxX;
		}

		// clamp y
		float minY = walkingBounds.getY();
		float maxY = minY + walkingBounds.getHeight() - spriteHeight;
		if (y < minY)
		{
			y = minY;
		}
		if (y > maxY)
		{
			y = maxY;
		}

		// collision detection with objects on map
		Coordinate newCoordinate = currentMap.checkCollision(x, y, oldX, oldY,
				spriteWidth, spriteHeight, character);
		x = newCoordinate.getX();
		y = newCoordinate.getY();

		return new Coordinate(x + spriteWidth / 2, y + spriteHeight / 2);
	}

}
