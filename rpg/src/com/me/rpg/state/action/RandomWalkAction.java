package com.me.rpg.state.action;

import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.ai.RandomWalkAI;
import com.me.rpg.characters.GameCharacter;

public class RandomWalkAction implements Action {

	private static final long serialVersionUID = 527979532282805294L;

	private RandomWalkAI walkAI;
	
	public RandomWalkAction(GameCharacter character, Rectangle walkingBounds) {
		this(character, 1.0f, 0.5f, walkingBounds);
	}
	
	public RandomWalkAction(GameCharacter character, float delaySeconds, float intervalSeconds, Rectangle walkingBounds ) {
		walkAI = new RandomWalkAI(character, delaySeconds, intervalSeconds, walkingBounds);
	}
	
	public void start() {
		walkAI.start();
	}
	
	public void stop() {
		System.out.printf("Called stop");
		walkAI.stop();
	}
	
	@Override
	public void doAction(float delta) {
		walkAI.update(delta);
	}

}
