package com.me.rpg.state.action;

import com.me.rpg.characters.GameCharacter;

public class RememberAttackerAction implements Action {
	
	private static final long serialVersionUID = 1L;
	
	private GameCharacter character;
	
	public RememberAttackerAction(GameCharacter character) {
		this.character = character;
	}
	
	@Override
	public void doAction(float delta) {
		character.rememberLastAttacker();
	}

}
