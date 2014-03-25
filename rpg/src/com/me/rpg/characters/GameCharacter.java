package com.me.rpg.characters;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.me.rpg.ScreenHandler;
import com.me.rpg.World;
import com.me.rpg.combat.IAttackable;
import com.me.rpg.combat.Projectile;
import com.me.rpg.combat.Shield;
import com.me.rpg.combat.StatusEffect;
import com.me.rpg.combat.Weapon;
import com.me.rpg.maps.Map;
import com.me.rpg.maps.MapType;
import com.me.rpg.reputation.NPCMemory;
import com.me.rpg.utils.Coordinate;
import com.me.rpg.utils.Direction;

public abstract class GameCharacter implements IAttackable, Serializable
{

	private static final long serialVersionUID = -5812091877699740886L;

	private static final int MAX_HEALTH = 100;

	private String name;
	private String spritesheetPath;
	private int width, height;
	private int tileWidth, tileHeight;
	private float animationDuration;
	private Coordinate bottomLeftCorner;
	private Direction moveDirection = Direction.DOWN;
	private Direction faceDirection = Direction.DOWN;
	private boolean moving = false;
	private float stateTime = 0f;
	private float baseSpeed = 100f;
	private float speedModifier = 1.0f;

	private transient Sprite sprite;
	private transient TextureRegion rightIdle, leftIdle, upIdle, downIdle;
	private transient Animation rightWalkAnimation, leftWalkAnimation, upWalkAnimation,
			downWalkAnimation;

	protected Map currentMap = null;
	protected final World world;

	// Combat stuff
	protected Weapon weaponSlot;
	protected Weapon swapWeaponSlot;
	protected Shield shieldSlot;
	protected boolean shielding;
	protected LinkedList<StatusEffect> inflictedEffects;
	protected HashMap<StatusEffect, Float> immunityHash;
	protected int health;
	protected int maxHealth;
	protected float strikeImmunity;
	protected boolean strafing;

	// Reputation Stuff
	protected NPCMemory npcMemory;
	protected float sightDistance;
	protected float hearingRadius;

	protected GameCharacter(String name, String spritesheetPath, int width,
			int height, int tileWidth, int tileHeight, float animationDuration,
			World world)
	{
		this.name = name;
		this.spritesheetPath = spritesheetPath;
		this.width = width;
		this.height = height;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.animationDuration = animationDuration;
		this.world = world;

		inflictedEffects = new LinkedList<StatusEffect>();
		immunityHash = new HashMap<StatusEffect, Float>();
		maxHealth = 100;
		health = getMaxHealth();

		npcMemory = new NPCMemory(world.getReputationSystem()
				.getMasterEventList());
		sightDistance = 225.0f;
		hearingRadius = 125.0f;
		create();
	}

	private void create()
	{
		Texture spritesheet = ScreenHandler.manager.get(spritesheetPath, Texture.class);
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
	}

