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

	public FollowPathAI(GameCharacter character, Rectangle ... path)
	{
		if (character == null)
			throw new NullPointerException("Can't have a null character for the AI");
		if (path == null)
			throw new NullPointerException("Don't be passing in a null path yo");
		for (int i = 0; i < path.length; ++i)
		{
			if (path[i] == null)
				throw new NullPointerException("Can't have a null rectangle in the path.");
		}
		this.character = character;
		this.path = new Rectangle[path.length];
		System.arraycopy(path, 0, this.path, 0, path.length);
	}
	
	public void setNewPath(Rectangle ... newPath)
	{
		if (newPath == null)
			throw new NullPointerException("Don't be passing in a null path yo");
		for (int i = 0; i < newPath.length; ++i)
		{
			if (newPath[i] == null)
				throw new NullPointerException("Can't have a null rectangle in the path.");
		}
		currentIndex = 0;
		path = new Rectangle[newPath.length];
		System.arraycopy(newPath, 0, path, 0, newPath.length);
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
	
	public void reset() {
		currentIndex = 0;
	}

	@Override
	public void update(float deltaTime, Map currentMap)
	{
		if (currentIndex >= path.length)
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
		if (hE < Coordinate.EPS) {
			character.setMoving(false);
			character.setCenter(new Coordinate(nextCenter));
			++currentIndex;
			return;
		}
		
		float speed = character.getSpeed();
		float oldX = character.getBottomLeftX();
		float oldY = character.getBottomLeftY();
		float dx = (xE / hE) * speed * deltaTime;
		float dy = (yE / hE) * speed * deltaTime;
		dx = (Math.abs(xE) < Math.abs(dx) ? xE : dx);
		dy = (Math.abs(yE) < Math.abs(dy) ? yE : dy);
		float x = oldX + dx;
		float y = oldY + dy;

		Coordinate newCoordinate = new Coordinate();
		boolean didMove = currentMap.checkCollision(x, y, oldX,
				oldY, character, newCoordinate);
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

		character.setMoving(didMove);
		character.setMoveDirection(newDirection);
		character.setBottomLeftCorner(newCoordinate);
	}

}
