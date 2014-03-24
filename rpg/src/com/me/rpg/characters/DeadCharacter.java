package com.me.rpg.characters;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.ScreenHandler;

public class DeadCharacter implements Serializable
{

	private static final long serialVersionUID = 1544763022077391987L;

	private GameCharacter deceased;
	private String gravestoneSpritePath;
	private float fadeTime;
	private float passedTime = 0f;

	private transient Sprite gravestone;

	public DeadCharacter(GameCharacter deceased, String gravestoneSpritePath,
			float fadeTime)
	{
		this.deceased = deceased;
		this.gravestoneSpritePath = gravestoneSpritePath;
		this.fadeTime = fadeTime;

		create();
	}

	private void create()
	{
		Texture gravestoneTexture = ScreenHandler.manager.get(gravestoneSpritePath, Texture.class);
		gravestone = new Sprite(new TextureRegion(gravestoneTexture, 0, 0, 34, 41));
		gravestone.setPosition(deceased.getBottomLeftX(), deceased.getBottomLeftY());
	}

	private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException
	{
		inputStream.defaultReadObject();
		create();
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
