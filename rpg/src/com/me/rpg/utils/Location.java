package com.me.rpg.utils;

import java.io.Serializable;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.me.rpg.maps.Map;

/**
 * A data structure for describing locations in the game world. It contains a
 * Rectangle to represent a region or area in the World and the Map that the
 * area is located on.
 */
public class Location
	implements Serializable
{

	private static final long serialVersionUID = -3813746950427024003L;

	private Map map;
	private Rectangle area;

	public Map getMap()
	{
		return map;
	}

	public void setMap(Map map)
	{
		this.map = map;
	}

	public Rectangle getArea()
	{
		return area;
	}

	public void setArea(Rectangle area)
	{
		this.area = area;
	}

	public Coordinate getBottomLeftCorner()
	{
		return new Coordinate(area.x, area.y);
	}

	public void setBottomLeftCorner(Coordinate bottomLeftCorner)
	{
		area = new Rectangle(bottomLeftCorner.getX(), bottomLeftCorner.getY(),
				area.width, area.height);
	}

	public Coordinate getCenter()
	{
		return new Coordinate(area.getCenter(new Vector2()));
	}

	public void setCenter(Coordinate center)
	{
		area = center.getCenteredRectangle(area.width, area.height);
	}

	public boolean connectedTo(Location other)
	{
		return (map.equals(other.map) && map.rectanglesConnected(area, other.area));
	}

	public boolean isOutOfBounds()
	{
		return map.isRectangleOutOfBounds(area);
	}

	public Location(Map map, float centerX, float centerY)
	{
		this(map, new Coordinate(centerX, centerY));
	}

	public Location(Map map, Coordinate center)
	{
		this(map, center.getSmallCenteredRectangle());
	}

	public Location(Map map, Coordinate center, float width, float height)
	{
		this(map, center.getCenteredRectangle(width, height));
	}

	public Location(Map map, Rectangle area)
	{
		this.map = map;
		this.area = area;
	}

	@Override
	public String toString()
	{
		return String.format("(%s, %s)", map.getName(), area);
	}

}
