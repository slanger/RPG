package com.me.rpg.state.transition;

import com.me.rpg.characters.GameCharacter;

public class TargetDeadCondition implements Condition {
	
	private static final long serialVersionUID = 1L;
	
	private GameCharacter character;
	
	public TargetDeadCondition(GameCharacter character) {
		this.character = character;
	}
	
	@Override
	public boolean test() {
		GameCharacter target = character.getRememberedAttacker();
		return (target == null) || (target.isDead());
	}

}
