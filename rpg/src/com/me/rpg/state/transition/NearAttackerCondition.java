package com.me.rpg.state.transition;

import com.me.rpg.characters.GameCharacter;
import com.me.rpg.utils.Coordinate;

public class NearAttackerCondition implements Condition {

	private static final long serialVersionUID = 3304232059378493018L;

	private GameCharacter character;
	private float distance;
	
	public NearAttackerCondition(GameCharacter character, float distance) {
		this.character = character;
		this.distance = distance;
	}
	
	@Override
	public boolean test() {
		GameCharacter target = character.getRememberedAttacker();
		if (target == null) return false;
		
		Coordinate tarCen = target.getCenter();
		Coordinate meCen = character.getCenter();
		float dist2 = tarCen.distanceSquared(meCen);
		
		return dist2 < distance*distance;
	}

}
