package com.me.rpg.state;

import java.util.ArrayList;

import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.characters.GameCharacter;
import com.me.rpg.state.action.Action;
import com.me.rpg.state.action.RandomWalkAction;

public class RandomWalkState extends State {

	private static final long serialVersionUID = -2185898702476495717L;

	private RandomWalkAction action;
	private ArrayList<Action> actions;
	
	public RandomWalkState(HierarchicalState parent, GameCharacter character, Rectangle walkingBounds) {
		this(parent, character, 1.0f, 1.0f, walkingBounds);
	}

	public RandomWalkState(HierarchicalState parent, GameCharacter character,
			float delaySeconds, float intervalSeconds, Rectangle walkingBounds) {
		super(parent, character);

		action = new RandomWalkAction(character, delaySeconds, intervalSeconds, walkingBounds);
		actions = new ArrayList<Action>();
		actions.add(action);
	}
	
	public ArrayList<Action> doGetEntryActions() {
		action.start();
		return super.doGetEntryActions();
	}
	
	public ArrayList<Action> doGetActions() {
		return actions;
	}
	
	public ArrayList<Action> doGetExitActions() {
		action.stop();
		return super.doGetExitActions();
	}
}
