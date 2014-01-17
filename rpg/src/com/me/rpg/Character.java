package com.me.rpg;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class Character
{
	
	protected String name;
	protected Sprite sprite;
	protected TextureRegion rightIdle, leftIdle, upIdle, downIdle;
	protected Animation rightWalkAnimation, leftWalkAnimation, upWalkAnimation, downWalkAnimation;
	protected Direction direction = Direction.DOWN;
	protected boolean moving = false;
	protected float stateTime = 0f;
	
	protected enum Direction
	{
		RIGHT	(0),
		LEFT	(1),
		UP		(2),
		DOWN	(3);
		
		int index;
		
		Direction(int index)
		{
			this.index = index;
		}
		
		public int getIndex()
		{
			return index;
		}
		
		public static Direction getDirection(int index)
		{
			for (Direction d : Direction.values())
			{
				if (d.getIndex() == index)
				{
					return d;
				}
			}
			throw new RuntimeException("Could not find the Direction with index of " + index);
		}
	}
	
	protected Character(String name, Texture spritesheet, int width, int height, int tileWidth, int tileHeight, int startX, int startY, float animationDuration)
	{
		TextureRegion[][] sheet = TextureRegion.split(spritesheet, tileWidth, tileHeight);
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
		sprite.setPosition(startX, startY);
	}
	
	public void render(SpriteBatch batch)
	{
		sprite.draw(batch);
		update();
	}
	
	protected abstract void update();
	
}
