package com.me.rpg.state.action;

import java.util.ArrayList;

import com.me.rpg.characters.GameCharacter;
import com.me.rpg.maps.Map;
import com.me.rpg.utils.Coordinate;

public class RememberNearestPersonAction implements Action {
	
	private GameCharacter character;
	private boolean hear;
	private boolean see;
	private boolean decisive;
	
	/**
	 * This will retrieve the lists from the map of who the character can see/hear
	 * It will then set the character's rememberedAttacker to the nearest person
	 * @param character GameCharacter who will have rememberedAttacker set
	 * @param hear Whether we consider nearby characters that we can hear
	 * @param see Whether we consider nearby characters that we can see
	 * @param decisive Whether, if we see no one, we record null, or not.
	 */
	public RememberNearestPersonAction(GameCharacter character, boolean hear, boolean see, boolean decisive) {
		this.character = character;
		this.hear = hear;
		this.see = see;
		this.decisive = decisive;
		if (!hear && !see) throw new RuntimeException("Must be able to see or hear.");
	}
	
	@Override
	public void doAction(float delta) {
		Map map = character.getCurrentMap();
		ArrayList<GameCharacter> visiblePeople = null;
		if (hear && see)
			visiblePeople = map.canSeeOrHearCharacters(character);
		else if (hear)
			visiblePeople = map.canHearCharacters(character);
		else
			visiblePeople = map.canSeeCharacters(character);
		
		if (visiblePeople.size() == 0) {
			if (decisive)
				character.resetRememberedAttacker();
			return;
		}
		
		Coordinate myCen = character.getCenter();
		
		GameCharacter nearby = visiblePeople.get(0);
		for (int i = 1; i < visiblePeople.size(); ++i) {
			GameCharacter other = visiblePeople.get(i);
			Coordinate nearbyCen = nearby.getCenter();
			Coordinate otherCen = other.getCenter();
			if (otherCen.distance2(myCen) < nearbyCen.distance2(myCen))
				nearby = other;
		}
		character.rememberTarget(nearby);
	}

}
