package com.me.rpg.ai;

import com.me.rpg.Coordinate;

public interface WalkAI
{

	public Coordinate update(float deltaTime);

	public void start();

	public void stop();

}
