package com.me.rpg.maps;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Rectangle;

public class Waypoint
{

	private final String name;
	private final Rectangle rectangle;
	private final List<Waypoint> connections = new ArrayList<Waypoint>();

	public String getName()
	{
		return name;
	}

	public Rectangle getRectangle()
	{
		return rectangle;
	}

	public List<Waypoint> getConnections()
	{
		return connections;
	}

	public Waypoint(String name, Rectangle rectangle)
	{
		this.name = name;
		this.rectangle = rectangle;
	}

}
