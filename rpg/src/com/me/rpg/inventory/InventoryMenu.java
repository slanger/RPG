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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.me.rpg.World;
import com.me.rpg.characters.GameCharacter;
import com.me.rpg.characters.PlayableCharacter;
import com.me.rpg.combat.Equippable;
import com.me.rpg.combat.MeleeWeapon;
import com.me.rpg.combat.Projectile;

public class InventoryMenu implements Serializable
{

	private static final long serialVersionUID = -4102496331718548744L;
	
	//transients for saving
	
	private transient Stage stage;
	
	private World world;
	
	private transient BitmapFont menuFont;
	
	private transient Table mainTable;
	private transient Table topPane;
	private transient Table bottomPane;
	private transient Table meleeWeaponRow;
	private transient Table arrowsRow;
	private transient Table shieldRow;
	private transient Table miscItemRow;
		
	private transient ArrayList<Table> meleeWeaponTables;
	private transient ArrayList<Table> arrowTables;
	private transient ArrayList<Table> shieldTables;
	private transient ArrayList<Table> miscItemTables;
	
	private transient Texture mainTableTexture;
	private transient Texture testTexture;
	private transient Texture itemRowTexture;
	private transient Texture itemSelectionTexture;
	
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
	private final int NUM_ITEMS_DISPLAYED = 8;
	
	private ArrayList<Equippable> meleeWeapons;  //first row
	private ArrayList<Projectile> arrows;   //second row
	
	private ArrayList<String> shields;       //third row
	private ArrayList<String> miscItems; 	 //fourth row
	
	public InventoryMenu()
	{
		createTransients();
		initializeInventoryMenu();
	}
	
	public void initializeInventoryMenu()
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
		topPane = new Table();
		bottomPane = new Table();
		meleeWeaponRow = new Table();
		arrowsRow = new Table();
		shieldRow = new Table();
		miscItemRow = new Table();
	
		meleeWeaponTables = new ArrayList<Table>();
		arrowTables = new ArrayList<Table>();
		shieldTables = new ArrayList<Table>();
		miscItemTables = new ArrayList<Table>();

		//initialize the 8 tables in each row
		for(int i=0; i<NUM_ITEMS_DISPLAYED; i++)
		{
			Table temp = new Table();
			meleeWeaponTables.add(temp);
			temp = new Table();
			arrowTables.add(temp);
			temp = new Table();
			shieldTables.add(temp);
			temp = new Table();
			miscItemTables.add(temp);
		}
		
