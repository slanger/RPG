package com.me.rpg.ai;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.me.rpg.characters.GameCharacter;
import com.me.rpg.maps.Map;
import com.me.rpg.maps.Waypoint;
import com.me.rpg.utils.Coordinate;
import com.me.rpg.utils.Direction;

public class FollowPathAI
	implements WalkAI
{

	private static final long serialVersionUID = -7616245852566751180L;

	private GameCharacter character;
	private List<Waypoint> path;
	private int currentIndex = 0;

	public FollowPathAI(GameCharacter character, Rectangle destination,
			Map destinationMap)
	{
		if (character == null)
			throw new NullPointerException(
					"Can't have a null character for the AI");
		if (destination == null)
			throw new NullPointerException(
					"Can't have a null destination for the AI");
		if (destinationMap == null)
			throw new NullPointerException(
					"Can't have a null destination map for the AI");
		this.character = character;
		this.path = makePath(character.getBoundingRectangle(),
				character.getCurrentMap(), destination, destinationMap);
	}

	private List<Waypoint> makePath(Rectangle source, Map sourceMap,
			Rectangle destination, Map destinationMap)
	{
		List<Waypoint> waypoints = new ArrayList<Waypoint>();
		Vector2 sourceCenter = new Vector2();
		source.getCenter(sourceCenter);
		Vector2 destinationCenter = new Vector2();
		destination.getCenter(destinationCenter);
		if (sourceMap.equals(destinationMap) && sourceMap.pointsConnected(sourceCenter, destinationCenter))
		{
			waypoints.add(new Waypoint(destination));
			return waypoints;
		}

		List<Waypoint> sourceWaypoints = sourceMap.getWaypoints();
		Waypoint closestWaypointToSource = null;
		float lengthSquaredToSource = Float.MAX_VALUE;
		for (Waypoint w : sourceWaypoints)
		{
			Vector2 wCenter = new Vector2();
			w.rectangle.getCenter(wCenter);
			if (sourceMap.pointsConnected(sourceCenter, wCenter))
			{
				float deltaX = Math.abs(sourceCenter.x - wCenter.x);
				float deltaY = Math.abs(sourceCenter.y - wCenter.y);
				float lengthSquared = deltaX * deltaX + deltaY * deltaY;
				if (closestWaypointToSource == null || lengthSquared < lengthSquaredToSource)
				{
					closestWaypointToSource = w;
					lengthSquaredToSource = lengthSquared;
				}
			}
		}

		// this may be too harsh
		if (closestWaypointToSource == null)
			throw new RuntimeException("Cannot follow path");

		List<Waypoint> destinationWaypoints = destinationMap.getWaypoints();
		Waypoint closestWaypointToDestination = null;
		float lengthSquaredToDestination = Float.MAX_VALUE;
		for (Waypoint w : sourceWaypoints)
		{
			Vector2 wCenter = new Vector2();
			w.rectangle.getCenter(wCenter);
			if (sourceMap.pointsConnected(destinationCenter, wCenter))
			{
				float deltaX = Math.abs(destinationCenter.x - wCenter.x);
				float deltaY = Math.abs(destinationCenter.y - wCenter.y);
				float lengthSquared = deltaX * deltaX + deltaY * deltaY;
				if (closestWaypointToDestination == null || lengthSquared < lengthSquaredToDestination)
				{
					closestWaypointToDestination = w;
					lengthSquaredToDestination = lengthSquared;
				}
			}
		}

		// this may be too harsh
		if (closestWaypointToDestination == null)
			throw new RuntimeException("Cannot follow path");

		// TODO use Djikstra's algorithm to create path
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
		if (hE < Coordinate.EPS)
		{
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
		boolean didMove = currentMap.checkCollision(x, y, oldX, oldY,
				character, newCoordinate);
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
