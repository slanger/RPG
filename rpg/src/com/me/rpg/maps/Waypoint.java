package com.me.rpg.maps;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Rectangle;

public class Waypoint
	implements Comparable<Waypoint>, Serializable
{

	private static final long serialVersionUID = 4313230046743309396L;

	public final Rectangle rectangle;
	public final Map mapLocatedOn;
	public final String name;
	public final String connectedWarpPointName;
	public final List<Edge> connections = new ArrayList<Edge>();
	public Waypoint connectedWarpPoint;
	// to be used by dijkstra's algorithm
	public float distanceFromSource;
	public Waypoint previousVertex;

	public Waypoint(Rectangle rectangle, Map mapLocatedOn)
	{
		this.rectangle = rectangle;
		this.mapLocatedOn = mapLocatedOn;
		name = null;
		connectedWarpPointName = null;
	}

	public Waypoint(Rectangle rectangle, Map mapLocatedOn, String name,
			String connectedWarpPointName)
	{
		this.rectangle = rectangle;
		this.mapLocatedOn = mapLocatedOn;
		this.name = name;
		this.connectedWarpPointName = connectedWarpPointName;
	}

	public boolean isWarpPoint()
	{
		return !(connectedWarpPointName == null);
	}

	@Override
	public String toString()
	{
		return String.format("(%s; %s); isWarpPoint=%s", rectangle.toString(),
				mapLocatedOn.getMapType().getMapName(), isWarpPoint());
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

	}

}
