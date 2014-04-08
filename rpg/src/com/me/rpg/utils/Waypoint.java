package com.me.rpg.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Rectangle;

public class Waypoint
	implements Comparable<Waypoint>, Serializable
{

	private static final long serialVersionUID = 4313230046743309396L;

	public final Location location;
	public final String name;
	public final String connectedWarpPointName;
	public final List<Edge> connections = new ArrayList<Edge>();
	public Waypoint connectedWarpPoint;
	// to be used by dijkstra's algorithm
	public float distanceFromSource;
	public Waypoint previousVertex;

	public Waypoint(Location location)
	{
		this(location, null, null);
	}

	public Waypoint(Location location, String name,
			String connectedWarpPointName)
	{
		this.location = location;
		this.name = name;
		this.connectedWarpPointName = connectedWarpPointName;
	}

	public Rectangle getRectangle()
	{
		return location.getArea();
	}

	public boolean isWarpPoint()
	{
		return !(connectedWarpPointName == null);
	}

	public Coordinate getCenter()
	{
		return location.getCenter();
	}

	@Override
	public String toString()
	{
		return String.format("%s; connections: %s; isWarpPoint=%s", location,
				connections, isWarpPoint());
	}

	// compare distances of the Waypoints
	@Override
	public int compareTo(Waypoint other)
	{
		if (this.distanceFromSource < other.distanceFromSource)
			return -1;
		if (this.distanceFromSource > other.distanceFromSource)
			return 1;
		return 0;
	}

	public static class Edge
		implements Serializable
	{

		private static final long serialVersionUID = 593353468577188650L;

		public final Waypoint connectedWaypoint;
		public final float cost;

		public Edge(Waypoint connectedWaypoint, float cost)
		{
			this.connectedWaypoint = connectedWaypoint;
			this.cost = cost;
		}

		@Override
		public String toString()
		{
			return String.format("(%s, %.2f)", connectedWaypoint.location, cost);
		}

	}

}
