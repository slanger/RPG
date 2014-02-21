package com.me.rpg.characters;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.me.rpg.World;
import com.me.rpg.ai.StandStillAI;
import com.me.rpg.ai.WalkAI;
import com.me.rpg.combat.IAttackable;
import com.me.rpg.combat.Projectile;
import com.me.rpg.combat.Shield;
import com.me.rpg.combat.StatusEffect;
import com.me.rpg.combat.Weapon;
import com.me.rpg.maps.Map;
import com.me.rpg.reputation.NPCMemory;
import com.me.rpg.utils.Coordinate;
import com.me.rpg.utils.Direction;

public abstract class GameCharacter implements IAttackable
{

	private static final int MAX_HEALTH = 100;

	private String name;
	private Sprite sprite;
	private Coordinate bottomLeftCorner;
	private TextureRegion rightIdle, leftIdle, upIdle, downIdle;
	private Animation rightWalkAnimation, leftWalkAnimation, upWalkAnimation,
			downWalkAnimation;
	private Direction moveDirection = Direction.DOWN;
	private Direction faceDirection = Direction.DOWN;
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
	protected boolean strafing;
	
	//Reputation Stuff
	protected NPCMemory npcMemory; 
	protected World world;
	protected float sightDistance;
	//
		

	protected GameCharacter(String name, Texture spritesheet, int width,
			int height, int tileWidth, int tileHeight, float animationDuration, World world)
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
		
		this.world=world;
		npcMemory = new NPCMemory(world.getReputationSystem().getMasterEventList());
		sightDistance = 250.0f;
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
		this.bottomLeftCorner = new Coordinate(
				center.getX() - getSpriteWidth() / 2,
				center.getY() - getSpriteHeight() / 2);
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
	
	public boolean isStrafing() {
		return strafing;
	}
	
	public void setStrafing(boolean strafing) {
		this.strafing = strafing;
	}
	
	public Direction getFaceDirection() {
		return faceDirection;
	}
	
	public void setFaceDirection(Direction faceDirection) {
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
		if (!strafing || moveDirection == faceDirection)
			return speed;
		return speed * 0.6f;
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
		doRenderBefore(batch);

		// blink if Character has been hit
		if (strikeImmunity > 0)
		{
			Color c = sprite.getColor();
			sprite.setColor(c.r, c.g, c.b, (float)Math.abs(Math.cos(strikeImmunity*16))); // 16 is a good value for blinking rate
		}

		// draw sprite
		sprite.draw(batch);

		// draw weapon
		if (weaponSlot != null)
		{
			weaponSlot.render(sprite.getBoundingRectangle(), getFaceDirection(), batch);
		}

		doRenderAfter(batch);
	}
	
	protected void doRenderBefore(SpriteBatch batch) { }

	protected void doRenderAfter(SpriteBatch batch)  { }
	
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
		walkAI.start();
	}

	public void removedFromMap(Map map)
	{
		walkAI.stop();
		currentMap = null;
	}

	public abstract void acceptGoodAction(GameCharacter characterDoingAction);

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
		boolean didMove = getCurrentMap().checkCollision(x, y, oldX, oldY, this, newCoordinate);
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
	public void receiveAttack(Weapon weapon) {
		if (strikeImmunity > 0)
			return;
		
		strikeImmunity = 1.0f;
		boolean result = attemptShieldBlock(weapon);
		if (result)
			return;
		inflictEffects(weapon.getEffects());
		receiveDamage(weapon.getPower());
		if(!name.equals("Player")){
			world.getReputationSystem().addNewEvent("Attacked", "test group", this, bottomLeftCorner);
		}
	}
	
	@Override
	public void receiveAttack(Projectile projectile) {
		if (strikeImmunity > 0)
			return;

		strikeImmunity = 1.0f;
		boolean result = attemptShieldBlock(projectile);
		if (result)
			return;
		inflictEffects(projectile.getEffects());
		receiveDamage(projectile.getPower());
		if(!name.equals("Player")){
			world.getReputationSystem().addNewEvent("Attacked", "test group", this, bottomLeftCorner);
		}
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
	}
	
	public boolean isDead() {
		boolean result = getHealth() == 0;
		if (result)
			deathCleanup();
		return result;
	}
	
	private void deathCleanup() {
		if (weaponSlot != null)
			weaponSlot.quickFinishAttack();
	}
	
	public boolean isGameOver() {
		return false;
	}
	
	private boolean attemptShieldBlock(Weapon weapon) {
		if (shieldSlot != null && weapon.getLastDirection().equals(faceDirection.opposite())) {
			// only allow shield to block if you are facing the attack
			shieldSlot.receiveAttack(weapon);
			return true;
		}
		return false;
	}
	
	private boolean attemptShieldBlock(Projectile projectile) {
		if (shieldSlot != null && projectile.getFiredDirection().equals(faceDirection.opposite())) {
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
				.format("(CharacterToString){name:%s, sprite:%s, facing:%s, moving:%s, stateTime:%lf}",
						name, sprite, faceDirection, moving, stateTime);
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
		r.setWidth(r.getWidth()-8); // why -8?
		r.setHeight(r.getHeight()-8); // why -8?
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

	public NPCMemory getNPCMemory() {
		return npcMemory;
	}

	public float getSightDistance() {
		return sightDistance;
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

}
