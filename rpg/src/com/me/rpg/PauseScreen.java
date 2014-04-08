package com.me.rpg;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.me.rpg.World;
import com.me.rpg.characters.PlayableCharacter;

public class PauseScreen implements Serializable
{
	private static final long serialVersionUID = 8335584742453519873L;

	private World world;
	
	private transient BitmapFont menuFont;
	private transient Texture pauseScreenTexture;
	private transient Stage stage;
	
	private boolean inPauseMenu = false;
	private PlayableCharacter player;
	
	private int selection;
	
	public PauseScreen(World world)
	{
		this.world = world;
		createTransients();
		initPauseMenu();
	}
	
	public void initPauseMenu()
	{
		inPauseMenu = false;
	}
	
	private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException
	{
		inputStream.defaultReadObject();
		createTransients();
	}
	
	private void createTransients()
	{
		stage = new Stage();
		menuFont = new BitmapFont();
		pauseScreenTexture = new Texture(Gdx.files.internal("images/PauseScreen.png"));
	}
	
	public void openPauseMenu()
	{
		world.setUpdateEnable(false);
		inPauseMenu = true;
		selection = 0;
	}
	
	public void closePauseMenu()
	{
		inPauseMenu = false;
		world.setUpdateEnable(true);
	}
	
	public void acceptPlayerInput(String key) //returns true if dialogue ended
	{
		if(key.equals("ESCAPE"))
		{
			closePauseMenu();
		}
		
		else if(key.equals("ENTER"))
		{		
			if(selection == 0) //resume
			{
				closePauseMenu();
			}
			if(selection == 1) //save
			{
				world.setSaveGame(true);
				closePauseMenu();
			}
			if(selection == 2) //exit to main menu
			{
				inPauseMenu=false;
				world.setResetGame(true);
			}
		}
		else if(key.equals("UP"))
		{
			if(selection > 0)
			{
				selection--;
			}
		}
		else if(key.equals("DOWN"))
		{
			if(selection < 2)
			{
				selection++;
			}
		}
	}
	
	
	public void render(SpriteBatch batch, OrthographicCamera camera)
	{

		batch.draw(pauseScreenTexture, 0, 0, camera.viewportWidth, camera.viewportHeight);

		//
		menuFont.setScale(1.6f);
		LabelStyle normalStyle = new LabelStyle(menuFont, Color.WHITE);
		LabelStyle selectedStyle = new LabelStyle(menuFont, Color.YELLOW);
		
		Label resumeLabel = new Label("Resume", normalStyle);
		Label saveLabel = new Label("Save Game", normalStyle);
		Label exitLabel = new Label("Exit to main screen",normalStyle);

		if(selection == 0)
		{
			resumeLabel.setStyle(selectedStyle);
		}
		else
		{
			resumeLabel.setStyle(normalStyle);
		}
		
		if(selection == 1)
		{
			saveLabel.setStyle(selectedStyle);
		}
		else
		{
			saveLabel.setStyle(normalStyle);
		}
		
		if(selection == 2)
		{
			exitLabel.setStyle(selectedStyle);
		}
		else
		{
			exitLabel.setStyle(normalStyle);
		}
		
		resumeLabel.setBounds(230, 380, 0, 0);
		saveLabel.setBounds(230, 350, 0, 0);
		exitLabel.setBounds(230, 320, 0, 0);
		
		resumeLabel.draw(batch, 1.0f);
		saveLabel.draw(batch, 1.0f);
		exitLabel.draw(batch, 1.0f);
		
		resumeLabel.clear();
		saveLabel.clear();
		exitLabel.clear();
	}
	
	public boolean getInMenu()
	{
		return inPauseMenu;
	}
}
