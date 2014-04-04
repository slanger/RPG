package com.me.rpg.state.action;

import com.me.rpg.characters.GameCharacter;
import com.me.rpg.utils.Direction;

public class FaceDirectionAction implements Action {

	private GameCharacter character;
	private Direction targetDirection;
	
	public FaceDirectionAction(GameCharacter character, Direction targetDirection) {
		this.character = character;
		this.targetDirection = targetDirection;
	}
	
	@Override
	public void doAction(float delta) {
		if (targetDirection != null)
			character.setFaceDirection(targetDirection);
	}

}
