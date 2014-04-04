package com.me.rpg.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

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
		path = makePath(character.getBoundingRectangle(),
				character.getCurrentMap(), destination, destinationMap);

		// if destination is unreachable (i.e. stuck in a collidable), then go
		// directly to destination (hopefully the character will switch its
		// destination)
		if (path == null)
		{
			path = new ArrayList<Waypoint>();
			path.add(new Waypoint(destination, destinationMap));
		}
	}

	@Override
	public void start()
	{
		// do nothing
	}

	@Override
	public void stop()
	{
		character.setMoving(false);
	}

	@Override
	public void update(float deltaTime)
	{
		if (currentIndex >= path.size())
			return;

		Waypoint currentWaypoint = new Waypoint(
				character.getBoundingRectangle(), character.getCurrentMap());
		Waypoint nextWaypoint = path.get(currentIndex);
		Vector2 currentCenter = currentWaypoint.rectangle
				.getCenter(new Vector2());
		Vector2 nextCenter = nextWaypoint.rectangle.getCenter(new Vector2());
		float xE = (float) (nextCenter.x - currentCenter.x);
		float yE = (float) (nextCenter.y - currentCenter.y);
		float hE = (float) Math.sqrt(xE * xE + yE * yE);
		if (hE < Coordinate.EPS)
		{
			character.setMoving(false);
			character.setCenter(new Coordinate(nextCenter));
			currentIndex++;
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
		boolean didMove = character.getCurrentMap().checkCollision(x, y, oldX,
				oldY, character, newCoordinate);
		x = newCoordinate.getX();
		y = newCoordinate.getY();

		Direction newDirection;
		if (Math.abs(yE) >= Math.abs(xE))
		{
			newDirection = (yE > 0) ? Direction.UP : Direction.DOWN;
		}
		else
		{
			newDirection = (xE >= 0) ? Direction.RIGHT : Direction.LEFT;
		}

		character.setMoving(didMove);
		character.setMoveDirection(newDirection);
		character.setBottomLeftCorner(newCoordinate);

		if (nextWaypoint.rectangle.contains(character.getCenterX(),
				character.getCenterY()))
		{
			currentIndex++;
			if (nextWaypoint.isWarpPoint())
			{
				currentIndex++;
				Waypoint warpToWaypoint = nextWaypoint.connectedWarpPoint;
				Map warpMap = warpToWaypoint.mapLocatedOn;
				Rectangle warpLocation = warpToWaypoint.rectangle;
				character.moveToOtherMap(warpMap, warpLocation);
			}
		}
	}

	private List<Waypoint> makePath(Rectangle source, Map sourceMap,
			Rectangle destination, Map destinationMap)
	{
		List<Waypoint> shortestPath;

		Vector2 sourceCenter = source.getCenter(new Vector2());
		Vector2 destinationCenter = destination.getCenter(new Vector2());

		// if source and destination are connected, then return a path with just
		// the destination
		if (sourceMap.equals(destinationMap)
				&& sourceMap.pointsConnected(sourceCenter, destinationCenter))
		{
			shortestPath = new ArrayList<Waypoint>();
			shortestPath.add(new Waypoint(destination, destinationMap));
			return shortestPath;
		}

		// find Waypoint that is closest yet still connected to the source
		List<Waypoint> sourceWaypoints = sourceMap.getWaypoints();
		Waypoint closestWaypointToSource = null;
		float lengthSquaredToSource = Float.MAX_VALUE;
		for (Waypoint w : sourceWaypoints)
		{
			Vector2 wCenter = w.rectangle.getCenter(new Vector2());
			if (sourceMap.pointsConnected(sourceCenter, wCenter))
			{
				float deltaX = Math.abs(sourceCenter.x - wCenter.x);
				float deltaY = Math.abs(sourceCenter.y - wCenter.y);
				float lengthSquared = deltaX * deltaX + deltaY * deltaY;
				if (closestWaypointToSource == null
						|| lengthSquared < lengthSquaredToSource)
				{
					closestWaypointToSource = w;
					lengthSquaredToSource = lengthSquared;
				}
			}
		}

		if (closestWaypointToSource == null)
			return null;

		// find Waypoint that is closest yet still connected to the destination
		List<Waypoint> destinationWaypoints = destinationMap.getWaypoints();
		Waypoint closestWaypointToDestination = null;
		float lengthSquaredToDestination = Float.MAX_VALUE;
		for (Waypoint w : destinationWaypoints)
		{
			Vector2 wCenter = new Vector2();
			w.rectangle.getCenter(wCenter);
			if (destinationMap.pointsConnected(destinationCenter, wCenter))
			{
				float deltaX = Math.abs(destinationCenter.x - wCenter.x);
				float deltaY = Math.abs(destinationCenter.y - wCenter.y);
				float lengthSquared = deltaX * deltaX + deltaY * deltaY;
				if (closestWaypointToDestination == null
						|| lengthSquared < lengthSquaredToDestination)
				{
					closestWaypointToDestination = w;
					lengthSquaredToDestination = lengthSquared;
				}
			}
		}

		if (closestWaypointToDestination == null)
			return null;

		// use Djikstra's algorithm to create shortest path
		shortestPath = dijkstra(character.getWorld().waypoints,
				closestWaypointToSource, closestWaypointToDestination);
		shortestPath.add(new Waypoint(destination, destinationMap));
		return shortestPath;
	}

	/**
	 * Returns the smallest cost of traversing from source to destination
	 */
	private List<Waypoint> dijkstra(List<Waypoint> graph, Waypoint source,
			Waypoint destination)
	{
		final float INFINITY = Float.MAX_VALUE / 2;

		for (Waypoint w : graph)
		{
			w.distanceFromSource = INFINITY;
			w.previousVertex = null;
		}
		source.distanceFromSource = 0;

		// create priority queue that comprises the unvisited set
		// priority is based on distance from the source vertex
		// smallest distance -> highest priority
		PriorityQueue<Waypoint> unvisited = new PriorityQueue<Waypoint>(graph);

		Waypoint currentVertex = unvisited.poll(); // initially equals source
													// vertex
		while (unvisited.contains(destination)
				&& currentVertex.distanceFromSource != INFINITY)
		{
			for (Waypoint.Edge e : currentVertex.connections)
			{// for each connection to current vertex...
				Waypoint toVertex = e.connectedWaypoint;
				float cost = e.cost;
				if (unvisited.contains(toVertex))
				{// toVertex is in unvisited set, check tentative distance
					float newDist = currentVertex.distanceFromSource + cost;
					if (newDist < toVertex.distanceFromSource)
					{// update priority queue
						unvisited.remove(toVertex);
						toVertex.distanceFromSource = newDist;
						toVertex.previousVertex = currentVertex;
						unvisited.add(toVertex);
					}
				}
			}
			currentVertex = unvisited.poll(); // vertex with shortest distance
												// from source
		}

		List<Waypoint> path = new ArrayList<Waypoint>();
		Waypoint current = destination;
		while (!current.equals(source))
		{
			path.add(current);
			current = current.previousVertex;
		}
		path.add(source);
		Collections.reverse(path);
		return path;
	}

}
