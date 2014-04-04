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

	public Vector2 getVector2()
	{
		return new Vector2(x, y);
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
	
	public Coordinate translate(Coordinate transVector) {
		return translate(transVector.x, transVector.y);
	}
	
	public Coordinate translate(float x, float y) {
		return new Coordinate(this.x + x, this.y + y);
	}

	public boolean isNear(Coordinate target) {
		double diff = Math.abs(target.x - x) + Math.abs(target.y - y);
		return diff < EPS;
	}
	
	public float distanceTo(Coordinate target)
	{
		float deltaX = x - target.x;
		float deltaY = y - target.y;
		return (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
	}
	
	public float distance2(Coordinate target)
	{
		float deltaX = x - target.x;
		float deltaY = y - target.y;
		return (deltaX * deltaX + deltaY * deltaY);
	}
	
	public Rectangle getSmallCenteredRectangle() {
		return getCenteredRectangle(EPS*2f, EPS*2f);
	}
	
	public Rectangle getCenteredRectangle(float width, float height) {
		return new Rectangle(x - width/2.0f, y - width/2.0f, width, height);
	}
	
	public Rectangle getBottomLeftRectangle(float width, float height) {
		return new Rectangle(x, y, width, height);
	}
	
	/**
	 * Returns the relative direction from this Coordinate to target
	 * @param target
	 * @return
	 */
	public Direction getDirection(Coordinate target) {
		Coordinate diffs = target.translate(-x, -y);
		int dx = 0;
		int dy = 0;
		if (Math.abs(diffs.x) > Math.abs(diffs.y)) {
			dx = (diffs.x > 0 ? 1 : -1);
			dy = 0;
		} else {
			dx = 0;
			dy = (diffs.y > 0 ? 1 : -1);
		}
		return Direction.getDirectionByDiff(dx, dy);
	}

}
