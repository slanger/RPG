package com.me.rpg.ai;

import com.me.rpg.Coordinate;
import com.me.rpg.Direction;
import com.me.rpg.maps.Map;

public interface WalkAI
{

	public void update(float deltaTime, Map currentMap);

	public void start();

	public void stop();

}
