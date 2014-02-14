package com.me.rpg.characters;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class DeadCharacter
{

	private GameCharacter deceased;
	private Sprite gravestone;
	private float fadeTime;
	private float passedTime;

	public DeadCharacter(GameCharacter deceased, Sprite gravestone,
			float fadeTime)
	{
		this.deceased = deceased;
		this.gravestone = gravestone;
		this.fadeTime = fadeTime;
		passedTime = 0f;
		this.gravestone.setPosition(deceased.getBottomLeftX(),
				deceased.getBottomLeftY());
	}

	public Rectangle getHitBox()
	{
		return deceased.getHitBox();
	}

	public void render(SpriteBatch batch)
	{
		if (passedTime < fadeTime)
		{
			deceased.getSprite().setColor(1f, 1f, 1f,
					(fadeTime - passedTime) / fadeTime);
			deceased.getSprite().draw(batch);
			gravestone.setColor(1f, 1f, 1f, passedTime / fadeTime);
			gravestone.setX(deceased.getSprite().getX()
					+ (float) Math.cos(passedTime * 200 * Math.PI));
			gravestone.draw(batch, passedTime / fadeTime);
			return;
		}
		gravestone.draw(batch);
	}

	public void update(float deltaTime)
	{
		if (passedTime > fadeTime)
			return;
		passedTime += deltaTime;
	}

	public String getDeadName()
	{
		return deceased.getName();
	}

	public boolean isGameOver()
	{
		if (passedTime < fadeTime)
			return false;
		return deceased.isGameOver();
	}
}
