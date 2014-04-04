package com.me.rpg.state;

import java.util.ArrayList;

import com.me.rpg.characters.GameCharacter;
import com.me.rpg.state.action.Action;
import com.me.rpg.state.action.AttackAction;
import com.me.rpg.state.action.KeepDistanceAction;
import com.me.rpg.state.action.MeleeMoveTowardAction;
import com.me.rpg.state.action.SetStrafeAction;
import com.me.rpg.state.transition.FloatCondition;
import com.me.rpg.state.transition.NearAttackerCondition;
import com.me.rpg.state.transition.Transition;
import com.me.rpg.utils.Comparison;

public class MeleeFightState extends ResettingHierarchicalState {
	
	public MeleeFightState(HierarchicalState parent, GameCharacter character) {
		super(parent, character);
		
		MFSConfident conf = new MFSConfident(this, character);
		setInitialState(conf);
	}
	
	private class MFSConfident extends ResettingHierarchicalState {

		public MFSConfident(HierarchicalState parent, GameCharacter character) {
			super(parent, character);
			
			MFSCanAttack canAtt = new MFSCanAttack(this, character);
			StandStillState standStill = new StandStillState(this, character, null);
			MFSCannotAttack cannotAtt = new MFSCannotAttack(this, character);
			
			FloatCondition waitToAttCondition = cannotAtt.getFloatCondition("timeInState", 1f, Comparison.GREATER);
			Transition toCanAtt = new Transition(canAtt, waitToAttCondition);
			cannotAtt.setTransitions(toCanAtt);
			
			FloatCondition waitToCannotCond = standStill.getFloatCondition("timeInState", 1f, Comparison.GREATER);
			Transition toCannotAtt = new Transition(cannotAtt, waitToCannotCond);
			standStill.setTransitions(toCannotAtt);
			
			ArrayList<Action> transActions = new ArrayList<Action>();
			transActions.add(new AttackAction(character));
			NearAttackerCondition nearAttackCond = new NearAttackerCondition(character, 35);
			Transition toStandStill = new Transition(cannotAtt, transActions, nearAttackCond);
			canAtt.setTransitions(toStandStill);
			
			setInitialState(cannotAtt);
		}
	}
	
	private class MFSCanAttack extends State {
		
		private ArrayList<Action> actions;
		
		public MFSCanAttack(HierarchicalState parent, GameCharacter character) {
			super(parent, character);
			actions = new ArrayList<Action>();
			actions.add(new MeleeMoveTowardAction(character));
		}
		
		public ArrayList<Action> doGetActions() {
			return actions;
		}
		
	}
	
	private class MFSCannotAttack extends State {
		
		private ArrayList<Action> entryActions;
		private ArrayList<Action> actions;
		private ArrayList<Action> exitActions;
		
		public MFSCannotAttack(HierarchicalState parent, GameCharacter character) {
			super(parent, character);
			actions = new ArrayList<Action>();
			actions.add(new KeepDistanceAction(character, 100));
			
			entryActions = new ArrayList<Action>();
			entryActions.add(new SetStrafeAction(character, true));
			
			exitActions = new ArrayList<Action>();
			exitActions.add(new SetStrafeAction(character, false));
		}
		
		public ArrayList<Action> doGetEntryActions() {
			return entryActions;
		}
		
		public ArrayList<Action> doGetActions() {
			return actions;
		}
		
		public ArrayList<Action> doGetExitActions() {
			return exitActions;
		}
	}
}
