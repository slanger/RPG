package com.me.rpg.ai;

import java.io.Serializable;

import com.me.rpg.maps.Map;

public interface WalkAI extends Serializable
{

	public void update(float deltaTime, Map currentMap);

	public void start();

	public void stop();

}
