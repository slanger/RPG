package com.me.rpg.state.action;

import java.util.ArrayList;

import com.me.rpg.characters.GameCharacter;
import com.me.rpg.combat.Weapon;
import com.me.rpg.maps.Map;

public class AttackMoveAction implements Action {
	
	private GameCharacter character;
	
	public AttackMoveAction(GameCharacter character) {
		this.character = character;
	}
	
	@Override
	public void doAction(float delta) {
		GameCharacter main = character;
		Weapon weapon = main.getEquippedWeapon();
		//int range = weapon.getRange();
		Map map = main.getCurrentMap();
		ArrayList<GameCharacter> enemy = map.canSeeCharacters(main, 200);
		//enemyCount.setValue(enemy.size());
		if (enemy.size() == 0)
			return;
		GameCharacter target = enemy.get(0);
		main.basicMoveToward(target.getCenter(), delta);
	}

}
