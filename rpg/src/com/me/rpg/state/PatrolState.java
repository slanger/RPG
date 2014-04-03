package com.me.rpg.state;

import java.util.ArrayList;

import com.me.rpg.characters.GameCharacter;
import com.me.rpg.state.action.Action;
import com.me.rpg.state.action.WalkAction;
import com.me.rpg.utils.Coordinate;

public class PatrolState extends State {

	private static final long serialVersionUID = -127716873166546219L;

	private ArrayList<Action> actions = new ArrayList<Action>();
	private WalkAction patrolLoc;
	private Coordinate[] patrol;
	private int idx = 0;
	
	public PatrolState(HierarchicalState parent, GameCharacter character, Coordinate[] patrol) {
		super(parent, character);
		if (patrol == null || patrol.length == 0)
			throw new NullPointerException("Can't pass in a null or empty patrol list.");
		for (int i = 0; i < patrol.length; ++i) {
			if (patrol[i] == null)
				throw new NullPointerException("Can't have a null location for patrol list: " + i + " " + patrol);
		}
		this.patrol = new Coordinate[patrol.length];
		System.arraycopy(patrol, 0, this.patrol, 0, patrol.length);
		patrolLoc = new WalkAction(character, patrol[0]);
		actions.add(patrolLoc);
	}
	
	@Override
	public ArrayList<Action> doGetEntryActions() {
		// find closest patrol location
		float min = Float.MAX_VALUE;
		Coordinate center = character.getCenter();
		for (int i = 0; i < patrol.length; ++i) {
			float dist2 = center.distance2(patrol[i]);
			if (dist2 < min) {
				min = dist2;
				idx = i;
				// TODO this is not ideal
				actions.remove(patrolLoc);
				patrolLoc = new WalkAction(character, patrol[idx]);
			}
		}
		return super.doGetEntryActions();
	}
	
	@Override
	public ArrayList<Action> doGetActions() {
		if (character.isCenterNear(patrol[idx])) {
			++idx;
			idx %= patrol.length;
			// TODO this is not ideal
			actions.remove(patrolLoc);
			patrolLoc = new WalkAction(character, patrol[idx]);
		}
		return actions;
	}
}
