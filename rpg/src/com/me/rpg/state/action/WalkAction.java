package com.me.rpg.state.action;

import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.ai.FollowPathAI;
import com.me.rpg.characters.GameCharacter;
import com.me.rpg.utils.Coordinate;

public class WalkAction implements Action {
	
	private GameCharacter character;
	private Rectangle targetLocation;
	private FollowPathAI walkAI;
	
	public WalkAction(GameCharacter character, Coordinate targetLocation) {
		this.character = character;
		this.targetLocation = targetLocation.getCenteredRectangle(2*Coordinate.EPS, 2*Coordinate.EPS);
		walkAI = new FollowPathAI(character, this.targetLocation);
	}
	
	@Override
	public void doAction(float delta) {
		// only can walk to one location
		walkAI.reset();
		walkAI.update(delta, character.getCurrentMap());
	}
	
	public void setNewLoc(Coordinate newTargetLocation) {
		targetLocation = newTargetLocation.getCenteredRectangle(2*Coordinate.EPS, 2*Coordinate.EPS);
		walkAI.setNewPath(targetLocation);
	}

}
