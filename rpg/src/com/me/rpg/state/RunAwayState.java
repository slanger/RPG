package com.me.rpg.state;

import java.util.ArrayList;

import com.me.rpg.characters.GameCharacter;
import com.me.rpg.state.action.Action;
import com.me.rpg.state.action.WalkAction;
import com.me.rpg.state.transition.FloatCondition;
import com.me.rpg.utils.Coordinate;

public class RunAwayState extends State {
	
	private float lastSeen = 0f;
	private WalkAction action;
	private ArrayList<Action> actions;
	
	public RunAwayState(HierarchicalState parent, GameCharacter character) {
		super(parent, character);
		action = new WalkAction(character, new Coordinate());
		actions = new ArrayList<Action>();
		actions.add(action);
	}
	
	public ArrayList<Action> doGetActions() {
		ArrayList<GameCharacter> people = character.getCurrentMap().canSeeCharacters(character, 200);
		if (people.size() == 0) {
			character.setMoving(false);
			return super.doGetActions();
		}
		lastSeen = 0f;
		GameCharacter near = people.get(0);
		Coordinate pCenter = near.getCenter();
		Coordinate meCenter = character.getCenter();
		meCenter.setX(2*meCenter.getX() - pCenter.getX());
		meCenter.setY(2*meCenter.getY() - pCenter.getY());
		action.setNewLoc(meCenter);
		return actions;
	}
	
	@Override
	protected void doUpdate(float delta) {
		lastSeen += delta;
	}
}
