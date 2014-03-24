package com.me.rpg.state.transition;

import java.io.Serializable;
import java.util.ArrayList;

import com.me.rpg.state.State;
import com.me.rpg.state.action.Action;

public class Transition implements Serializable
{

	private static final long serialVersionUID = -5132723529410827096L;

	private State targetState;
	private ArrayList<Action> actions;
	private Condition condition;
	
	public Transition(State targetState, Condition condition) {
		this(targetState, new ArrayList<Action>(), condition);
	}
	
	public Transition(State targetState, ArrayList<Action> actions, Condition condition) {
		this.targetState = targetState;
		this.actions = new ArrayList<Action>(actions);
		this.condition = condition;
	}
	
	public boolean isTriggered() {
		return condition.test();
	}
	
	public State getTargetState() {
		return targetState;
	}
	
	public ArrayList<Action> getActions() {
		return actions;
	}
}
