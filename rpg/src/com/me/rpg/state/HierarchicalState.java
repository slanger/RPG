package com.me.rpg.state;

import java.util.ArrayList;

import com.me.rpg.characters.GameCharacter;
import com.me.rpg.state.action.Action;
import com.me.rpg.state.transition.Transition;

public class HierarchicalState extends State {

	private static final long serialVersionUID = -185496761908429304L;

	private ArrayList<State> children;
	private State initialState;
	private State currentState;
	
	public HierarchicalState(HierarchicalState parent, GameCharacter character) {
		super(parent, character);
		children = new ArrayList<State>();
	}
	
	public void setInitialState(State initialState) {
		if (initialState == null)
			throw new NullPointerException("Can't set the initialState to be null.");
		if (!children.contains(initialState))
			throw new RuntimeException("Can't set the initialState if it isn't a child.");
		if (this.initialState != null)
			throw new RuntimeException("Can't set the initialState more than once.");
		
		this.initialState = initialState;
	}
	
	/**
	 * Gets the current state stack
	 */
	public ArrayList<State> getStates() {
		ArrayList<State> currentStack = new ArrayList<State>();
		currentStack.add(this);
		if (currentState != null) {
			currentStack.addAll(currentState.getStates());
		}
		return currentStack;
	}
	
	public UpdateResult update(float delta) {
		UpdateResult result = new UpdateResult();
		// If we aren't in a state, use the initial state.
		if (currentState == null) {
			currentState = initialState;
			result.actions = currentState.getEntryActions();
			return result;
		}
		
		// Try to find a transition in the current state
		Transition triggeredTransition = null;
		Transition[] transitions = currentState.getTransitions();
		for (Transition t: transitions) {
			if (t.isTriggered()) {
				triggeredTransition = t;
				break;
			}
		}
		
		// If we've found one, make a result structure for it
		if (triggeredTransition != null) {
			result.actions = new ArrayList<Action>();
			result.transition = triggeredTransition;
		} else { // Otherwise, recurse down for a result
			result = currentState.update(delta);
		}
		
		// Check if the result contains a transition
		if (result.transition != null) {
			// Act based on its level
			if (this == result.transition.getTargetState().parent) {
				// It is on our level: honor it
				State targetState = result.transition.getTargetState();
				result.actions.addAll(currentState.getExitActions());
				result.actions.addAll(result.transition.getActions());
				result.actions.addAll(targetState.getEntryActions());
				
				// Set our current state
				currentState = targetState;
				
				// Add our normal action (we may be a state)
				result.actions.addAll(getActions(delta));
				
				// Clear the transition, so nobody else does it
				result.transition = null;
			} else if (result.transition.getTargetState().getParentStates().contains(this)) {
				// It needs to be passed down
				State targetState = result.transition.getTargetState();
				HierarchicalState targetParent = targetState.parent;
				result.actions.addAll(result.transition.getActions());
				result.actions.addAll(targetParent.updateDown(targetState, this));
				
				// Clear the transition, so nobody else uses it
				result.transition = null;
			} else {
				// It is destined for a higher level
				// Exit our current state
				result.actions.addAll(currentState.getExitActions());
				currentState = null;
			}
		} else {
			// Else we didn't get a transition
			// Simply do our normal actions
			result.actions.addAll(getActions(delta));
		}
		
		return result;
	}
	
	public ArrayList<Action> updateDown(State child, State upperLimit) {
		ArrayList<Action> actions = null;
		// If we're not at top level, continue recursing
		if (this != upperLimit) {
			// Pass ourself as the transition state to our parent
			actions = parent.updateDown(this, upperLimit);
		} else { // otherwise, we have no actions to add to
			actions = new ArrayList<Action>();
		}
		
		// If we have a current state, exit it
		if (currentState != null)
			actions.addAll(currentState.getExitActions());
		
		// Move to the new state, and return all the actions
		currentState = child;
		actions.addAll(child.getEntryActions());
		return actions;
	}

	public void addChild(State state) {
		children.add(state);
	}
	
	protected ArrayList<State> getChildren() {
		return children;
	}
	
	public String toString() {
		StringBuilder part = new StringBuilder();
		for (int i = 0; i < children.size(); ++i) {
			if (i != 0)
				part.append(" | ");
			part.append(children.get(i).toString());
		}
		return String.format("%s:{%s}", super.toString(), part.toString());
	}
}
