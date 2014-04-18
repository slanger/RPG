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
	private final int NUM_ROWS = 27;
	private int showIndex = 0;

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
		showIndex = 0;
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
			
			if(rowSelectionIndex >= 27)
			{
				showIndex--;
			}
		}
		if(key.equals("DOWN"))
		{
			if(rowSelectionIndex < world.getCharactersInWorld().size()-1)
			{
				rowSelectionIndex++;
			}
			if(rowSelectionIndex >27)
			{
				showIndex ++;
			}
		}
		System.out.println("rowIndex "+rowSelectionIndex );
		System.out.println("showIndex "+showIndex );
		return false;
		
	}
	
	
	public void render(SpriteBatch batch, OrthographicCamera camera)
	{
		ArrayList<GameCharacter> charactersInWorld = new ArrayList<GameCharacter>(world.getCharactersInWorld());
		for(int i = 0; i<charactersInWorld.size(); i++)
		{
			if (charactersInWorld.get(i).getGroup().equalsIgnoreCase("player_group"))
			{
				charactersInWorld.remove(i);
			}
		}
		GameCharacter selectedCharacter = null;
		if(rowSelectionIndex >= charactersInWorld.size())
		{
			rowSelectionIndex = charactersInWorld.size()-1;
		}
		
		selectedCharacter = charactersInWorld.get(rowSelectionIndex);
		
		//Set up MainTable, contains everything else
		stage.addActor(mainTable);
		mainTable.setFillParent(true);
		mainTable.setBackground(new TextureRegionDrawable(new TextureRegion(mainTableTexture)));
		mainTable.top().left().pad(10.0f);
		mainTable.add(leftPane).width(200.0f).fillY().expandY().top().left();
		mainTable.add(rightPane).top();
		leftPane.top().left();
		
		
		LabelStyle style = new LabelStyle(menuFont, Color.WHITE);
		LabelStyle selectedStyle = new LabelStyle(menuFont, Color.GREEN);
		
		Label temp1 = new Label("Name",style);
		Label temp2 = new Label("Disp",style);
		Label temp3 = new Label("PlayerView",style);
		leftPane.row().expandX().fillX();
		leftPane.add(temp1).padRight(10.0f);
		leftPane.add(temp2).padRight(10.0f);
		leftPane.add(temp3).padRight(10.0f);
		for(int i=showIndex; i<NUM_ROWS+showIndex; i++)
		{
			
			if(i < charactersInWorld.size())
			{
				String name = charactersInWorld.get(i).getName();
				String dispositionValue = Integer.toString(charactersInWorld.get(i).getDispositionValue());
				String viewOfPlayer = charactersInWorld.get(i).getViewOfPlayer();
				
				Label nameLabel = null;
				Label dispositionValueLabel = null;
				Label viewOfPlayerLabel = null;
				
				if(i == rowSelectionIndex+showIndex)
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
		
		Label temp4 = new Label("EventType",style);
		Label temp5 = new Label("Group",style);
		Label temp6 = new Label("Character",style);
		Label temp7 = new Label("Magnitude",style);
		
		rightPane.row().expandX().fillX();
		rightPane.add(temp4).padRight(10.0f);
		rightPane.add(temp5).padRight(10.0f);
		rightPane.add(temp6).padRight(10.0f);
		rightPane.add(temp7).padRight(10.0f);

		
		if(selectedCharacter != null && !selectedCharacter.getGroup().equals("player_group"))
		{
			selectedCharacter.getNPCMemory().getRememberedEvents();
			for(int i=0; i<selectedCharacter.getNPCMemory().getRememberedEvents().size(); i++)
			{
				Label eventTypeLabel = new Label(selectedCharacter.getNPCMemory().getRememberedEvents().get(i).getRepEventPointer().getEventType(), style);
				Label groupLabel = new Label(selectedCharacter.getNPCMemory().getRememberedEvents().get(i).getRepEventPointer().getGroupAffected(), style);
				Label npcNameLabel = new Label(selectedCharacter.getNPCMemory().getRememberedEvents().get(i).getRepEventPointer().getCharacterAffected().getName(), style);
				Label magnitudeLabel = new Label(Integer.toString(selectedCharacter.getNPCMemory().getRememberedEvents().get(i).getMagnitudeKnownByNPC()),style);
				
				rightPane.row().expandX().fillX();
				rightPane.add(eventTypeLabel).padRight(10.0f);
				rightPane.add(groupLabel).padRight(10.0f);
				rightPane.add(npcNameLabel).padRight(10.0f);
				rightPane.add(magnitudeLabel).padRight(10.0f);

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
