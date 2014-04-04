package com.me.rpg.ai;

import java.io.Serializable;

public interface WalkAI extends Serializable
{

	public void update(float deltaTime);

	public void start();

	public void stop();

}
