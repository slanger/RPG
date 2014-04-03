package com.me.rpg.inventory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.me.rpg.World;
import com.me.rpg.characters.GameCharacter;
import com.me.rpg.characters.PlayableCharacter;

public class InventoryMenu implements Serializable
{

	private static final long serialVersionUID = -4102496331718548744L;
	
	//transients for saving
	
	private transient Stage stage;
	
	private transient BitmapFont menuFont;
	
	private transient Table mainTable;
	private transient Table leftPane;
	private transient Table rightPane;
	private transient Table testTable;
	
	private transient Texture texture;
	private transient Texture texture2;
	//inventory 
	private boolean inMenu = false;
	private PlayableCharacter player;
	
	private int rowSelectionIndex;
	private int colSelectionIndex;
	
	private int showMeleeWeaponsIndex;
	private int showProjWeaponsIndex;
	private int showShieldsIndex;
	private int showMiscItemsIndex;

	
	private final int NUM_ITEM_ROWS = 4;
	
	private ArrayList<String> meleeWeapons;  //first row
	private ArrayList<String> projWeapons;   //second row
	private ArrayList<String> shields;       //third row
	private ArrayList<String> miscItems; 	 //fourth row
	
	public InventoryMenu()
	{
		createTransients();
		initializeDialogueSystem();
	}
	
	public void initializeDialogueSystem()
	{
		
		//responses = new ArrayList<String>();

	}
	
	private void createTransients()
	{
		// initialize renderer stuff
		stage = new Stage();
		menuFont = new BitmapFont();
		inMenu = false;
		mainTable = new Table();
		testTable = new Table();
		texture = new Texture(Gdx.files.internal("images/inventory_menu_background.png"));
		texture2 = new Texture(Gdx.files.internal("images/inventory_menu_item_background.png"));
	}
	
	private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException
	{
		inputStream.defaultReadObject();
		createTransients();
	}
	
	public void openInventory(PlayableCharacter player)
	{
		this.player = player;
		
		inMenu = true;
		rowSelectionIndex = 0;
		colSelectionIndex = 0;
		
		meleeWeapons = new ArrayList<String>();
		meleeWeapons.add("item1");

		projWeapons = new ArrayList<String>();
		projWeapons.add("item1");
		projWeapons.add("item2");
		
		shields = new ArrayList<String>();
		shields.add("item1");
		shields.add("item2");
		shields.add("item1");

		miscItems = new ArrayList<String>();
	
//		this.player = player;
//		this.conversingNPC = conversingNPC;
//		
//		for(Node rootNode : rootNodes)
//		{
//			if(rootNode.getObjectID().equalsIgnoreCase(conversingNPC.getName()))
//			{
//				//npc has dialogue tree
//				inMenu = true;
//				currentDialogueNode = rootNode;
//				rowSelectionIndex = 0;
//				showIndex = 0;
//				return true;
//			}
//		}
//		return false;
	}
	
	public void closeInventory()
	{
		inMenu = false;
	}
	
