package com.me.rpg;

import com.badlogic.gdx.math.Vector2;

public class Coordinate
{

	public static final Coordinate ZERO = new Coordinate(0f, 0f);

	private float x;
	private float y;

	/**
	 * Empty constructor. Use this if you want to set x and y after
	 * construction.
	 */
	public Coordinate()
	{

	}

	public Coordinate(float x, float y)
	{
		this.x = x;
		this.y = y;
	}

	public Coordinate(Vector2 v)
	{
		this.x = v.x;
		this.y = v.y;
	}

	public float getX()
	{
		return x;
	}

	public float getY()
	{
		return y;
	}

	public void setX(float x)
	{
		this.x = x;
	}

	public void setY(float y)
	{
		this.y = y;
	}

	/**
	 * Prints the coordinate, X first, then Y
	 */
	@Override
	public String toString()
	{
		return String.format("(%f, %f)", x, y);
	}

	public static Coordinate copy(Coordinate c)
	{
		return new Coordinate(c.getX(), c.getY());
	}

}
