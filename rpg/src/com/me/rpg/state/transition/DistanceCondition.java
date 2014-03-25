package com.me.rpg.state.transition;

import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.characters.GameCharacter;
import com.me.rpg.maps.Map;
import com.me.rpg.utils.Coordinate;

/**
 * Checks the distance from a character to a location
 * This is a lazy implementation, only supports standing exactly on target
 * @author Alex
 *
 */
public class DistanceCondition implements Condition {
	
	private GameCharacter character;
	private Map targetMap;
	public Rectangle targetRect;
	
	public DistanceCondition(GameCharacter character, Map targetMap, Coordinate targetCoordinate) {
		this.character = character;
		this.targetMap = targetMap;
		this.targetRect = targetCoordinate.getSmallCenteredRectangle();
	}
	
	@Override
	public boolean test() {
		Map charMap = character.getCurrentMap();
		if (charMap != targetMap)
			return false;
		Coordinate cen = character.getCenter();
		return targetRect.contains(cen.getX(), cen.getY());
	}

}
