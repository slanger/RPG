package com.me.rpg;

public class Coordinate {
	
	public static final Coordinate ZERO = new Coordinate(0f, 0f);
	
	private float x;
	private float y;
	
	public Coordinate(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public void setX(float x) {
		this.x = x;
	}
	
	public void setY(float y) {
		this.y = y;
	}
	
	/**
	 * Prints the coordinate, X first, then Y
	 */
	@Override
	public String toString() {
		return String.format("(%f, %f)", x, y);
	}
}
