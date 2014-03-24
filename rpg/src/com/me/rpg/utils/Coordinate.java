package com.me.rpg.utils;

import java.io.Serializable;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Coordinate implements Serializable
{

	private static final long serialVersionUID = -1843687349069001351L;

	public static final Coordinate ZERO = new Coordinate(0f, 0f);
	public static final float EPS = 0.00000001f;

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

	public boolean isNear(Coordinate target) {
		double diff = Math.abs(target.x - x) + Math.abs(target.y - y);
		return diff < EPS;
	}
	
	public float distance2(Coordinate target) {
		return (x-target.x)*(x-target.x) + (y-target.y)*(y-target.y);
	}
	
	public Rectangle getCenteredRectangle(float width, float height) {
		return new Rectangle(x - width/2.0f, y - width/2.0f, width, height);
	}
	
	public Rectangle getBottomLeftRectangle(float width, float height) {
		return new Rectangle(x, y, width, height);
	}

}