	public boolean acceptPlayerInput(String key) //returns true if dialogue ended
	{
		if(key.equals("ENTER"))
		{		
			//PERFORM ACTION ON ITEM SELECTED
			// E.G.   EQUIP BOW, SWORD, SHIELD
			// USE MISC ITEM
			
//			if(currentDialogueNode.getNumChildren() > 0 )
//			{
//				currentDialogueNode = currentDialogueNode.getChild(choiceIndex);
//			}
//			else
//			{
//				inMenu = false;
//				return true;
//			}
//			
//			//activate trigger for this child
//			
//			responses.clear();
//			npcStatement = currentDialogueNode.getDialogue();
//			for(int i = 0; i < currentDialogueNode.getNumChildren(); i++)
//			{
//				responses.add(currentDialogueNode.getChild(i).getDialogue());
//			}
//			choiceIndex = 0;
//			showIndex = 0;
		}
		else if(key.equals("UP"))
		{
			if(rowSelectionIndex > 0)
			{
				rowSelectionIndex--;
				colSelectionIndex = 0;
				//if(rowSelectionIndex > 1 && (showIndex > 0)) showIndex--;
			}
			
		}
		else if(key.equals("DOWN"))
		{
			if(rowSelectionIndex < NUM_ITEM_ROWS-1)
			{
				rowSelectionIndex++;
				colSelectionIndex = 0;
			}
//			if(rowSelectionIndex < currentDialogueNode.getNumChildren()-1) 
//			{
//				rowSelectionIndex++;
//				if((rowSelectionIndex > 2) && (showIndex < currentDialogueNode.getNumChildren()-3))
//				{
//					showIndex++;
//				}
//					
//			}
		}
		else if(key.equals("RIGHT"))
		{
			colSelectionIndex ++;
			
		}
		else if(key.equals("LEFT"))
		{
		
			if(colSelectionIndex > 0)
			{
				colSelectionIndex --;			
			}
		}
		
		
		System.out.println("current row: "+ rowSelectionIndex);
		System.out.println("current col: "+colSelectionIndex);
		//System.out.println("showIndex: "+showIndex);
		return false;
	}
	
	
	public void render(SpriteBatch batch, OrthographicCamera camera)
	{
		Sprite equippedWeapon = player.getEquippedMeleeWeapon().getItemSpriteUp();
		Image equippedWeaponImage = new Image(equippedWeapon);
		equippedWeaponImage.scale(1.6f);
		//initialize resources
		LabelStyle style = new LabelStyle(menuFont, Color.DARK_GRAY);
		Label label1 = new Label("left pane", style);
		Label label2 = new Label("right pane",style);
		Label label3 = new Label("test1", style);
		Label label4 = new Label("test2", style);

		//add actors
		mainTable.setFillParent(true);
		mainTable.setBackground(new TextureRegionDrawable(new TextureRegion(texture)));
		stage.addActor(mainTable);
		testTable.setBackground(new TextureRegionDrawable(new TextureRegion(texture2)));
		testTable.add(equippedWeaponImage);
		mainTable.add(testTable);
		mainTable.add(equippedWeaponImage);
		    mainTable.add(label1).expandX();
		    mainTable.add(label2).width(200);
		    mainTable.row();
		    mainTable.add(label3);
		    mainTable.add(label4).width(100);
		    
		
		//draw
		mainTable.debug(); // turn on all debug lines (table, cell, and widget)
	    mainTable.debugTable(); // turn on only table lines
	    stage.draw();
	    Table.drawDebug(stage);
		mainTable.clear();
		

	   
		
		float desiredY = (camera.viewportHeight);
		
		float dialoguePositionX = camera.position.x - camera.viewportWidth / 2;
		float dialoguePositionY = camera.position.y - camera.viewportHeight / 2;
		
		float dialogueWidth = camera.viewportWidth;
		//float dialogueHeight = (camera.viewportHeight - 8*desiredX);;
		float dialogueHeight = camera.viewportHeight;
	
		
	//	Label objectName = new Label(conversingNPC.getName() , style);
		//Label npcStatement1 = new Label(npcStatement, style);
		
	//	int selectedIndex = rowSelectionIndex - showIndex;
				
//		if((currentDialogueNode.getNumChildren()-1) >= showIndex)
//		{
//			response1.setText(responses.get(showIndex));
//		}
//		if((currentDialogueNode.getNumChildren()-1) >= showIndex+1)
//		{
//			response2.setText(responses.get(showIndex+1));
//
//		}
//		if((currentDialogueNode.getNumChildren()-1) >= showIndex+2)
//		{
//			response3.setText(responses.get(showIndex+2));
//		}
		
		//response1.setStyle(separatorStyle);
//		if(selectedIndex == 0)
//		{
//			response1.setStyle(selectedStyle);
//		}
//		else if(selectedIndex == 1)
//		{
//			response2.setStyle(selectedStyle);
//		}
//		else
//		{
//			response3.setStyle(selectedStyle);
//		}
//		
//		npcStatement1.setWrap(true);
		
//		System.out.println("camera position x "+camera.position.x);
//		System.out.println("camera position y "+camera.position.y);
//		
//		System.out.println("dialogue position x: "+dialoguePositionX);
//		System.out.println("dialogue position y: "+dialoguePositionY);
	    
		
//	    table.setBounds(dialoguePositionX, dialoguePositionY, dialogueWidth, dialogueHeight);
//	    
//	    //table.add(objectName).width(dialogueWidth*0.9f);
//	    table.row();
//	   // table.add(npcStatement1).width(dialogueWidth*0.9f);
//	    table.row();
//	    table.add(separator).width(dialogueWidth);
//	    table.row();
//	    table.add(response1).width(dialogueWidth*0.9f);
//	    table.row();
//	    table.add(response2).width(dialogueWidth*0.9f);
//	    table.row();
//	    table.add(response3).width(dialogueWidth*0.9f);
//	    table.row();
//	    
//	    //table.setScale(0.5f);
//	    table.debug();
//		stage.addActor(table);
//		
//	 	batch.draw(texture, dialoguePositionX, dialoguePositionY, dialogueWidth, dialogueHeight);
//	
//		table.draw(batch, 1.0f);
//		table.clear();
		//Table.drawDebug(stage);
	}
	
	public boolean getInMenu()
	{
		return inMenu;
	}
}
