package com.me.rpg.state.action;

import com.me.rpg.ai.FollowPathAI;
import com.me.rpg.ai.WalkAI;
import com.me.rpg.characters.GameCharacter;
import com.me.rpg.utils.Coordinate;
import com.me.rpg.utils.Direction;

public class MeleeMoveTowardAction implements Action {
	
	private GameCharacter character;
	private WalkAI walkai;
	
	// Assumes that this character has a rememberedTarget, and that the char
	// is equipped with a melee weapon.
	public MeleeMoveTowardAction(GameCharacter character) {
		this.character = character;
	}
	
	@Override
	public void doAction(float delta) {
		GameCharacter target = character.getRememberedAttacker();
		Coordinate tarCen = target.getCenter();
		Coordinate[] shiftedTarget = new Coordinate[4];
		float width = target.getSpriteWidth() + character.getSpriteWidth();
		float height = target.getSpriteHeight() + character.getSpriteHeight();
		for (int i = 0; i < 4; ++i) {
			Direction d = Direction.getDirectionByIndex(i);
			shiftedTarget[i] = tarCen.translate(d.getDx() * width / 2.0f, d.getDy() * height / 2.0f);
		}
		// for now, just go to the nearest one, by direct path
		Coordinate myCen = character.getCenter();
		int best = 0;
		for (int i = 1; i < 4; ++i) {
			if (shiftedTarget[best].distance2(myCen) > shiftedTarget[i].distance2(myCen)) {
				best = i;
			}
		}
		
		walkai = new FollowPathAI(target, shiftedTarget[best].getSmallCenteredRectangle());
		walkai.update(delta, character.getCurrentMap());
	}

}
