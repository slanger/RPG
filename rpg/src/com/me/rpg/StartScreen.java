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
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.me.rpg.combat.MeleeWeapon;
import com.me.rpg.combat.Projectile;

public class StartScreen implements Screen, InputProcessor
{

	private Stage stage;
	private Skin skin;
	private TextureAtlas atlas;
	private Table table;
	private ScreenHandler screenHandler;
	private SpriteBatch spriteBatch = new SpriteBatch();
	private Texture mainScreenTexture;
	private int width, height;
	private TextButton buttonPlay, buttonSettings;
	private BitmapFont menuFont;
	private Label heading;

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

			@Override
			public boolean keyUp(InputEvent event, int keycode)
			{
				if (keycode == Keys.ENTER)
				{
					//stage.removeListener(this);
					if(selection == 0) //new game
					{
						screenHandler.setScreen(new RPG(screenHandler, World.getNewInstance(), 1));
					}
					if(selection == 1) //load save 1
					{
						World world = retrieveSaveFile(1);
						if (world == null)
							world = World.getNewInstance();
						screenHandler.setScreen(new RPG(screenHandler, world, 1));
					}
					if(selection == 2) //load save 2
					{
						//stage.removeListener(this);
						World world = retrieveSaveFile(2);
						if (world == null)
							world = World.getNewInstance();
						screenHandler.setScreen(new RPG(screenHandler, world, 2));
					}
					if(selection == 3) //load save 3
					{
						//stage.removeListener(this);
						World world = retrieveSaveFile(3);
						if (world == null)
							world = World.getNewInstance();
						screenHandler.setScreen(new RPG(screenHandler, world, 3));
					}
				}
				else if (keycode == Keys.NUM_1)
				{
					//stage.removeListener(this);
					World world = retrieveSaveFile(1);
					if (world == null)
						world = World.getNewInstance();
					screenHandler.setScreen(new RPG(screenHandler, world, 1));
				}
				else if (keycode == Keys.NUM_2)
				{
					//stage.removeListener(this);
					World world = retrieveSaveFile(2);
					if (world == null)
						world = World.getNewInstance();
					screenHandler.setScreen(new RPG(screenHandler, world, 2));
				}
				else if (keycode == Keys.NUM_3)
				{
					//stage.removeListener(this);
					World world = retrieveSaveFile(3);
					if (world == null)
						world = World.getNewInstance();
					screenHandler.setScreen(new RPG(screenHandler, world, 3));
				}
				else if (keycode == Keys.UP)
				{
					//stage.removeListener(this);
					if(selection > 0) selection--;
					System.out.println(selection);

				}
				else if (keycode == Keys.DOWN)
				{
					//stage.removeListener(this);
					if(selection < 3) selection++;
					System.out.println(selection);
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
		
		Label newGameLabel = new Label("New Game", normalStyle);
		Label save1Label = new Label("Load Save 1", normalStyle);
		Label save2Label = new Label("Load Save 2", normalStyle);
		Label save3Label = new Label("Load Save 3", normalStyle);


		if(selection == 0)
		{
			newGameLabel.setStyle(selectedStyle);
		}
		else
		{
			newGameLabel.setStyle(normalStyle);
		}
		
		if(selection == 1)
		{
			save1Label.setStyle(selectedStyle);
		}
		else
		{
			save1Label.setStyle(normalStyle);
		}
		
		if(selection == 2)
		{
			save2Label.setStyle(selectedStyle);
		}
		else
		{
			save2Label.setStyle(normalStyle);
		}
		
		if(selection == 3)
		{
			save3Label.setStyle(selectedStyle);
		}
		else
		{
			save3Label.setStyle(normalStyle);
		}
		
		newGameLabel.setBounds(250, 380, 0, 0);
		save1Label.setBounds(250, 350, 0, 0);
		save2Label.setBounds(250, 320, 0, 0);
		save3Label.setBounds(250, 290, 0, 0);

		newGameLabel.draw(spriteBatch, 1.0f);
		save1Label.draw(spriteBatch, 1.0f);
		save2Label.draw(spriteBatch, 1.0f);
		save3Label.draw(spriteBatch, 1.0f);
		
		spriteBatch.end();

		newGameLabel.clear();
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
