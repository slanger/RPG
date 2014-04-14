package com.me.rpg.reputation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
import com.me.rpg.characters.GameCharacter;

public class ReputationDebugMenu implements Serializable
{
	private static final long serialVersionUID = -5772952471039852395L;

	private World world;
	
	//transients for saving
	private transient Stage stage;
	private transient BitmapFont menuFont;
	private transient Table mainTable;
	private transient Table leftPane;
	private transient Table rightPane;
	private transient Texture mainTableTexture;
	
	//non transients
	private boolean inMenu = false;
	private int rowSelectionIndex;
	private final int NUM_ROWS = 20;

	public ReputationDebugMenu(World world)
	{
		this.world = world;
		createTransients();
		initializeDebugMenu();
	}
	
	public void initializeDebugMenu()
	{
		inMenu = false;
		//responses = new ArrayList<String>();

	}
	
	private void createTransients()
	{
		// initialize renderer stuff
		stage = new Stage();
		menuFont = new BitmapFont();
		mainTable = new Table();
		leftPane = new Table();
		rightPane = new Table();
		mainTableTexture = new Texture(Gdx.files.internal("images/inventory_menu_background.png"));
	}
	
	private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException
	{
		inputStream.defaultReadObject();
		createTransients();
	}
	
	public void openMenu()
	{
		inMenu = true;
		rowSelectionIndex = 0;
	}
	
	public void closeMenu()
	{
		inMenu = false;
	}
	
	public boolean acceptPlayerInput(String key) //returns true if dialogue ended
	{
		if(key.equals("UP"))
		{
			if(rowSelectionIndex > 0)
			{
				rowSelectionIndex--;
			}
			
		}
		if(key.equals("DOWN"))
		{
			if(rowSelectionIndex < world.getCharactersInWorld().size()-1)
			{
				rowSelectionIndex++;
			}
		}
		return false;
	}
	
	
	public void render(SpriteBatch batch, OrthographicCamera camera)
	{
		ArrayList<GameCharacter> charactersInWorld = world.getCharactersInWorld();
		GameCharacter selectedCharacter = null;
		if(rowSelectionIndex > world.getCharactersInWorld().size())
		{
			selectedCharacter = charactersInWorld.get(world.getCharactersInWorld().size()-1);
		}
		else
		{
			selectedCharacter = charactersInWorld.get(rowSelectionIndex);
		}
		//Set up MainTable, contains everything else
		stage.addActor(mainTable);
		mainTable.setFillParent(true);
		mainTable.setBackground(new TextureRegionDrawable(new TextureRegion(mainTableTexture)));
		mainTable.top().left().pad(10.0f);
		mainTable.add(leftPane).width(200.0f).fillY().expandY().top().left();
		mainTable.add(rightPane).top();
		leftPane.top().left();
		for(int i=0; i<NUM_ROWS; i++)
		{
			LabelStyle style = new LabelStyle(menuFont, Color.WHITE);
			LabelStyle selectedStyle = new LabelStyle(menuFont, Color.GREEN);
			if(i < charactersInWorld.size())
			{
				String name = charactersInWorld.get(i).getName();
				String dispositionValue = Integer.toString(charactersInWorld.get(i).getDispositionValue());
				String viewOfPlayer = charactersInWorld.get(i).getViewOfPlayer();
				
				Label nameLabel = null;
				Label dispositionValueLabel = null;
				Label viewOfPlayerLabel = null;
				
				if(i == rowSelectionIndex)
				{
					nameLabel = new Label(name, selectedStyle);
					dispositionValueLabel = new Label(dispositionValue, selectedStyle);
					viewOfPlayerLabel = new Label(viewOfPlayer, selectedStyle);
				}
				else
				{
					nameLabel = new Label(name, style);
					dispositionValueLabel = new Label(dispositionValue, style);
					viewOfPlayerLabel = new Label(viewOfPlayer, style);
				}
		
				leftPane.row().expandX().fillX();
				leftPane.add(nameLabel).padRight(10.0f);
				leftPane.add(dispositionValueLabel).padRight(10.0f);
				leftPane.add(viewOfPlayerLabel).padRight(10.0f);
				
			}
		}
		if(selectedCharacter != null && !selectedCharacter.getGroup().equals("player_group"))
		{
			selectedCharacter.getNPCMemory().getRememberedEvents();
			for(int i=0; i<selectedCharacter.getNPCMemory().getRememberedEvents().size(); i++)
			{
				String eventInfoString = selectedCharacter.getNPCMemory().getRememberedEvents().get(i).toString();
				LabelStyle style = new LabelStyle(menuFont, Color.WHITE);
				
				Label eventInfo = new Label(eventInfoString,style);
				
				rightPane.row().expandX().fillX();
				
				rightPane.add(eventInfo).padRight(10.0f);
			}
		}
		
		
		//mainTable.debug(); // turn on all debug lines (table, cell, and widget)
		//leftPane.debug();
		//rightPane.debug();
	    stage.draw();
	    //Table.drawDebug(stage);
	    
		mainTable.clear();
		leftPane.clear();
		rightPane.clear();
	}
	
	public boolean getInMenu()
	{
		return inMenu;
	}
}
