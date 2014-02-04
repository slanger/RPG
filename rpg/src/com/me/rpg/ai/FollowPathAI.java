package com.me.rpg.ai;

import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.Character;
import com.me.rpg.Coordinate;
import com.me.rpg.maps.Map;

public class FollowPathAI implements WalkAI
{

	private Character character;
	private Rectangle[] path;
	private WalkAI previousWalkAI;
	private int currentIndex = 0;

	public FollowPathAI(Character character, Rectangle[] path, WalkAI previousWalkAI)
	{
		this.character = character;
		this.path = path;
		this.previousWalkAI = previousWalkAI;
	}

	@Override
	public void start()
	{
		character.setMoving(true);
	}

	@Override
	public void stop()
	{
		character.setMoving(false);
		character.setWalkAI(previousWalkAI);
	}

	@Override
	public Coordinate update(float deltaTime, Map currentMap)
	{
		float xE = (float) (path[currentIndex + 1].getX() - path[currentIndex].getX());
		float yE = (float) (path[currentIndex + 1].getY() - path[currentIndex].getY());
		float hE = (float) Math.sqrt(xE * xE + yE * yE);
		
		Coordinate returnCoordinate = new Coordinate();
		float speed = character.getSpeed();
		float x = character.getX() + (xE / hE) * speed;
		float y = character.getY() + (yE / hE) * speed;
		returnCoordinate.setX(x);
		returnCoordinate.setY(y);

		final float PRECISION = 1.0f;

		if (Math.abs(x - (float) (path[currentIndex + 1].getX())) < PRECISION
				&& Math.abs(y - (float) (path[currentIndex + 1].getY())) < PRECISION)
		{
			currentIndex++;
		}

		if (currentIndex >= path.length - 1)
		{
			stop();
		}

		return returnCoordinate;
	}

}
