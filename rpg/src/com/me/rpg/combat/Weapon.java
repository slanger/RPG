package com.me.rpg.combat;

public abstract class Weapon {
	
	protected float speed;		// how long it takes for the weapon to reach the end of its stroke, in seconds
	protected float fireRate; 	// how long the user must wait after attacking to attack or switch weapons, in seconds 
	protected int range; 		// how far the weapon can reach, in pixels?
	protected int power; 		// how much damage the weapon does, 1:1 with health
}
