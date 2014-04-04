package com.me.rpg.state.action;

import com.me.rpg.characters.GameCharacter;
import com.me.rpg.utils.Coordinate;
import com.me.rpg.utils.Direction;

public class AttackAction implements Action {
	
	private GameCharacter character;
	
	public AttackAction(GameCharacter character) {
		this.character = character;
	}
	
	@Override
	public void doAction(float delta) {
		// figure out what direction to face, then attack!
		GameCharacter target = character.getRememberedAttacker();
		if (target != null) {
			Coordinate tarCen = target.getCenter();
			Coordinate meCen = character.getCenter();
			Direction d = meCen.getDirection(tarCen);
			character.setFaceDirection(d);
		}
		character.initiateAttack();
	}

}
