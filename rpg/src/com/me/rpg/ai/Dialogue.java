package com.me.rpg.ai;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.me.rpg.ScreenHandler;
import com.me.rpg.characters.GameCharacter;
import com.me.rpg.utils.Direction;

public class Dialogue
{
	private BitmapFont dialogueFont;

	private SpriteBatch batch;
	private OrthographicCamera camera;

	private boolean inDialogue;
	
	
	//new stuff
	private Stage stage;
	private Table table;
	private SpriteBatch spriteBatch;
	private BitmapFont white;
	private Label heading;
	private ShapeRenderer shapeRenderer;
	
	//end new stuff

	public Dialogue()
	{

	}

	public void setInDialogue(boolean inDialogue)
	{
		this.inDialogue = inDialogue;
	}

	public boolean getInDialogue()
	{
		return inDialogue;
	}

////	public void advanceDialogue(String key)
//	{
//		int temp2;
//		if (currentIndex < 0)
//			currentIndex = 0;
//		if (currentIndex < lastIndex)
//		{
//			currentText = dialogueArray[currentIndex];
//			int temp = playerResponsePosition[currentIndex];
//			if (temp != 0)
//				requireResponse = true;
//			if (requireResponse == true)
//			{
//				if (key == "NUM_1" && temp > 0)
//				{
//					requireResponse = false;
//					currentIndex += 1;
//				}
//				else if (key == "NUM_2" && temp > 1)
//				{
//					requireResponse = false;
//					currentIndex += 2;
//				}
//				else if (key == "NUM_3" && temp > 2)
//				{
//					requireResponse = false;
//					currentIndex += 3;
//				}
//				else
//				{
//					// index stays same until response
//				}
//			}
//			else
//			{
//				if (key == "E")
//					currentIndex++;
//				else
//				{
//					// index stays same until E hit.
//				}
//			}
//			if (currentIndex + 1 > lastIndex)
//			{
//				temp2 = 0;
//			}
//			else
//			{
//				temp2 = playerResponsePosition[currentIndex + 1];
//			}
//			if (temp2 != 0)
//			{ // request player response
//				requireResponse = true;
//			}
//
//		}
//		else
//		{
//			System.out.println(lastIndex);
//			requireResponse = false;
//			currentText = "";
//			inDialogue = false;
//		}
//	}

	public void render(SpriteBatch batch, OrthographicCamera camera, ShapeRenderer shapeRenderer)
	{
		this.batch = batch;
		this.camera = camera;
		this.shapeRenderer = shapeRenderer;
		
		//stage = new Stage();
		
		float desiredX = (camera.viewportWidth * 0.1f);
		float desiredY = (camera.viewportHeight * 0.9f);
		
		
		
		float dialoguePositionX = camera.position.x - camera.viewportWidth / 2 + desiredX;
		float dialoguePositionY = camera.position.y + camera.viewportHeight / 2 - desiredY;
		
		System.out.println(dialoguePositionX);
		System.out.println(dialoguePositionY);
		
		
		float dialogueWidth = (camera.viewportWidth - 2*desiredX);
		//float dialogueHeight = (camera.viewportHeight - 8*desiredX);;
		float dialogueHeight = 250.0f;
		
		Texture texture = new Texture(Gdx.files.internal("images/DialogueBackground.png"));

		table = new Table();

		dialogueFont = new BitmapFont();
		
		LabelStyle style = new LabelStyle(dialogueFont, Color.WHITE);
		LabelStyle selectedStyle = new LabelStyle(dialogueFont, Color.BLUE);
		LabelStyle separatorStyle = new LabelStyle(dialogueFont, Color.GRAY );
		
//---------------------------------------------------------------------------------	
		Label objectName = new Label("John" , style);
		Label npcStatement1 = new Label("NPC statement ------ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd---", style);
		Label npcStatement2 = new Label("NPC statement---------", selectedStyle);
		Label separator = new Label("------------------------------------------------------------------"
				+ "------------------------------------------------", separatorStyle);
//---------------------------------------------------------------------------------
		Label response1 = new Label("Response1 -------------- ", style);
		Label response2 = new Label("Response2 ----------------", style);
		Label response3 = new Label("Response2 ---------------", style);
		
		npcStatement1.setWrap(true);
		
	    Table table = new Table();
	    table.setBounds(dialoguePositionX, dialoguePositionY, dialogueWidth, dialogueHeight);
	    
	    table.add(objectName).width(dialogueWidth*0.9f);
	    table.row();
	    table.add(npcStatement1).width(dialogueWidth*0.9f);
	    table.row();
	    table.add(separator).width(dialogueWidth);;
	    table.row();
	    table.add(response1).width(dialogueWidth*0.9f);;
	    table.row();
	    table.add(response2).width(dialogueWidth*0.9f);;
	    table.row();
	    table.add(response3).width(dialogueWidth*0.9f);;
	    table.row();
	    
	    table.debug();
		//stage.addActor(table);
		
	 	batch.draw(texture, dialoguePositionX, dialoguePositionY, dialogueWidth, dialogueHeight);
	
		table.draw(batch, 1.0f);
		//Table.drawDebug(stage);
	}
	
}
