package com.me.rpg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.me.rpg.World;
import com.me.rpg.characters.GameCharacter;
import com.me.rpg.characters.PlayableCharacter;
import com.me.rpg.combat.Equippable;
import com.me.rpg.combat.MeleeWeapon;
import com.me.rpg.combat.Projectile;

public class PauseScreen implements Serializable
{
	private static final long serialVersionUID = 8335584742453519873L;

	private World world;
	
	private transient BitmapFont menuFont;
	private transient Texture pauseScreenTexture;
	private transient Stage stage;
	private transient Table mainTable;
	
	private boolean inPauseMenu = false;
	private PlayableCharacter player;
	
	private int selection;
	
	public PauseScreen()
	{
		createTransients();
		initPauseMenu();
	}
	
	public void initPauseMenu()
	{
		inPauseMenu = false;
		//responses = new ArrayList<String>();
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
	
	
	
	public void openPauseMenu(PlayableCharacter player)
	{
		this.player = player;
		
		inPauseMenu = true;
		selection = 0;
	}
	
	public void closePauseMenu()
	{
		inPauseMenu = false;
	}
	
	public void acceptPlayerInput(String key) //returns true if dialogue ended
	{
		if(key.equals("E"))
		{		
			if(selection == 0)
			{
			
			}
			if(selection == 1)
			{
				
			}
			if(selection == 2)
			{
				
			}
			if(selection == 3)
			{
				
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
			if(selection < 3)
			{
				selection++;
			}
		}
	}
	
	
	public void render(SpriteBatch batch, OrthographicCamera camera)
	{
		LabelStyle normalStyle = new LabelStyle(menuFont, Color.WHITE);
		LabelStyle selectedStyle = new LabelStyle(menuFont, Color.YELLOW);
		
		Label resumeLabel = new Label("Resume Game", normalStyle);
		Label save1Label = new Label("Save to Slot 1",normalStyle);
		Label save2Label = new Label("Save to Slot 2", normalStyle);
		Label save3Label = new Label("Save to Slot 3", normalStyle);
		Label exitLabel = new Label("Exit Game", normalStyle);

		if(selection == 0) resumeLabel.setStyle(selectedStyle);
		else resumeLabel.setStyle(normalStyle);
		
		if(selection == 1) save1Label.setStyle(selectedStyle);
		else save1Label.setStyle(normalStyle);

		if(selection == 2) save2Label.setStyle(selectedStyle);
		else save2Label.setStyle(normalStyle);
		
		if(selection == 3) save3Label.setStyle(selectedStyle);
		else save3Label.setStyle(normalStyle);
		
		if(selection == 4) exitLabel.setStyle(selectedStyle);
		else exitLabel.setStyle(normalStyle);
		
		//Set up MainTable, contains everything else
		stage.addActor(mainTable);
		mainTable.setFillParent(true);
		mainTable.setBackground(new TextureRegionDrawable(new TextureRegion(pauseScreenTexture)));
		//mainTable.top().left().pad(10.0f);
		
		mainTable.add(resumeLabel);
		mainTable.row();
		mainTable.add(save1Label);
		mainTable.row();
		mainTable.add(save2Label);
		mainTable.row();
		mainTable.add(save3Label);
		mainTable.row();
		mainTable.add(exitLabel);
		
		mainTable.debug(); // turn on all debug lines (table, cell, and widget)

	    stage.draw();
	    Table.drawDebug(stage);
	    
		mainTable.clear();
		resumeLabel.clear();
		save1Label.clear();
		save2Label.clear();
		save3Label.clear();
		exitLabel.clear();
	}
	
	public boolean getInMenu()
	{
		return inPauseMenu;
	}
}
