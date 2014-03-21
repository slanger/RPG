package com.me.rpg.state.action;

import com.me.rpg.characters.GameCharacter;
import com.me.rpg.utils.Coordinate;

public class WalkAction implements Action {
	
	private GameCharacter character;
	private Coordinate targetLocation;
	
	public WalkAction(GameCharacter character, Coordinate targetLocation) {
		this.character = character;
		this.targetLocation = targetLocation;
	}
	
	@Override
	public void doAction(float delta) {
		character.basicMoveToward(targetLocation, delta);
	}
	
	public void setNewLoc(Coordinate newTargetLocation) {
		targetLocation = newTargetLocation;
	}

}
