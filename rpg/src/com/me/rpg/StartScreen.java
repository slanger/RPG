package com.me.rpg;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

public class StartScreen implements Screen, InputProcessor
{

	private Stage stage;
	private Skin skin;
	private TextureAtlas atlas;
	private ScreenHandler screenHandler;
	private SpriteBatch spriteBatch = new SpriteBatch();
	private Texture mainScreenTexture;
	private int width, height;
	private BitmapFont menuFont;

	private int selection=0;
	// CONSTRUCTOR
	public StartScreen(ScreenHandler screenHandler)
	{
		this.screenHandler = screenHandler;
	}

	@Override
	public void show()
	{
		mainScreenTexture = new Texture(
				Gdx.files.internal("images/MainScreen_new.png"));
		stage = new Stage();

		menuFont = new BitmapFont();
		// INPUT PROCESSOR
		Gdx.input.setInputProcessor(stage);
		
		// if Enter is pressed, go to game screen
		// TODO add more keyboard functionality to start menu
		
		stage.addListener(new InputListener()
		{
			long timeWait = System.currentTimeMillis();
			@Override
			public boolean keyUp(InputEvent event, int keycode)
			{
				if (keycode == Keys.ENTER)
				{
					if(System.currentTimeMillis() - timeWait < 1000)
					{
						return true;
					}
					System.out.println("enter pressed");

					if(selection == 0) //play slot 1
					{
						World world = retrieveSaveFile(1);
						if (world == null)
							world = World.getNewInstance();
						screenHandler.setScreen(new RPG(screenHandler, world, 1));
						stage.removeListener(this);
					}
					if(selection == 1) //play slot 2
					{
						//stage.removeListener(this);
						World world = retrieveSaveFile(2);
						if (world == null)
							world = World.getNewInstance();
						screenHandler.setScreen(new RPG(screenHandler, world, 2));
						stage.removeListener(this);
					}
					if(selection == 2) //Play slot 3
					{
						//stage.removeListener(this);
						World world = retrieveSaveFile(3);
						if (world == null)
							world = World.getNewInstance();
						screenHandler.setScreen(new RPG(screenHandler, world, 3));
						stage.removeListener(this);
					}
					if(selection == 3) //Exit 
					{
						Gdx.app.exit();
					}		
				}
				else if (keycode == Keys.UP)
				{
					if(selection > 0) selection--;
				}
				else if (keycode == Keys.DOWN)
				{
					if(selection < 3) selection++;
				}
				return true;
			}

		});
	}

	private World retrieveSaveFile(int saveFileId)
	{
		String fileName = saveFileId + ".sav";

		try
		{
			FileInputStream saveFile = new FileInputStream(fileName);
			ObjectInputStream retrieveStream = new ObjectInputStream(saveFile);
			World world = (World) retrieveStream.readObject();
			retrieveStream.close();
			world.pushMessage("Game retrieved from " + fileName);
			return world;
		}
		catch (FileNotFoundException e)
		{
			System.err.println("Could not find file: " + saveFileId + ".sav");
			System.err.println("Creating new file for " + saveFileId + ".sav");
			return null;
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		catch (ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void render(float delta)
	{
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		spriteBatch.begin();
		spriteBatch.draw(mainScreenTexture, 0, 0, width, height);
	

		
		//
		menuFont.setScale(1.6f);
		LabelStyle normalStyle = new LabelStyle(menuFont, Color.WHITE);
		LabelStyle selectedStyle = new LabelStyle(menuFont, Color.YELLOW);
		
		Label save1Label = new Label("Play Save 1", normalStyle);
		Label save2Label = new Label("Play Save 2", normalStyle);
		Label save3Label = new Label("Play Save 3", normalStyle);
		Label exitLabel = new Label("Exit", normalStyle);


		if(selection == 0)
		{
			save1Label.setStyle(selectedStyle);
		}
		else
		{
			save1Label.setStyle(normalStyle);
		}
		
		if(selection == 1)
		{
			save2Label.setStyle(selectedStyle);
		}
		else
		{
			save2Label.setStyle(normalStyle);
		}
		
		if(selection == 2)
		{
			save3Label.setStyle(selectedStyle);
		}
		else
		{
			save3Label.setStyle(normalStyle);
		}
		
		if(selection == 3)
		{
			exitLabel.setStyle(selectedStyle);
		}
		else
		{
			exitLabel.setStyle(normalStyle);
		}
		
		save1Label.setBounds(250, 380, 0, 0);
		save2Label.setBounds(250, 350, 0, 0);
		save3Label.setBounds(250, 320, 0, 0);
		exitLabel.setBounds(250, 290, 0, 0);

		save1Label.draw(spriteBatch, 1.0f);
		save2Label.draw(spriteBatch, 1.0f);
		save3Label.draw(spriteBatch, 1.0f);
		exitLabel.draw(spriteBatch, 1.0f);

		spriteBatch.end();

		exitLabel.clear();
		save1Label.clear();
		save2Label.clear();
		save3Label.clear();
		//
	}

	@Override
	public void resize(int width, int height)
	{
		this.width = width;
		this.height = height;
	}

	@Override
	public void hide()
	{
		// TODO Auto-generated method stub

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
	public void dispose()
	{
		stage.dispose();
		skin.dispose();
		atlas.dispose();
		spriteBatch.dispose();
		mainScreenTexture.dispose();
		menuFont.dispose();
	}

	@Override
	public boolean keyDown(int keycode)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount)
	{
		// TODO Auto-generated method stub
		return false;
	}

}
