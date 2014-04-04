package com.me.rpg.state.action;

import com.me.rpg.characters.GameCharacter;

public class SetStrafeAction implements Action {
	
	private GameCharacter character;
	private boolean strafe;
	
	public SetStrafeAction(GameCharacter character, boolean strafe) {
		this.character = character;
		this.strafe = strafe;
	}
	
	@Override
	public void doAction(float delta) {
		character.setStrafing(strafe);
	}

}
