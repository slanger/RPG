package com.me.rpg.state;

import java.util.ArrayList;

import com.me.rpg.characters.GameCharacter;
import com.me.rpg.state.transition.Transition;

public class ResettingHierarchicalState extends HierarchicalState {

	public ResettingHierarchicalState(HierarchicalState parent,
			GameCharacter character) {
		super(parent, character);
	}
	
	public void setTransitions(Transition ... transitions) {
		addTransitions(transitions);
	}
	
	public void addTransitions(Transition ... transitions) {
		ArrayList<State> children = getChildren();
		for (int i = 0; i < children.size(); ++i) {
			children.get(i).addTransitions(transitions);
		}
	}

}
