package com.me.rpg.state;

import java.util.ArrayList;

import com.me.rpg.characters.GameCharacter;
import com.me.rpg.state.action.Action;
import com.me.rpg.state.action.AttackAction;
import com.me.rpg.state.action.RangedMoveTowardAction;
import com.me.rpg.state.action.SetStrafeAction;
import com.me.rpg.state.transition.AttackerInRangeCondition;
import com.me.rpg.state.transition.FloatCondition;
import com.me.rpg.state.transition.Transition;
import com.me.rpg.utils.Comparison;

public class RangedFightState extends ResettingHierarchicalState {

	
	private static final long serialVersionUID = 1L;

	public RangedFightState(HierarchicalState parent, GameCharacter character) {
		super(parent, character);
		
		RFSConfident conf = new RFSConfident(this, character);
		setInitialState(conf);
	}
	
	private class RFSConfident extends ResettingHierarchicalState {

		private static final long serialVersionUID = 8500121329510295040L;

		public RFSConfident(HierarchicalState parent, GameCharacter character) {
			super(parent, character);
			
			RFSCanAttack canAtt = new RFSCanAttack(this, character);
			StandStillState standStill = new StandStillState(this, character, null);
			RFSCannotAttack cannotAtt = new RFSCannotAttack(this, character);
			
			FloatCondition waitToAttCondition = cannotAtt.getFloatCondition("timeInState", 1f, Comparison.GREATER);
			Transition toCanAtt = new Transition(canAtt, waitToAttCondition);
			cannotAtt.setTransitions(toCanAtt);
			
			FloatCondition waitToCannotCond = standStill.getFloatCondition("timeInState", 0.3f, Comparison.GREATER);
			Transition toCannotAtt = new Transition(cannotAtt, waitToCannotCond);
			standStill.setTransitions(toCannotAtt);
			
			ArrayList<Action> transActions = new ArrayList<Action>();
			transActions.add(new AttackAction(character));
			AttackerInRangeCondition nearAttackCond = new AttackerInRangeCondition(character);
			Transition toStandStill = new Transition(cannotAtt, transActions, nearAttackCond);
			canAtt.setTransitions(toStandStill);
			
			setInitialState(cannotAtt);
		}
	}
	
	private class RFSCanAttack extends State {

		private static final long serialVersionUID = -9156404073207904722L;

		private ArrayList<Action> entryActions;
		private ArrayList<Action> actions;
		private ArrayList<Action> exitActions;
		
		public RFSCanAttack(HierarchicalState parent, GameCharacter character) {
			super(parent, character);
			actions = new ArrayList<Action>();
			actions.add(new RangedMoveTowardAction(character));
			
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
	
	private class RFSCannotAttack extends State {

		private static final long serialVersionUID = 8649088522931762992L;

		private ArrayList<Action> entryActions;
		private ArrayList<Action> actions;
		private ArrayList<Action> exitActions;
		
		public RFSCannotAttack(HierarchicalState parent, GameCharacter character) {
			super(parent, character);
			actions = new ArrayList<Action>();
			actions.add(new RangedMoveTowardAction(character));
			
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
