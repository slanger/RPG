package com.me.rpg;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.me.rpg.ai.StandStillAI;
import com.me.rpg.ai.WalkAI;
import com.me.rpg.combat.IAttackable;
import com.me.rpg.combat.Projectile;
import com.me.rpg.combat.Shield;
import com.me.rpg.combat.StatusEffect;
import com.me.rpg.combat.Weapon;
import com.me.rpg.maps.Map;

public abstract class Character implements IAttackable
{
	private static final int MAX_HEALTH = 100;

	private String name;
	private Sprite sprite;
	private Coordinate location;
	private TextureRegion rightIdle, leftIdle, upIdle, downIdle;
	private Animation rightWalkAnimation, leftWalkAnimation, upWalkAnimation,
			downWalkAnimation;
	private Direction direction = Direction.DOWN;
	private boolean moving = false;
	private float stateTime = 0f;
	private float speed = 100f;

	protected WalkAI walkAI;
	protected Map currentMap = null;
	
	// Combat stuff
	protected Weapon weaponSlot;
	protected Weapon weaponSlotExtra;
	protected Shield shieldSlot;
	protected LinkedList<StatusEffect> inflictedEffects;
	protected HashMap<StatusEffect, Float> immunityHash;
	protected int health;
	protected float strikeImmunity;
	

