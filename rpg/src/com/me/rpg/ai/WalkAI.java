package com.me.rpg.ai;

import com.me.rpg.Coordinate;
import com.me.rpg.maps.Map;

public interface WalkAI
{

	public Coordinate update(float deltaTime, Map currentMap);

	public void start();

	public void stop();

}
