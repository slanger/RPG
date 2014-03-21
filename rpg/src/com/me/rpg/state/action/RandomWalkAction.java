package com.me.rpg.state.action;

import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.ai.RandomWalkAI;
import com.me.rpg.characters.GameCharacter;

public class RandomWalkAction implements Action {
	
	private RandomWalkAI walkAI;
	private GameCharacter character;
	
	public RandomWalkAction(GameCharacter character, Rectangle walkingBounds) {
		this(character, 1.0f, 0.5f, walkingBounds);
	}
	
	public RandomWalkAction(GameCharacter character, float delaySeconds, float intervalSeconds, Rectangle walkingBounds ) {
		this.character = character;
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
		walkAI.update(delta, character.getCurrentMap());
	}

}
