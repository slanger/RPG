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
	private transient Texture mainTableTexture;
	
	//non transients
	private boolean inMenu = false;
	private int pageSelectionIndex;
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
		pageSelectionIndex = 0;
	}
	
	public void closeMenu()
	{
		inMenu = false;
	}
	
	public boolean acceptPlayerInput(String key) //returns true if dialogue ended
	{
		if(key.equals("LEFT"))
		{
			if(pageSelectionIndex > 0)
			{
				pageSelectionIndex--;
			}
			
		}
		if(key.equals("RIGHT"))
		{
//			if(pageSelectionIndex < NUM_ITEM_ROWS-1)
//			{
//				rowSelectionIndex++;
//			}
		}
		return false;
	}
	
	
	public void render(SpriteBatch batch, OrthographicCamera camera)
	{
		ArrayList<GameCharacter> charactersInWorld = world.getCharactersInWorld();
		//Set up MainTable, contains everything else
		stage.addActor(mainTable);
		mainTable.setFillParent(true);
		mainTable.setBackground(new TextureRegionDrawable(new TextureRegion(mainTableTexture)));
		mainTable.top().left().pad(10.0f);
	
		for(int i=0; i<NUM_ROWS; i++)
		{
			LabelStyle style = new LabelStyle(menuFont, Color.WHITE);
			if(i < charactersInWorld.size())
			{
				String name = charactersInWorld.get(i).getName();
				String dispositionValue = Integer.toString(charactersInWorld.get(i).getDispositionValue());
				String viewOfPlayer = charactersInWorld.get(i).getViewOfPlayer();
				
				Label nameLabel = new Label(name, style);
				Label dispositionValueLabel = new Label(dispositionValue, style);
				Label viewOfPlayerLabel = new Label(viewOfPlayer, style);
		
				mainTable.add(nameLabel).padRight(10.0f);
				mainTable.add(dispositionValueLabel).padRight(10.0f);
				mainTable.add(viewOfPlayerLabel).padRight(10.0f);
				mainTable.row();
			}
		}
		
		mainTable.debug(); // turn on all debug lines (table, cell, and widget)
	    stage.draw();
	    //Table.drawDebug(stage);
	    
		mainTable.clear();
	}
	
	public boolean getInMenu()
	{
		return inMenu;
	}
}
