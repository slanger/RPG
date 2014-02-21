package com.me.rpg.ai;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.me.rpg.characters.GameCharacter;
import com.me.rpg.maps.Map;
import com.me.rpg.utils.Coordinate;
import com.me.rpg.utils.Direction;

public class FollowPathAI implements WalkAI
{

	private GameCharacter character;
	private Rectangle[] path;
	private int currentIndex = 0;

	public FollowPathAI(GameCharacter character, Rectangle[] path)
	{
		this.character = character;
		this.path = path;
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
	public void update(float deltaTime, Map currentMap)
	{
		if (currentIndex == path.length)
			return;
		Rectangle currentWaypoint = character.getBoundingRectangle();
		Rectangle nextWaypoint = path[currentIndex];
		Vector2 currentCenter = new Vector2();
		Vector2 nextCenter = new Vector2();
		currentWaypoint.getCenter(currentCenter);
		nextWaypoint.getCenter(nextCenter);
		float xE = (float) (nextCenter.x - currentCenter.x);
		float yE = (float) (nextCenter.y - currentCenter.y);
		float hE = (float) Math.sqrt(xE * xE + yE * yE);

		float speed = character.getSpeed();
		float oldX = character.getBottomLeftX();
		float oldY = character.getBottomLeftY();
		float x = oldX + (xE / hE) * speed * deltaTime;
		float y = oldY + (yE / hE) * speed * deltaTime;

		Coordinate newCoordinate = new Coordinate();
		boolean didMove = currentMap.checkCollisionWithObjects(x, y, oldX,
				oldY, character.getSpriteWidth(), character.getSpriteHeight(),
				newCoordinate);
		character.setMoving(didMove);
		x = newCoordinate.getX();
		y = newCoordinate.getY();

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

		if (nextWaypoint.contains(character.getCenterX(),
				character.getCenterY()))
		{
			currentIndex++;
		}

		if (currentIndex >= path.length)
		{
			character.doneFollowingPath();
		}

		character.setMoving(didMove);
		character.setMoveDirection(newDirection);
		character.setBottomLeftCorner(newCoordinate);
	}

}
