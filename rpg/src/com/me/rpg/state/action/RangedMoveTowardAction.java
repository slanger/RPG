package com.me.rpg.state.action;

import com.me.rpg.ai.FollowPathAI;
import com.me.rpg.ai.WalkAI;
import com.me.rpg.characters.GameCharacter;
import com.me.rpg.combat.Weapon;
import com.me.rpg.utils.Coordinate;
import com.me.rpg.utils.Location;

public class RangedMoveTowardAction implements Action {
	
	private GameCharacter character;
	
	public RangedMoveTowardAction(GameCharacter character) {
		this.character = character;
	}
	
	@Override
	public void doAction(float delta) {
		System.err.printf("rangedmovetoward\n");
		GameCharacter target = character.getRememberedAttacker();
		if (target == null) return;
		
		Coordinate tarCen = target.getCenter();
		Coordinate meCen = character.getCenter();
		Coordinate diff = tarCen.translate(-meCen.getX(), -meCen.getY());
		int dx = -1;
		int dy = -1;
		if (Math.abs(diff.getX()) > Math.abs(diff.getY())) {
			dx = 0;
			dy = (diff.getY() > 0 ? 1 : -1);
		} else {
			dx = (diff.getX() > 0 ? 1 : -1);
			dy = 0;
		}
		Weapon weapon = character.getEquippedWeapon();
		int range = weapon.getRange();
		Coordinate desiredLoc = tarCen.translate(range*dy, range*dx);
		WalkAI walkai = new FollowPathAI(character, new Location(character.getCurrentMap(), desiredLoc));
		walkai.update(delta);
	}

}
