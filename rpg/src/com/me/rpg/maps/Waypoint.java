package com.me.rpg.maps;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Rectangle;

public class Waypoint
{

	public final String name;
	public final Rectangle rectangle;
	public final String connectedWarpPointName;
	public final List<Edge> connections = new ArrayList<Edge>();

	public Waypoint(Rectangle rectangle)
	{
		this.rectangle = rectangle;
		name = null;
		connectedWarpPointName = null;
	}

	public Waypoint(Rectangle rectangle, String name, String connectedWarpPointName)
	{
		this.rectangle = rectangle;
		this.name = name;
		this.connectedWarpPointName = connectedWarpPointName;
	}

	public boolean isWarpPoint()
	{
		return !(connectedWarpPointName == null);
	}

	public static class Edge
	{

		public final Waypoint connectedWaypoint;
		public final float cost;

		public Edge(Waypoint connectedWaypoint, float cost)
		{
			this.connectedWaypoint = connectedWaypoint;
			this.cost = cost;
		}

	}

}
