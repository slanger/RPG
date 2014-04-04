package com.me.rpg.state;

import java.util.ArrayList;

import com.me.rpg.characters.GameCharacter;
import com.me.rpg.state.action.Action;
import com.me.rpg.state.action.AttackAction;
import com.me.rpg.state.action.KeepDistanceAction;
import com.me.rpg.state.action.MeleeMoveTowardAction;
import com.me.rpg.state.transition.FloatCondition;
import com.me.rpg.state.transition.NearAttackerCondition;
import com.me.rpg.state.transition.Transition;
import com.me.rpg.utils.Comparison;

public class MeleeFightState extends HierarchicalState {
	
	public MeleeFightState(HierarchicalState parent, GameCharacter character) {
		super(parent, character);
		
		MFSConfident conf = new MFSConfident(this, character);
		setInitialState(conf);
	}
	
	private class MFSConfident extends HierarchicalState {

		public MFSConfident(HierarchicalState parent, GameCharacter character) {
			super(parent, character);
			
			MFSCanAttack canAtt = new MFSCanAttack(this, character);
			MFSCannotAttack cannotAtt = new MFSCannotAttack(this, character);
			
			FloatCondition waitToAttackCond = cannotAtt.getFloatCondition("timeInState", 5f, Comparison.GREATER);
			Transition toCanAtt = new Transition(canAtt, waitToAttackCond);
			cannotAtt.setTransitions(toCanAtt);
			
			ArrayList<Action> transActions = new ArrayList<Action>();
			transActions.add(new AttackAction(character));
			NearAttackerCondition nearAttackCond = new NearAttackerCondition(character, 90);
			Transition toCannotAtt = new Transition(cannotAtt, transActions, nearAttackCond);
			canAtt.setTransitions(toCannotAtt);
			
			setInitialState(cannotAtt);
		}
		
	}
	
	private class MFSCanAttack extends State {
		
		private ArrayList<Action> entryActions;
		private ArrayList<Action> actions;
		
		public MFSCanAttack(HierarchicalState parent, GameCharacter character) {
			super(parent, character);
			actions = new ArrayList<Action>();
			actions.add(new MeleeMoveTowardAction(character));

			entryActions = new ArrayList<Action>();
			entryActions.add(new AttackAction(character));
		}
		
		public ArrayList<Action> doGetActions() {
			return actions;
		}
		
	}
	
	private class MFSCannotAttack extends State {
		
		private ArrayList<Action> actions;
		
		public MFSCannotAttack(HierarchicalState parent, GameCharacter character) {
			super(parent, character);
			actions = new ArrayList<Action>();
			actions.add(new KeepDistanceAction(character, 100));
		}
		
		public ArrayList<Action> doGetActions() {
			return actions;
		}
	}
}
