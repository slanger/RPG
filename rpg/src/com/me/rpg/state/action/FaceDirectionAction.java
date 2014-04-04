package com.me.rpg.state.action;

import com.me.rpg.characters.GameCharacter;
import com.me.rpg.utils.Direction;

public class FaceDirectionAction implements Action {

	private static final long serialVersionUID = 5489051115011963913L;

	private GameCharacter character;
	private Direction targetDirection;
	
	public FaceDirectionAction(GameCharacter character, Direction targetDirection) {
		this.character = character;
		this.targetDirection = targetDirection;
	}
	
	@Override
	public void doAction(float delta) {
		character.setFaceDirection(targetDirection);
	}

}
