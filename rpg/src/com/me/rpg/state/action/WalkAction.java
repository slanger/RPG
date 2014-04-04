package com.me.rpg.state.action;

import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.ai.FollowPathAI;
import com.me.rpg.characters.GameCharacter;
import com.me.rpg.maps.Map;
import com.me.rpg.utils.Coordinate;

public class WalkAction implements Action
{

	private static final long serialVersionUID = 4776613710435812862L;

	private FollowPathAI walkAI;

	public WalkAction(GameCharacter character, Coordinate targetLocation, Map targetMap)
	{
		Rectangle targetLocationRectangle = targetLocation.getSmallCenteredRectangle();
		walkAI = new FollowPathAI(character, targetLocationRectangle, targetMap);
	}

	@Override
	public void doAction(float delta)
	{
		walkAI.update(delta);
	}

}
