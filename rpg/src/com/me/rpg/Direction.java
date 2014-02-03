package com.me.rpg;

public enum Direction
{

	RIGHT	(0,  1,  0, 0),
	LEFT	(1, -1,  0, 180),
	UP		(2,  0,  1, 90),
	DOWN	(3,  0, -1, 270);
	
	private int index, dx, dy;
	private float degrees;
	
	private Direction(int index, int dx, int dy, float degrees)
	{
		this.index = index;
		this.dx = dx;
		this.dy = dy;
		this.degrees = degrees;
	}

	public int getIndex()
	{
		return index;
	}

	public int getDx()
	{
		return dx;
	}

	public int getDy()
	{
		return dy;
	}

	public float getDegrees()
	{
		return degrees;
	}

	public static Direction getDirectionByIndex(int index)
	{
		for (Direction d : Direction.values())
		{
			if (d.getIndex() == index)
			{
				return d;
			}
		}
		throw new RuntimeException(String.format(
				"Could not find the Direction with index = %d", index));
	}

	public static Direction getDirectionByDiff(int dx, int dy)
	{
		for (Direction d : Direction.values())
		{
			if (d.getDx() == dx && d.getDy() == dy)
			{
				return d;
			}
		}
		throw new RuntimeException(
				String.format(
						"Could not find the Direction with dx = %d and dy = %d",
						dx, dy));
	}

}
