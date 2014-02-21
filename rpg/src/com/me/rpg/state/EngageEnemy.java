package com.me.rpg.state;

import com.me.rpg.characters.GameCharacter;
import com.me.rpg.combat.Weapon;
import com.me.rpg.state.transition.IntTransition;
import com.me.rpg.utils.Comparison;
import com.me.rpg.utils.MutableInt;

public class EngageEnemy extends AtomicState {
	
	private MutableInt enemyCount;
	
	public EngageEnemy(GameCharacter mainCharacter) {
		super("Engaging enemy.", mainCharacter);
		
		enemyCount = new MutableInt();
	}
	
	
	@Override
	public void doUpdateBeforeTransition(float deltaTime) {
		GameCharacter main = getMainCharacter();
		Weapon weapon = main.getEquippedWeapon();
		int range = weapon.getRange();
		
		
	}
	
	@Override
	public IntTransition getIntTransition(String key, int target, Comparison type) {
		if (key.equals("enemyCount")) {
			return new IntTransition(enemyCount, target, type);
		}
		return super.getIntTransition(key, target, type);
	}
}
