package com.me.rpg.ai;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.me.rpg.Character;
import com.me.rpg.Coordinate;
import com.me.rpg.Direction;
import com.me.rpg.maps.Map;

public class FollowPathAI implements WalkAI
{

	private Character character;
	private Map currentMap;
	private Rectangle[] path;
	private int currentIndex = 0;

	public FollowPathAI(Character character, Map currentMap)
	{
		this.character = character;
		this.currentMap = currentMap;

		Rectangle[] tempPath = currentMap.getTestPath();
		path = new Rectangle[tempPath.length + 1];
		path[0] = character.getBoundingRectangle();
		System.arraycopy(tempPath, 0, path, 1, tempPath.length);
	}

	@Override
	public void start()
	{
		character.setMoving(true);
	}

	@Override
	public void stop()
	{
		character.setMoving(false);
	}

	@Override
	public Direction update(float deltaTime, Map currentMap, Coordinate newLocation)
	{
		Rectangle currentWaypoint = path[currentIndex];
		Rectangle nextWaypoint = path[currentIndex + 1];
		Vector2 currentCenter = new Vector2();
		Vector2 nextCenter = new Vector2();
		currentWaypoint.getCenter(currentCenter);
		nextWaypoint.getCenter(nextCenter);
		float xE = (float) (nextCenter.x - currentCenter.x);
		float yE = (float) (nextCenter.y - currentCenter.y);
		float hE = (float) Math.sqrt(xE * xE + yE * yE);

		float speed = character.getSpeed();
		float x = character.getX() + (xE / hE) * speed * deltaTime;
		float y = character.getY() + (yE / hE) * speed * deltaTime;
		newLocation.setX(x);
		newLocation.setY(y);

		Direction newDirection;
		if (Math.abs(yE) >= Math.abs(xE))
		{
			if (yE > 0)
			{
				newDirection = Direction.UP;
			}
			else
			{
				newDirection = Direction.DOWN;
			}
		}
		else
		{
			if (xE >= 0)
			{
				newDirection = Direction.RIGHT;
			}
			else
			{
				newDirection = Direction.LEFT;
			}
		}

		if (nextWaypoint.contains(x, y))
		{
			currentIndex++;
		}

		if (currentIndex >= path.length - 1)
		{
			System.out.println(character.getName() + " has arrived!");
			character.doneFollowingPath();
		}

		return newDirection;
	}

}
