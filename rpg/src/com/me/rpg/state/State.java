package com.me.rpg.state;

import java.util.ArrayList;

import com.me.rpg.characters.GameCharacter;
import com.me.rpg.state.action.Action;
import com.me.rpg.state.transition.BooleanCondition;
import com.me.rpg.state.transition.FloatCondition;
import com.me.rpg.state.transition.IntCondition;
import com.me.rpg.state.transition.Transition;
import com.me.rpg.utils.Comparison;
import com.me.rpg.utils.MutableFloat;

public abstract class State {
	
	protected HierarchicalState parent;
	protected Transition[] transitions;
	protected boolean setTransitions;
	protected GameCharacter character;
	
	private ArrayList<Action> emptyActions;
	private MutableFloat timeInState;
	private String stateName;
	private ArrayList<State> parentStates;
	
	public State(HierarchicalState parent, GameCharacter character) {
		this.parent = parent;
		this.character = character;
		transitions = null;
		emptyActions = new ArrayList<Action>();
		timeInState = new MutableFloat(0f);
		transitions = new Transition[0];
		setTransitions = false;
		
		stateName = this.getClass().getCanonicalName();
		parentStates = null;
		if (parent != null) {
			parent.addChild(this);
		}
	}
	
	public void setName(String newName) {
		stateName = newName;
	}
	
	public ArrayList<State> getParentStates() {
		if (parentStates != null)
			return parentStates;
		ArrayList<State> ret = new ArrayList<State>();
		if (parent != null) {
			ret.addAll(parent.getParentStates());
		}
		ret.add(this);
		parentStates = ret;
		return parentStates;
	}
	
	/**
	 * Gets the current state stack
	 * @return This state has no children - it is a leaf.
	 */
	public ArrayList<State> getStates() {
		ArrayList<State> list = new ArrayList<State>();
		list.add(this);
		return list;
	}
	
	public final ArrayList<Action> getActions(float delta) {
		timeInState.incrementValue(delta);
		return doGetActions();
	}
	
	public final ArrayList<Action> getEntryActions() {
		return doGetEntryActions();
	}
	
	public final ArrayList<Action> getExitActions() {
		timeInState.setValue(0f);
		return doGetExitActions();
	}
	
	protected ArrayList<Action> doGetEntryActions() {
		return emptyActions;
	}
	
	protected ArrayList<Action> doGetActions() {
		return emptyActions;
	}
	
	protected ArrayList<Action> doGetExitActions() {
		return emptyActions;
	}
	
	public void setTransitions(Transition ... transitions) {
		if (setTransitions)
			throw new RuntimeException("Cannot set the transitions more than once.");
		if (transitions == null)
			throw new NullPointerException("Cannot set the transition array to null.");
		for (int i = 0; i < transitions.length; ++i) {
			if (transitions[i] == null)
				throw new NullPointerException("Cannot have a null transition: " + i + " " + transitions);
		}
		this.transitions = new Transition[transitions.length];
		setTransitions = true;
		System.arraycopy(transitions, 0, this.transitions, 0, transitions.length);
	}
	
	public Transition[] getTransitions() {
		return transitions;
	}
	
	public UpdateResult update(float delta) {
		UpdateResult result = new UpdateResult();
		result.actions = getActions(delta);
		doUpdate(delta);
		return result;
	}
	
	// Can override in subclasses for delta details
	protected void doUpdate(float delta) {}
	
	public BooleanCondition getBooleanCondition(String key, boolean target) {
		throw new UnsupportedOperationException("getBooleanCondition function not supported.");
	}
	
	public IntCondition getIntCondition(String key, int target, Comparison type) {
		throw new UnsupportedOperationException("getIntCondition function not supported.");
	}
	
	public FloatCondition getFloatCondition(String key, float target, Comparison type) {
		if (key.equals("timeInState")) {
			return new FloatCondition(timeInState, target, type);
		}
		throw new RuntimeException("Attempt to get FloatCondition from improper key: " + key);
	}
	
	public String toString() {
		return stateName;
	}
}