	protected Character(String name, Texture spritesheet, int width,
			int height, int tileWidth, int tileHeight, float animationDuration)
	{
		this.name = name;
		TextureRegion[][] sheet = TextureRegion.split(spritesheet, tileWidth,
				tileHeight);
		int columns = sheet[0].length;
		TextureRegion[] rightWalkFrames = new TextureRegion[columns];
		TextureRegion[] leftWalkFrames = new TextureRegion[columns];
		TextureRegion[] upWalkFrames = new TextureRegion[columns];
		TextureRegion[] downWalkFrames = new TextureRegion[columns];
		for (int i = 0; i < columns; i++)
		{
			rightWalkFrames[i] = sheet[Direction.RIGHT.getIndex()][i];
			leftWalkFrames[i] = sheet[Direction.LEFT.getIndex()][i];
			upWalkFrames[i] = sheet[Direction.UP.getIndex()][i];
			downWalkFrames[i] = sheet[Direction.DOWN.getIndex()][i];
		}
		rightWalkAnimation = new Animation(animationDuration, rightWalkFrames);
		leftWalkAnimation = new Animation(animationDuration, leftWalkFrames);
		upWalkAnimation = new Animation(animationDuration, upWalkFrames);
		downWalkAnimation = new Animation(animationDuration, downWalkFrames);
		rightIdle = sheet[Direction.RIGHT.getIndex()][0];
		leftIdle = sheet[Direction.LEFT.getIndex()][0];
		upIdle = sheet[Direction.UP.getIndex()][0];
		downIdle = sheet[Direction.DOWN.getIndex()][0];
		// start sprite facing downward
		sprite = new Sprite(downIdle, 0, 0, width, height);
		sprite.setRegion(downIdle);
		// default walk AI
		walkAI = new StandStillAI();
		
		inflictedEffects = new LinkedList<StatusEffect>();
		immunityHash = new HashMap<StatusEffect, Float>();
		health = getMaxHealth();
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Coordinate getLocation()
	{
		return location;
	}

	public void setLocation(Coordinate location)
	{
		this.location = location;
	}

	public float getX()
	{
		return location.getX();
	}

	public float getY()
	{
		return location.getY();
	}

	public Direction getDirection()
	{
		return direction;
	}

	public void setDirection(Direction direction)
	{
		this.direction = direction;
	}

	public boolean isMoving()
	{
		return moving;
	}

	public void setMoving(boolean moving)
	{
		this.moving = moving;
	}

	protected float getStateTime()
	{
		return stateTime;
	}

	protected void addToStateTime(float deltaTime)
	{
		this.stateTime += deltaTime;
		this.strikeImmunity -= deltaTime;
	}

	public Sprite getSprite()
	{
		return sprite;
	}

	public Rectangle getBoundingRectangle()
	{
		return sprite.getBoundingRectangle();
	}

	protected TextureRegion getRightIdle()
	{
		return rightIdle;
	}

	protected TextureRegion getLeftIdle()
	{
		return leftIdle;
	}

	protected TextureRegion getUpIdle()
	{
		return upIdle;
	}

	protected TextureRegion getDownIdle()
	{
		return downIdle;
	}

	protected Animation getRightWalkAnimation()
	{
		return rightWalkAnimation;
	}

	protected Animation getLeftWalkAnimation()
	{
		return leftWalkAnimation;
	}

	protected Animation getUpWalkAnimation()
	{
		return upWalkAnimation;
	}

	protected Animation getDownWalkAnimation()
	{
		return downWalkAnimation;
	}

	public float getSpeed()
	{
		return speed;
	}

	public void setSpeed(float newSpeed)
	{
		this.speed = newSpeed;
	}

	public float getSpriteWidth()
	{
		return sprite.getWidth();
	}

	public float getSpriteHeight()
	{
		return sprite.getHeight();
	}

	public void setPosition(float x, float y)
	{
		sprite.setPosition(x, y);
	}

	public WalkAI getWalkAI()
	{
		return walkAI;
	}

	public void setWalkAI(WalkAI walkAI)
	{
		this.walkAI = walkAI;
	}

	public Map getCurrentMap()
	{
		return currentMap;
	}

	public void setCurrentMap(Map currentMap)
	{
		this.currentMap = currentMap;
	}

	public void render(SpriteBatch batch)
	{
		doRenderBefore();
		if (strikeImmunity > 0) {
			Color c = sprite.getColor();
			sprite.setColor(c.r, c.g, c.b, (float)Math.abs(Math.cos(strikeImmunity*16)));
		}
		sprite.draw(batch);
		if (weaponSlot != null)
		{
			weaponSlot.render(sprite.getBoundingRectangle(), getDirection(), batch);
		}
		doRenderAfter();
	}
	
	protected void doRenderBefore() { }
	protected void doRenderAfter()  { }
	
	public final void update(float deltaTime, Map currentMap) {
		addToStateTime(deltaTime);
		Iterator<StatusEffect> iter = inflictedEffects.iterator();
		while (iter.hasNext()) {
			StatusEffect effect = iter.next();
			effect.applyStatusEffect(deltaTime, this);
			if (effect.hasWornOff()) {
				iter.remove();
			}
		}
		if (weaponSlot != null)
			weaponSlot.update(deltaTime);
		doUpdate(deltaTime, currentMap);
	}
	
	protected abstract void doUpdate(float deltaTime, Map currentMap);

	protected void updateTexture()
	{
		TextureRegion currentFrame = null;
		switch (getDirection())
		{
		case RIGHT:
			currentFrame = isMoving() ? getRightWalkAnimation().getKeyFrame(
					getStateTime(), true) : getRightIdle();
					break;
		case LEFT:
			currentFrame = isMoving() ? getLeftWalkAnimation().getKeyFrame(
					getStateTime(), true) : getLeftIdle();
					break;
		case UP:
			currentFrame = isMoving() ? getUpWalkAnimation().getKeyFrame(
					getStateTime(), true) : getUpIdle();
					break;
		case DOWN:
			currentFrame = isMoving() ? getDownWalkAnimation().getKeyFrame(
					getStateTime(), true) : getDownIdle();
					break;
		}

		if (currentFrame != null)
		{
			sprite.setRegion(currentFrame);
		}
	}

	public void addedToMap(Map map)
	{
		currentMap = map;
		walkAI.start();
	}

	public void removedFromMap(Map map)
	{
		walkAI.stop();
		currentMap = null;
	}

	public abstract void acceptGoodAction(Character characterDoingAction);
	
	@Override
	public void receiveAttack(Weapon weapon) {
		if (strikeImmunity > 0)
			return;
		
		boolean result = attemptShieldBlock(weapon);
		if (result)
			return;
		inflictEffects(weapon.getEffects());
		receiveDamage(weapon.getPower());
		strikeImmunity = 1.0f;
	}
	
	@Override
	public void receiveAttack(Projectile projectile) {
		if (strikeImmunity > 0)
			return;
		
		boolean result = attemptShieldBlock(projectile);
		if (result)
			return;
		inflictEffects(projectile.getEffects());
		receiveDamage(projectile.getPower());
		strikeImmunity = 1.0f;
	}
	
	@Override
	public void receiveDamage(int damage) {
		damage = Math.max(damage, 0);
		int health = getHealth();
		damage = Math.min(damage, health);
		health -= damage;
		System.err.printf("%s takes %d damage. %d left.\n", getName(), damage, health);
		setHealth(health);
	}
	
	private void inflictEffects(StatusEffect[] effects) {
		for(StatusEffect effect: effects) {
			if(!isImmune(effect)) {
				inflictEffect(effect);
			}
		}
	}
	
	private void inflictEffect(StatusEffect effect) {
		immunityHash.put(effect.getParentRef(), stateTime + effect.getImmunePeriod());
		inflictedEffects.add(effect);
		System.out.printf("effect added: %s, immunityHash size =%d\n", effect.getEffectName(), immunityHash.size());
	}
	
	private boolean isImmune(StatusEffect effect) {
		Float immunityEnd = immunityHash.get(effect.getParentRef());
		return immunityEnd != null && stateTime < immunityEnd;
	}
	
	public int getHealth() {
		return health;
	}
	
	public int getMaxHealth() {
		return MAX_HEALTH;
	}
	
	protected void setHealth(int health) {
		health = Math.max(health, 0);
		health = Math.min(getMaxHealth(), health);
		this.health = health;
		doHealthCheck();
	}
	
	protected void doHealthCheck() {
		if (getHealth() == 0)
			System.err.printf("Yae, %s is dead.\n", getName());
	}
	
	private boolean attemptShieldBlock(Weapon weapon) {
		if (shieldSlot != null && weapon.getLastDirection().equals(direction.opposite())) {
			// only allow shield to block if you are facing the attack
			shieldSlot.receiveAttack(weapon);
			return true;
		}
		return false;
	}
	
	private boolean attemptShieldBlock(Projectile projectile) {
		if (shieldSlot != null && projectile.getFiredDirection().equals(direction.opposite())) {
			shieldSlot.receiveAttack(projectile);
			return true;
		}
		return false;
	}

	public abstract void doneFollowingPath();

	/**
	 * May be useful with debugging. Likely will be out of date if Character
	 * class is updated.
	 */
	@Override
	public String toString()
	{
		return String
				.format("(CharacterToString){name:%s, sprite:%s, direction:%s, moving:%s, stateTime:%lf}",
						name, sprite, direction, moving, stateTime);
	}

	/**
	 * Likely insufficient implementation of equip
	 * @param sword
	 */
	public boolean equip(Map m, Weapon weapon)
	{
		if (weaponSlot != null)
		{
			m.removeEquippedWeapon(weaponSlot);
			weaponSlot.unequip();
			weaponSlot = null;
		}
		
		boolean result = weapon.tryEquip(this);
		if (!result)
		{
			return result;
		}

		this.weaponSlot = weapon;
		m.addEquippedWeapon(weapon);
		return result;
	}
	
	public void swapWeapon(Map m) {
		m.removeEquippedWeapon(weaponSlot);
		weaponSlot.unequip();
		
		Weapon temp = weaponSlot;
		weaponSlot = weaponSlotExtra;
		weaponSlotExtra = temp;
		if (weaponSlot == null)
			return;
		weaponSlot.tryEquip(this);
		m.addEquippedWeapon(weaponSlot);
	}
	
	public Rectangle getHitBox() {
		Rectangle r = sprite.getBoundingRectangle();
		Vector2 center = new Vector2();
		r.getCenter(center);
		r.setWidth(r.getWidth()-8);
		r.setHeight(r.getHeight()-8);
		r.setCenter(center);
		return r;
	}
	
	public boolean equipShield(Shield s) {
		if (shieldSlot != null) {
			shieldSlot.unequip();
			shieldSlot = null;
		}
		
		boolean result = s.tryEquip(this);
		if (!result)
		{
			return result;
		}
		
		shieldSlot = s;
		return result;
	}
	
	public void unequipShield() {
		shieldSlot.unequip();
	}

}
