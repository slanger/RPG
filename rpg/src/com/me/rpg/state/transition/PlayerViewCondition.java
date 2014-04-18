package com.me.rpg.state.transition;

import com.me.rpg.characters.GameCharacter;
import com.me.rpg.utils.Comparison;

public class PlayerViewCondition implements Condition {

	private static final long serialVersionUID = -6677647770958720307L;
	private String playerView;
	private String target;
	private Comparison type;
	
	public PlayerViewCondition(GameCharacter character, String target, Comparison type) {
		playerView = character.getViewOfPlayer();
		this.target = target;
		this.type = type;
	}
	
	
	@Override
	public boolean test() {
		switch (type) {
		case EQUALS:
			return playerView.equals(target);
		case NOTEQUALS:
			return !playerView.equals(target);
		default:
			break;
		}
		throw new RuntimeException("Transition error: " + type + " is not a valid comparison type.");
	}

}