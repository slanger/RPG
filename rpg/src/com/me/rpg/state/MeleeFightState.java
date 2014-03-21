package com.me.rpg.state;

import java.util.ArrayList;

import com.me.rpg.characters.GameCharacter;
import com.me.rpg.state.action.Action;
import com.me.rpg.state.action.AttackMoveAction;

public class MeleeFightState extends State {
	
	private ArrayList<Action> actions;
	
	public MeleeFightState(HierarchicalState parent, GameCharacter character) {
		super(parent, character);
		
		Action a = new AttackMoveAction(character);
		actions = new ArrayList<Action>();
		actions.add(a);
	}
	
	protected ArrayList<Action> doGetActions() {
		return actions;
	}
}
