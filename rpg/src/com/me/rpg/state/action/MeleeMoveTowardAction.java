package com.me.rpg.state.action;

import com.me.rpg.ai.FollowPathAI;
import com.me.rpg.ai.WalkAI;
import com.me.rpg.characters.GameCharacter;
import com.me.rpg.utils.Coordinate;
import com.me.rpg.utils.Direction;
import com.me.rpg.utils.Location;

public class MeleeMoveTowardAction implements Action {

	private static final long serialVersionUID = -1027746630531574165L;

	// update walk AI every UPDATE_INTERVAL seconds
	private static final float UPDATE_INTERVAL = 1.0f; // in seconds

	private GameCharacter character;
	private WalkAI walkAI;
	// start at UPDATE_INTERVAL so that we can initialize walkAI
	private float timePassedSinceLastUpdate = UPDATE_INTERVAL; // in seconds
	
	// Assumes that this character has a rememberedTarget, and that the char
	// is equipped with a melee weapon.
	public MeleeMoveTowardAction(GameCharacter character) {
		this.character = character;
	}
	
	@Override
	public void doAction(float delta)
	{
		timePassedSinceLastUpdate += delta;
		if (timePassedSinceLastUpdate < UPDATE_INTERVAL)
		{
			walkAI.update(delta);
			return;
		}

		timePassedSinceLastUpdate -= UPDATE_INTERVAL;

		Coordinate targetCoordinate;
		GameCharacter target = character.getRememberedAttacker();

		if (character.getCurrentMap().equals(target.getCurrentMap()))
		{
			Coordinate tarCen = target.getCenter();
			Coordinate[] shiftedTarget = new Coordinate[4];
			float width = target.getSpriteWidth() + character.getSpriteWidth();
			float height = target.getSpriteHeight()
					+ character.getSpriteHeight();
			for (int i = 0; i < 4; ++i)
			{
				Direction d = Direction.getDirectionByIndex(i);
				shiftedTarget[i] = tarCen.translate(d.getDx() * width / 2.0f,
						d.getDy() * height / 2.0f);
			}
			// TODO for now, just go to the nearest one, by direct path
			Coordinate myCen = character.getCenter();
			int best = 0;
			for (int i = 1; i < 4; ++i)
			{
				if (shiftedTarget[best].distanceSquared(myCen) > shiftedTarget[i]
						.distanceSquared(myCen))
				{
					best = i;
				}
			}

			targetCoordinate = shiftedTarget[best];
		}
		else
		{
			targetCoordinate = target.getCenter();
		}

		Location targetLocation = new Location(target.getCurrentMap(),
				targetCoordinate.getSmallCenteredRectangle());
		walkAI = new FollowPathAI(character, targetLocation);
		walkAI.update(delta);
	}

}