		mainTableTexture = new Texture(Gdx.files.internal("images/inventory_menu_background.png"));
		testTexture = new Texture(Gdx.files.internal("images/inventory_menu_item_background.png"));
		itemRowTexture = new Texture(Gdx.files.internal("images/inv_menu_row_background.png"));
		itemSelectionTexture = new Texture(Gdx.files.internal("images/selected_item.png"));
	}
	
	private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException
	{
		inputStream.defaultReadObject();
		createTransients();
	}
	
	public void openInventory(PlayableCharacter player)
	{
		this.player = player;
		meleeWeapons = player.getEquippableItems();
		arrows = player.getArrows();
		
		inMenu = true;
		rowSelectionIndex = 0;
		colSelectionIndex = 0;
		
		
	}
	
	public void closeInventory()
	{
		inMenu = false;
	}
	
	public boolean acceptPlayerInput(String key) //returns true if dialogue ended
	{
		if(key.equals("ENTER"))
		{		
			
		}
		else if(key.equals("UP"))
		{
			if(rowSelectionIndex > 0)
			{
				rowSelectionIndex--;
				colSelectionIndex = 0;
			}
			
		}
		else if(key.equals("DOWN"))
		{
			if(rowSelectionIndex < NUM_ITEM_ROWS-1)
			{
				rowSelectionIndex++;
				colSelectionIndex = 0;
			}
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
		Sprite bowWeapon = player.getEquippedMeleeWeapon().getItemSpriteUp();
		Sprite meleeWeapon = player.getEquippedWeapon().getItemSpriteUp();
		
		//equippedWeaponImage.scale(1.6f);
		//initialize resources
		LabelStyle style = new LabelStyle(menuFont, Color.DARK_GRAY);
		Label label1 = new Label("left pane", style);
		Label label2 = new Label("right pane",style);
		Label label3 = new Label("test row", style);
		//Label label4 = new Label("test2", style);

		//Set up MainTable, contains everything else
		stage.addActor(mainTable);
		mainTable.setFillParent(true);
		mainTable.setBackground(new TextureRegionDrawable(new TextureRegion(mainTableTexture)));
		mainTable.top().left().pad(10.0f);
		
		//Top pane will contain selected item, with stats on it
		topPane.setBackground(new TextureRegionDrawable(new TextureRegion(testTexture)));
		topPane.pad(10.0f);
		topPane.add(label1);
		mainTable.add(topPane).expandX().fill().height(250.0f);
		
		mainTable.row();
		
		//bottom pane will contain the 4 rows of selectable items
		bottomPane.pad(10.0f);
		bottomPane.setBackground(new TextureRegionDrawable(new TextureRegion(testTexture)));
		mainTable.add(bottomPane).expandY().fill();
		
		//adding rowTables
		
		meleeWeaponRow.setBackground(new TextureRegionDrawable(new TextureRegion(itemRowTexture)));
		bottomPane.add(meleeWeaponRow).expandX().fill().pad(10.0f);
		
		bottomPane.row();
		
		arrowsRow.setBackground(new TextureRegionDrawable(new TextureRegion(itemRowTexture)));
		bottomPane.add(arrowsRow).expandX().fill().pad(10.0f);
		
		bottomPane.row();
		
		
		shieldRow.setBackground(new TextureRegionDrawable(new TextureRegion(itemRowTexture)));
		bottomPane.add(shieldRow).expandX().fill().pad(10.0f);
		
		bottomPane.row();
		
		
		miscItemRow.setBackground(new TextureRegionDrawable(new TextureRegion(itemRowTexture)));
		bottomPane.add(miscItemRow).expandX().fill().pad(10.0f);
		
		//Adding items to individual rows
		
		//weapon row
		meleeWeaponRow.left().padLeft(20.0f);
		for(int i=0; i<NUM_ITEMS_DISPLAYED; i++)
		{
			meleeWeaponRow.add(meleeWeaponTables.get(i)).size(50.0f).pad(10.0f);
			meleeWeaponTables.get(i).setBackground(new TextureRegionDrawable(new TextureRegion(itemSelectionTexture)));
			
			MeleeWeapon temp = null;
			if(meleeWeapons.size() > i)
			{
				temp = (MeleeWeapon) meleeWeapons.get(i);
				meleeWeaponTables.get(i).add(new Image(temp.getItemSpriteUp())).expand().fill();
			}
			else
			{
				
			}
			meleeWeaponTables.get(i).debug();
		}
		
		arrowsRow.left().padLeft(20.0f);
		for(int i=0; i<NUM_ITEMS_DISPLAYED; i++)
		{
			arrowsRow.add(arrowTables.get(i)).size(50.0f).pad(10.0f);
			arrowTables.get(i).setBackground(new TextureRegionDrawable(new TextureRegion(itemSelectionTexture)));
			
			Projectile temp = null;
			if(arrows.size() > i)
			{
				temp = arrows.get(i);
				arrowTables.get(i).add(new Image(temp.getItemSpriteUp())).expand().fill();
			}
			else
			{
				
			}
			arrowTables.get(i).debug();
		}
		
		shieldRow.left().padLeft(20.0f);
		for(int i=0; i<NUM_ITEMS_DISPLAYED; i++)
		{
			shieldRow.add(shieldTables.get(i)).size(50.0f).pad(10.0f);
			shieldTables.get(i).setBackground(new TextureRegionDrawable(new TextureRegion(itemSelectionTexture)));
			shieldTables.get(i).add(new Image(bowWeapon)).expand().fill();
			shieldTables.get(i).debug();
		}
		
		miscItemRow.left().padLeft(20.0f);
		for(int i=0; i<NUM_ITEMS_DISPLAYED; i++)
		{
			miscItemRow.add(miscItemTables.get(i)).size(50.0f).pad(10.0f);
			miscItemTables.get(i).setBackground(new TextureRegionDrawable(new TextureRegion(itemSelectionTexture)));
			miscItemTables.get(i).add(new Image(bowWeapon)).expand().fill();
			miscItemTables.get(i).debug();
		}
		
		
		
		mainTable.debug(); // turn on all debug lines (table, cell, and widget)
		topPane.debug();
		bottomPane.debug();
		meleeWeaponRow.debug();
		arrowsRow.debug();
		shieldRow.debug();
		miscItemRow.debug();

	    stage.draw();
	    Table.drawDebug(stage);
	    
		mainTable.clear();
		topPane.clear();
		bottomPane.clear();
		meleeWeaponRow.clear();
		arrowsRow.clear();
		shieldRow.clear();
		miscItemRow.clear();

		for(int i=0; i<NUM_ITEMS_DISPLAYED; i++)
		{
			meleeWeaponTables.get(i).clear();
			arrowTables.get(i).clear();
			shieldTables.get(i).clear();
			miscItemTables.get(i).clear();
		}
		
	}
	
	public boolean getInMenu()
	{
		return inMenu;
	}
}
