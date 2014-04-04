package com.me.rpg.state;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.me.rpg.characters.GameCharacter;
import com.me.rpg.state.action.Action;

public class MeleeFightState extends HierarchicalState {
	
	private ArrayList<Action> actions;
	
	public MeleeFightState(HierarchicalState parent, GameCharacter character) {
		super(parent, character);
		
		MFSConfident conf = new MFSConfident(this, character);
		setInitialState(conf);
	}
	
	protected ArrayList<Action> doGetActions() {
		return actions;
	}
	
	private class MFSConfident extends HierarchicalState {

		public MFSConfident(HierarchicalState parent, GameCharacter character) {
			super(parent, character);
			
			MFSCanAttack canAtt = new MFSCanAttack(this, character);
		}
		
	}
	
	private class MFSCanAttack extends State {
		
		private ArrayList<Actions> actions;
		
		public MFSCanAttack(HierarchicalState parent, GameCharacter character) {
			super(parent, character);
			actions = new ArrayList<Actions>();
			actions.add(new MeleeMoveTowardAction());
		}
		
	}
}
