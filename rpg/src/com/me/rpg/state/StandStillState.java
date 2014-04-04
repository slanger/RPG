package com.me.rpg.state;

import java.util.ArrayList;

import com.me.rpg.characters.GameCharacter;
import com.me.rpg.state.action.Action;
import com.me.rpg.state.action.FaceDirectionAction;
import com.me.rpg.utils.Direction;

public class StandStillState extends State {

	private static final long serialVersionUID = -9127125400944142907L;

	private ArrayList<Action> entryActions;
	
	public StandStillState(HierarchicalState parent, GameCharacter character, Direction faceDirection) {
		super(parent, character);
		Action a = new FaceDirectionAction(character, faceDirection);
		entryActions = new ArrayList<Action>();
		entryActions.add(a);
	}
	
	public ArrayList<Action> doGetEntryActions() {
		return entryActions;
	}
}
