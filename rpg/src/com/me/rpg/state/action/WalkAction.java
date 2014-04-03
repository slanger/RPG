package com.me.rpg.state.action;

import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.ai.FollowPathAI;
import com.me.rpg.characters.GameCharacter;
import com.me.rpg.utils.Coordinate;

public class WalkAction implements Action
{

	private static final long serialVersionUID = 4776613710435812862L;

	private Rectangle targetLocation;
	private FollowPathAI walkAI;

	public WalkAction(GameCharacter character, Coordinate targetLocation)
	{
		this.targetLocation = targetLocation.getCenteredRectangle(
				2 * Coordinate.EPS, 2 * Coordinate.EPS);
		walkAI = new FollowPathAI(character, this.targetLocation,
				character.getCurrentMap());
	}

	@Override
	public void doAction(float delta)
	{
		walkAI.update(delta);
	}

}
