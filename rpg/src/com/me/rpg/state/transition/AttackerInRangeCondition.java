package com.me.rpg.state.transition;

import com.me.rpg.characters.GameCharacter;
import com.me.rpg.combat.Weapon;
import com.me.rpg.utils.Coordinate;

public class AttackerInRangeCondition implements Condition {

	private static final long serialVersionUID = 3304232059378493018L;

	private GameCharacter character;
	
	public AttackerInRangeCondition(GameCharacter character) {
		this.character = character;
	}
	
	@Override
	public boolean test() {
		GameCharacter target = character.getRememberedAttacker();
		if (target == null) return false;
		
		Coordinate tarCen = target.getCenter();
		Coordinate meCen = character.getCenter();
		Coordinate diff = tarCen.translate(-meCen.getX(), -meCen.getY());
		float dist = Math.max(Math.abs(diff.getX()), Math.abs(diff.getY()));
		Weapon weapon = character.getEquippedWeapon();
		int range = weapon.getRange();
		return dist < range + (target.getSpriteWidth()+character.getSpriteWidth())/2.0f;
	}

}
