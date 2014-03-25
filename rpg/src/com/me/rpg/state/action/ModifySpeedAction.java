package com.me.rpg.state.action;

import com.me.rpg.characters.GameCharacter;

public class ModifySpeedAction implements Action {
	
	private static final long serialVersionUID = 5789704968899699340L;
	
	private GameCharacter character;
	private float modifier;
	
	public ModifySpeedAction(GameCharacter character, float modifier) {
		this.character = character;
		this.modifier = modifier;
	}
	
	@Override
	/**
	 * Note: First application applies the modifier, second application undoes the first.
	 */
	public void doAction(float delta) {
		float oldModifier = character.getSpeedModifier();
		character.setSpeedModifier(oldModifier + modifier);
		modifier = -modifier;
	}

}