	private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException
	{
		inputStream.defaultReadObject();
		create();
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Coordinate getBottomLeftCorner()
	{
		return bottomLeftCorner;
	}

	public float getBottomLeftX()
	{
		return bottomLeftCorner.getX();
	}

	public float getBottomLeftY()
	{
		return bottomLeftCorner.getY();
	}

	public void setBottomLeftCorner(Coordinate bottomLeftCorner)
	{
		this.bottomLeftCorner = bottomLeftCorner;
	}

	public Coordinate getCenter()
	{
		return new Coordinate(bottomLeftCorner.getX() + getSpriteWidth() / 2,
				bottomLeftCorner.getY() + getSpriteHeight() / 2);
	}

	public float getCenterX()
	{
		return getCenter().getX();
	}

	public float getCenterY()
	{
		return getCenter().getY();
	}

	public void setCenter(Coordinate center)
	{
		this.bottomLeftCorner = new Coordinate(center.getX() - getSpriteWidth()
				/ 2, center.getY() - getSpriteHeight() / 2);
	}
	
	public boolean isCenterNear(Coordinate target) {
		Coordinate c = getCenter();
		return c.isNear(target);
	}
	
	public boolean isNear(Coordinate bottomLeftTarget) {
		return bottomLeftCorner.isNear(bottomLeftTarget);
	}

	public Direction getMoveDirection()
	{
		return moveDirection;
	}

	public void setMoveDirection(Direction moveDirection)
	{
		this.moveDirection = moveDirection;
		if (!strafing)
			setFaceDirection(moveDirection);
	}

	public boolean isStrafing()
	{
		return strafing;
	}
	
	private void setStrafing(boolean strafing) {
		this.strafing = strafing;
	}
	
	public boolean isUsingShield() {
		return shielding;
	}
	
	public void usingShield(boolean shielding) {
		if (shieldSlot == null)
			throw new RuntimeException("There's no shield equipped!");
		this.shielding = shielding;
		setStrafing(shielding);
	}
	
	public boolean isShielded() {
		return shieldSlot != null && shielding && !isAttacking();
	}
	
	public Direction getFaceDirection() {
		return faceDirection;
	}

	public void setFaceDirection(Direction faceDirection)
	{
		this.faceDirection = faceDirection;
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
		if (!strafing)
			return baseSpeed * speedModifier;
		return baseSpeed * 0.6f * speedModifier;
	}
	
	public float getSpeedModifier() {
		return speedModifier;
	}
	
	public void setSpeedModifier(float modifier) {
		if (modifier < 0)
			throw new RuntimeException("Can't have a negative speed modifier: " + modifier);
		speedModifier = modifier;
	}

	public void setBaseSpeed(float newSpeed)
	{
		this.baseSpeed = newSpeed;
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

	public Map getCurrentMap()
	{
		return currentMap;
	}

	public void setCurrentMap(Map currentMap)
	{
		this.currentMap = currentMap;
	}
	
	public Weapon getEquippedWeapon() {
		return weaponSlot;
	}
	
	public boolean isAttacking() {
		return weaponSlot == null ? false : weaponSlot.isAttacking();
	}

	public World getWorld()
	{
		return world;
	}

	public abstract void moveToOtherMap(MapType mapType, Coordinate newLocation);

	public abstract void warpToOtherMap(MapType mapType, Coordinate newLocation);

	public void render(SpriteBatch batch)
	{
		doRenderBefore(batch);

		// blink if Character has been hit
		if (strikeImmunity > 0)
		{
			Color c = sprite.getColor();
			// 16 is a good value for blinking rate
			float alpha = (float) Math.abs(Math.cos(strikeImmunity * 16));
			sprite.setColor(c.r, c.g, c.b, alpha);
		}
		
		// if we are facing up and using shield, draw shield before character sprite
		if (isShielded() && faceDirection.equals(Direction.UP)) {
			shieldSlot.render(getBoundingRectangle(), faceDirection, batch);
		}
		
		// draw sprite
		sprite.draw(batch);
		
		// if we are shielding and not facing up, then draw shield after character sprite
		if (isShielded() && !faceDirection.equals(Direction.UP)){
			shieldSlot.render(getBoundingRectangle(), faceDirection, batch);
		}

		// draw weapon
		if (weaponSlot != null)
		{
			weaponSlot.render(sprite.getBoundingRectangle(),
					getFaceDirection(), batch);
		}

		doRenderAfter(batch);
	}

	protected void doRenderBefore(SpriteBatch batch)
	{
	}

	protected void doRenderAfter(SpriteBatch batch)
	{
	}

	public final void update(float deltaTime, Map currentMap)
	{
		addToStateTime(deltaTime);

		Iterator<StatusEffect> iter = inflictedEffects.iterator();
		while (iter.hasNext())
		{
			StatusEffect effect = iter.next();
			effect.applyStatusEffect(deltaTime, this);
			if (effect.hasWornOff())
			{
				iter.remove();
			}
		}

		if (weaponSlot != null)
			weaponSlot.update(deltaTime);

		doUpdate(deltaTime, currentMap);

		// update position
		setPosition(getBottomLeftX(), getBottomLeftY());
	}

	protected abstract void doUpdate(float deltaTime, Map currentMap);

	protected void updateTexture()
	{
		TextureRegion currentFrame = null;
		switch (getFaceDirection())
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
	}

	public void removedFromMap(Map map)
	{
		currentMap = null;
	}

	public void acceptPush(GameCharacter pushingCharacter)
	{
		Direction pushingDirection = pushingCharacter.getMoveDirection();
		faceDirection = pushingDirection.opposite();
		moveDirection = pushingDirection;
		float oldX = getBottomLeftX();
		float oldY = getBottomLeftY();
		float x = oldX + (getSpriteWidth() / 2) * pushingDirection.getDx();
		float y = oldY + (getSpriteHeight() / 2) * pushingDirection.getDy();
		Coordinate newCoordinate = new Coordinate();
		boolean didMove = getCurrentMap().checkCollision(x, y, oldX, oldY,
				this, newCoordinate);
		if (didMove)
		{
			setBottomLeftCorner(newCoordinate);
		}
	}
	
	protected Rectangle getHitboxInFrontOfCharacter()
	{
		float width = getSpriteWidth();
		float height = getSpriteHeight();
		float x = getBottomLeftX() + width * faceDirection.getDx();
		float y = getBottomLeftY() + height * faceDirection.getDy();
		return new Rectangle(x, y, width, height);
	}

	@Override
	public void receiveAttack(Weapon weapon)
	{
		if (strikeImmunity > 0)
			return;
		strikeImmunity = 1.0f;
		boolean result = attemptShieldBlock(weapon);
		if (result)
			return;
		inflictEffects(weapon.getEffects());
		receiveDamage(weapon.getPower());
		if (!name.equals("Player"))
		{
			world.getReputationSystem().addNewEvent("Attacked", "test group",
					this, bottomLeftCorner,null);
		}
	}

	@Override
	public void receiveAttack(Projectile projectile)
	{
		if (strikeImmunity > 0)
			return;

		strikeImmunity = 1.0f;
		boolean result = attemptShieldBlock(projectile);
		if (result)
			return;
		inflictEffects(projectile.getEffects());
		receiveDamage(projectile.getPower());
		if (!name.equals("Player"))
		{
			world.getReputationSystem().addNewEvent("Attacked", "test group",
					this, bottomLeftCorner,null);
		}
	}

	@Override
	public void receiveDamage(int damage)
	{
		damage = Math.max(damage, 0);
		int health = getHealth();
		damage = Math.min(damage, health);
		health -= damage;
		System.err.printf("%s takes %d damage. %d left.\n", getName(), damage,
				health);
		setHealth(health);
	}
	
	private boolean attemptShieldBlock(Weapon weapon) {
		if (isShielded() && weapon.getLastDirection().equals(faceDirection.opposite())) {
			// only allow shield to block if you are facing the attack
			shieldSlot.receiveAttack(weapon);
			return true;
		}
		return false;
	}
	
	private boolean attemptShieldBlock(Projectile projectile) {
		if (isShielded() && projectile.getFiredDirection().equals(faceDirection.opposite())) {
			shieldSlot.receiveAttack(projectile);
			return true;
		}
		return false;
	}
	
	private void inflictEffects(StatusEffect[] effects) {
		for(StatusEffect effect: effects) {
			if(!isImmune(effect)) {
				inflictEffect(effect);
			}
		}
	}

	private void inflictEffect(StatusEffect effect)
	{
		immunityHash.put(effect.getParentRef(),
				stateTime + effect.getImmunePeriod());
		inflictedEffects.add(effect);
	}

	private boolean isImmune(StatusEffect effect)
	{
		Float immunityEnd = immunityHash.get(effect.getParentRef());
		return immunityEnd != null && stateTime < immunityEnd;
	}

	public int getHealth()
	{
		return health;
	}

	public int getMaxHealth()
	{
		return MAX_HEALTH;
	}

	protected void setHealth(int health)
	{
		health = Math.max(health, 0);
		health = Math.min(getMaxHealth(), health);
		this.health = health;
	}

	public boolean isDead()
	{
		boolean result = getHealth() == 0;
		if (result)
			deathCleanup();
		return result;
	}

	private void deathCleanup()
	{
		if (weaponSlot != null)
			weaponSlot.quickFinishAttack();
	}

	public boolean isGameOver()
	{
		return false;
	}

	/**
	 * May be useful with debugging. Likely will be out of date if Character
	 * class is updated.
	 */
	@Override
	public String toString()
	{
		return String
				.format("(CharacterToString){name:%s, sprite:%s, facing:%s, moving:%s, stateTime:%lf}",
						name, sprite, faceDirection, moving, stateTime);
	}

	/**
	 * Likely insufficient implementation of equip
	 * 
	 * @param sword
	 */
	public boolean equipWeapon(Map m, Weapon weapon)
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

	public void swapWeapon(Map m)
	{
		m.removeEquippedWeapon(weaponSlot);
		weaponSlot.unequip();

		Weapon temp = weaponSlot;
		weaponSlot = swapWeaponSlot;
		swapWeaponSlot = temp;
		if (weaponSlot == null)
			return;
		weaponSlot.tryEquip(this);
		m.addEquippedWeapon(weaponSlot);
	}

	public Rectangle getHitBox()
	{
		Rectangle r = bottomLeftCorner.getBottomLeftRectangle(width, height);
		Vector2 center = new Vector2();
		r.getCenter(center);
		// TODO: Looks unnatural/bad when arrow disappears "before" it hits (because character has clear pixels)
		// This is just a hack to make it look better, needs a better fix
		int widthReduction = 8;
		int heightReduction = 8;
		r.setWidth(r.getWidth()-widthReduction);
		r.setHeight(r.getHeight()-heightReduction);
		r.setCenter(center);
		return r;
	}
	
	public boolean equipShield(Shield s) {
		if (shieldSlot != null) {
			unequipShield();
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

	public void unequipShield()
	{
		shieldSlot.unequip();
	}

	public NPCMemory getNPCMemory()
	{
		return npcMemory;
	}

	public float getSightDistance()
	{
		return sightDistance;
	}
	
	public float getHearingRadius()
	{
		return hearingRadius;
	}
	
	public boolean checkCoordinateInVision(Coordinate c) {
		return checkCoordinateInVision(c.getX(), c.getY());
	}
	
	public boolean checkCoordinateInVision(float x, float y)
	{
		float tempX = getCenterX();
		float tempY = getCenterY();
		float visionFieldPoints[] = new float[8];
		
		if(faceDirection.name().equalsIgnoreCase("up"))
		{
			visionFieldPoints[0] = tempX; //x value of point centered on NPC
			visionFieldPoints[1] = tempY; //y value of point centered on NPC
			visionFieldPoints[2] = tempX - 0.8f*(sightDistance); 
			visionFieldPoints[3] = tempY + 0.8f*(sightDistance);
			visionFieldPoints[4] = tempX;
			visionFieldPoints[5] = tempY + sightDistance;
			visionFieldPoints[6] = tempX +  0.8f*(sightDistance);
			visionFieldPoints[7] = tempY +  0.8f*(sightDistance); 
		}
		else if(faceDirection.name().equalsIgnoreCase("down"))
		{
			visionFieldPoints[0] = tempX; //x value of point centered on NPC
			visionFieldPoints[1] = tempY; //y value of point centered on NPC
			visionFieldPoints[2] = tempX -  0.8f*(sightDistance); 
			visionFieldPoints[3] = tempY -  0.8f*(sightDistance);
			visionFieldPoints[4] = tempX;
			visionFieldPoints[5] = tempY - sightDistance;
			visionFieldPoints[6] = tempX +  0.8f*(sightDistance);
			visionFieldPoints[7] = tempY -  0.8f*(sightDistance); 
		}
		else if(faceDirection.name().equalsIgnoreCase("left"))
		{
			visionFieldPoints[0] = tempX; //x value of point centered on NPC
			visionFieldPoints[1] = tempY; //y value of point centered on NPC
			visionFieldPoints[2] = tempX -  0.8f*(sightDistance); 
			visionFieldPoints[3] = tempY - 0.8f*(sightDistance);
			visionFieldPoints[4] = tempX - sightDistance;
			visionFieldPoints[5] = tempY;
			visionFieldPoints[6] = tempX - 0.8f*(sightDistance);
			visionFieldPoints[7] = tempY + 0.8f*(sightDistance); 
		}
		else //facing right
		{
			visionFieldPoints[0] = tempX; //x value of point centered on NPC
			visionFieldPoints[1] = tempY; //y value of point centered on NPC
			visionFieldPoints[2] = tempX + 0.8f*(sightDistance); 
			visionFieldPoints[3] = tempY - 0.8f*(sightDistance);
			visionFieldPoints[4] = tempX + sightDistance;
			visionFieldPoints[5] = tempY;
			visionFieldPoints[6] = tempX + 0.8f*(sightDistance);
			visionFieldPoints[7] = tempY + 0.8f*(sightDistance); 
		}
		
		Polygon visionCone = new Polygon(visionFieldPoints);
		if(visionCone.contains(x,y))
		{
			return true;
		}
		
		return false;
	}
	
	public boolean checkCoordinateWithinHearing(Coordinate c) {
		return checkCoordinateWithinHearing(c.getX(), c.getY());
	}
	
	public boolean checkCoordinateWithinHearing(float x, float y)
	{
		Circle circle = new Circle(getCenterX(),
				getCenterY(), hearingRadius);
		if(circle.contains(x, y))
		{
			return true;
		}
		return false;
	}

}
