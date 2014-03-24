package com.me.rpg;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import com.badlogic.gdx.Screen;

public class RPG implements Screen
{

	public final ScreenHandler screenHandler;
	public final World world;
	private final int saveFileId;

	public RPG(ScreenHandler screenHandler, World world, int saveFileId)
	{
		this.screenHandler = screenHandler;
		this.world = world;
		this.saveFileId = saveFileId;
	}

	@Override
	public void render(float deltaTime)
	{
		// update before render
		update(deltaTime);

		// render World
		world.render();
	}

	private void update(float deltaTime)
	{
		world.update(deltaTime);
		if (world.isGameOver())
		{
			gameOver();
		}
		if (world.saveGame())
		{
			saveGame();
		}
	}

	private void gameOver()
	{
		screenHandler.setScreen(new GameOverScreen(screenHandler));
	}

	/**
	 * Save world instance to a file
	 */
	private void saveGame()
	{
		String fileName = saveFileId + ".sav";

		world.setSaveGame(false);

		try
		{
			FileOutputStream saveFile = new FileOutputStream(fileName);
			ObjectOutputStream saveStream = new ObjectOutputStream(saveFile);
			saveStream.writeObject(world);
			saveStream.close();
			System.out.println("Game saved to " + fileName);
		}
		catch (FileNotFoundException e)
		{
			throw new RuntimeException(e);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void resize(int width, int height)
	{
		// TODO calculate new viewport
		// float aspectRatio = (float)width/(float)height;
		// float scale = 1f;
		// Vector2 crop = new Vector2(0f, 0f);
		//
		// if(aspectRatio > ASPECT_RATIO) {
		// scale = (float) height / (float) VIRTUAL_HEIGHT;
		// crop.x = (width - VIRTUAL_WIDTH * scale) / 2f;
		// } else if(aspectRatio < ASPECT_RATIO) {
		// scale = (float) width / (float) VIRTUAL_WIDTH;
		// crop.y = (height - VIRTUAL_HEIGHT * scale) / 2f;
		// } else {
		// scale = (float) width / (float) VIRTUAL_WIDTH;
		// }
		//
		// float w = (float) VIRTUAL_WIDTH * scale;
		// float h = (float) VIRTUAL_HEIGHT * scale;
		// viewport = new Rectangle(crop.x, crop.y, w, h);
	}

	@Override
	public void pause()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void resume()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void show()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void hide()
	{
		dispose();
	}

	@Override
	public void dispose()
	{
		world.dispose();
	}

}
