package com.me.rpg.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.me.rpg.characters.GameCharacter;
import com.me.rpg.maps.Map;
import com.me.rpg.utils.Coordinate;
import com.me.rpg.utils.Direction;
import com.me.rpg.utils.Location;
import com.me.rpg.utils.Waypoint;

public class FollowPathAI
	implements WalkAI
{

	private static final long serialVersionUID = -7616245852566751180L;

	private GameCharacter character;
	private Location targetLocation;
	private List<Waypoint> path;
	private boolean isDestinationReachable;
	private float totalPathDistance;
	private int currentIndex = 0;

	public FollowPathAI(GameCharacter character, Location targetLocation)
	{
		this.character = character;
		this.targetLocation = targetLocation;

		start();
	}

	@Override
	public void start()
	{
		currentIndex = 0;

		Map sourceMap = character.getCurrentMap();
		Rectangle sourceArea = character.getBoundingRectangle();
		Location sourceLocation = new Location(sourceMap, sourceArea);
		List<Waypoint> shortestPath = makePath(sourceLocation, targetLocation);

		// if destination is unreachable (i.e. stuck in a collidable), then go
		// directly to destination (hopefully the character will switch its
		// destination)
		if (shortestPath == null)
		{
			isDestinationReachable = false;
			path = new ArrayList<Waypoint>();
			path.add(new Waypoint(targetLocation));
			Coordinate sourceCenter = sourceLocation.getCenter();
			Coordinate destinationCenter = targetLocation.getCenter();
			totalPathDistance = sourceCenter.distanceTo(destinationCenter);
		}
		else
		{
			isDestinationReachable = true;
			path = shortestPath;
		}
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

		Map currentMap = character.getCurrentMap();
		Rectangle currentArea = character.getBoundingRectangle();
		Location currentLocation = new Location(currentMap, currentArea);
		Waypoint nextWaypoint = path.get(currentIndex);
		Coordinate currentCenter = currentLocation.getCenter();
		Coordinate nextCenter = nextWaypoint.getCenter();

		float xE = (float) (nextCenter.getX() - currentCenter.getX());
		float yE = (float) (nextCenter.getY() - currentCenter.getY());
		float hE = (float) Math.sqrt(xE * xE + yE * yE);

		if (hE < Coordinate.EPS)
		{
			character.setMoving(true);
			character.setCenter(nextCenter);
		}
		else
		{
			float speed = character.getSpeed();
			float oldX = character.getBottomLeftX();
			float oldY = character.getBottomLeftY();
			float dx = (xE / hE) * speed * deltaTime;
			float dy = (yE / hE) * speed * deltaTime;
			dx = (Math.abs(xE) < Math.abs(dx)) ? xE : dx;
			dy = (Math.abs(yE) < Math.abs(dy)) ? yE : dy;
			float x = oldX + dx;
			float y = oldY + dy;

			Coordinate newCoordinate = new Coordinate();
			boolean didMove = character.getCurrentMap().checkCollision(x, y,
					oldX, oldY, character, newCoordinate);
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
		}

		if (!character.isMoving())
		{
			return;
		}

		Vector2 characterCenter = new Vector2(character.getCenterX(),
				character.getCenterY());
		Rectangle nextWaypointRectangle = nextCenter
				.getSmallCenteredRectangle();
		if (nextWaypointRectangle.contains(characterCenter))
		{
			currentIndex++;
		}

		// check warp point collision
		Waypoint warpWaypoint = character.getCurrentMap()
				.checkCollisionWithWarpPoints(
						character.getBoundingRectangle());
		if (warpWaypoint != null)
		{
			character.moveToOtherMap(warpWaypoint.location);
			if (currentIndex < path.size() - 1)
			{
				currentIndex++;
			}
		}
	}

	public boolean isDestinationReachable()
	{
		return isDestinationReachable;
	}

	public float getTotalPathDistance()
	{
		return totalPathDistance;
	}

	private List<Waypoint> makePath(Location source, Location destination)
	{
		// if destination is off the map, then return null
		if (destination.isOutOfBounds())
		{
			return null;
		}

		Coordinate sourceCenter = source.getCenter();
		Coordinate destinationCenter = destination.getCenter();

		// if source and destination are connected, then return a path with just
		// the destination
		if (source.connectedTo(destination))
		{
			List<Waypoint> shortestPath = new ArrayList<Waypoint>();
			shortestPath.add(new Waypoint(destination));
			totalPathDistance = sourceCenter.distanceTo(destinationCenter);
			return shortestPath;
		}

		Map sourceMap = source.getMap();
		Map destinationMap = destination.getMap();

		List<Waypoint> worldWaypoints = new ArrayList<Waypoint>(character.getWorld().waypoints);
		Waypoint sourceWaypoint = new Waypoint(source);
		Waypoint destinationWaypoint = new Waypoint(destination);

		// find Waypoints that are connected to source
		for (Waypoint w : sourceMap.getWaypoints())
		{
			if (sourceWaypoint.connectedTo(w))
			{
				Coordinate wCenter = w.getCenter();
				float cost = sourceCenter.distanceTo(wCenter);
				sourceWaypoint.connections.add(new Waypoint.Edge(w, cost));
				w.connections.add(new Waypoint.Edge(sourceWaypoint, cost));
			}
		}

		if (sourceWaypoint.connections.size() == 0)
		{
			return null;
		}

		worldWaypoints.add(sourceWaypoint);

		// find Waypoints that are connected to destination
		for (Waypoint w : destinationMap.getWaypoints())
		{
			if (destinationWaypoint.connectedTo(w))
			{
				Coordinate wCenter = w.getCenter();
				float cost = destinationCenter.distanceTo(wCenter);
				destinationWaypoint.connections.add(new Waypoint.Edge(w, cost));
				w.connections.add(new Waypoint.Edge(destinationWaypoint, cost));
			}
		}

		if (destinationWaypoint.connections.size() == 0)
		{
			return null;
		}

		worldWaypoints.add(destinationWaypoint);

		// use Djikstra's algorithm to create shortest path
		return dijkstra(worldWaypoints, sourceWaypoint, destinationWaypoint);
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
			w.previousEdge = null;
		}
		source.distanceFromSource = 0;

		// create priority queue that comprises the unvisited set
		// priority is based on distance from the source vertex
		// smallest distance -> highest priority
		PriorityQueue<Waypoint> unvisited = new PriorityQueue<Waypoint>(graph);

		Waypoint currentVertex = unvisited.poll(); // initially equals source
		while (unvisited.contains(destination))
		{
			if (currentVertex.distanceFromSource == INFINITY)
			{
				// destination is unreachable
				return null;
			}

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
						toVertex.previousEdge = new Waypoint.Edge(currentVertex, cost);
						unvisited.add(toVertex);
					}
				}
			}

			currentVertex = unvisited.poll(); // vertex with shortest distance
												// from source
		}

		// backtracking
		List<Waypoint> path = new ArrayList<Waypoint>();
		Waypoint current = destination;
		totalPathDistance = 0;
		while (!current.equals(source))
		{
			path.add(current);
			totalPathDistance += current.previousEdge.cost;
			current = current.previousEdge.connectedWaypoint;
		}

		Collections.reverse(path);
		return path;
	}

}
