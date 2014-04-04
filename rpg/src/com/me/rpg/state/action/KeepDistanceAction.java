package com.me.rpg.state.action;

import com.me.rpg.characters.GameCharacter;
import com.me.rpg.utils.Coordinate;
import com.me.rpg.utils.Direction;

public class KeepDistanceAction implements Action {
	
	private GameCharacter character;
	private float distance;
	private WalkAction action;
	
	public KeepDistanceAction(GameCharacter character, float distance) {
		this.character = character;
		this.distance = distance;
		action = new WalkAction(character, new Coordinate());
	}
	
	@Override
	public void doAction(float delta) {
		GameCharacter target = character.getRememberedAttacker();
		Coordinate pCenter = target.getCenter();
		Coordinate meCenter = character.getCenter();
		float dist = meCenter.distance2(pCenter);
		meCenter.setX(2*meCenter.getX() - pCenter.getX());
		meCenter.setY(2*meCenter.getY() - pCenter.getY());
		if (dist > distance*distance+20) {
			action.setNewLoc(pCenter);
		} else if (dist < distance*distance-20) {
			action.setNewLoc(meCenter);
		} else {
			return;
		}
		action.doAction(delta);
		Direction d = meCenter.getDirection(pCenter);
		character.setFaceDirection(d);
	}

}
