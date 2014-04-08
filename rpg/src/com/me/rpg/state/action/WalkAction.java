package com.me.rpg.state.action;

import com.me.rpg.ai.FollowPathAI;
import com.me.rpg.characters.GameCharacter;
import com.me.rpg.utils.Location;

public class WalkAction
	implements Action
{

	private static final long serialVersionUID = 4776613710435812862L;

	private FollowPathAI walkAI;

	public WalkAction(GameCharacter character, Location targetLocation)
	{
		walkAI = new FollowPathAI(character, targetLocation);
	}

	public void start()
	{
		walkAI.start();
	}

	public void stop()
	{
		walkAI.stop();
	}

	@Override
	public void doAction(float delta)
	{
		walkAI.update(delta);
	}

}
