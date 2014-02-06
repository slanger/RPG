package com.me.rpg.ai;

import com.me.rpg.Coordinate;
import com.me.rpg.Direction;
import com.me.rpg.maps.Map;

public interface WalkAI
{

	public Direction update(float deltaTime, Map currentMap, Coordinate newLocation);

	public void start();

	public void stop();

}
