package com.me.rpg.state.action;

import com.me.rpg.characters.GameCharacter;
import com.me.rpg.utils.Coordinate;
import com.me.rpg.utils.Direction;

public class KeepDistanceAction implements Action {

	private static final long serialVersionUID = -7032150212667126445L;

	private GameCharacter character;
	private float distance;
	
	public KeepDistanceAction(GameCharacter character, float distance) {
		this.character = character;
		this.distance = distance;
	}
	
	@Override
	public void doAction(float delta) {
		GameCharacter target = character.getRememberedAttacker();
		Coordinate pCenter = target.getCenter();
		Coordinate meCenter = character.getCenter();
		float dist = meCenter.distanceSquared(pCenter);
		meCenter.setX(2*meCenter.getX() - pCenter.getX());
		meCenter.setY(2*meCenter.getY() - pCenter.getY());
		Coordinate targetLoc = null;
		if (dist > distance*distance+20) {
			targetLoc = pCenter;
		} else if (dist < distance*distance-20) {
			targetLoc = meCenter;
		} else {
			return;
		}
		WalkAction action = new WalkAction(character, targetLoc, character.getCurrentMap());
		action.doAction(delta);
		Direction d = meCenter.getDirection(pCenter);
		character.setFaceDirection(d);
	}

}
