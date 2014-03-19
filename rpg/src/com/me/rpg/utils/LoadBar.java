package com.me.rpg.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class LoadBar
{

	private final float x, y, width, height;
	private final Texture loadTexture;
	private float progress = 0;

	public LoadBar(float x, float y, float width, float height, Texture loadTexture)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.loadTexture = loadTexture;
	}

	public void update(float progress)
	{
		this.progress = progress;
	}

	public void render(SpriteBatch batch)
	{
		batch.draw(loadTexture, x, y, width * progress, height);
	}

}
