package com.me.rpg.state;

import com.me.rpg.characters.GameCharacter;

public abstract class AtomicState extends State {
	
	public AtomicState(String goal, GameCharacter mainCharacter) {
		super(goal, mainCharacter);
	}
}
