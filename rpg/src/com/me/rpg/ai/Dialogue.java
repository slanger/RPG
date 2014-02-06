package com.me.rpg.ai;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.Character;
import com.me.rpg.Coordinate;
import com.me.rpg.World;
import com.me.rpg.maps.Map;

public class Dialogue {
	private BitmapFont dialogueFont;
	
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private World world;
	private Map map;
	private ArrayList<Character> charactersOnMap;
	private Character currentCharacter;
	
	//Planning on converting this array system to a tree implementation soon
	private int playerResponsePosition[];  //index locations that require response will have int > 0. The int is how many choices
	private String dialogueArray[]; //this array will store 
	private int currentIndex;
	private int lastIndex;
	private String currentText;//the dialogue at the position in the arrays
	private int numOptions;//the number of options for response.
	private boolean requireResponse=false;//does the player have to select an option
	private boolean inDialogue;

	
	public Dialogue(World world, SpriteBatch batch, OrthographicCamera camera)
	{
		this.world=world;
		this.batch=batch;
		this.camera=camera;
		
		currentText="";
		playerResponsePosition=new int[100];
		dialogueArray = new String[100];
		
		dialogueFont = new BitmapFont();
		dialogueFont.setColor(1.0f, 1.0f, 1.0f, 1f); // white
	}
	public void setInDialogue(boolean inDialogue)
	{
		this.inDialogue=inDialogue;
	}
	public boolean getInDialogue()
	{
		return inDialogue;
	}
	public int getNumResponseOptions()
	{
		return playerResponsePosition[currentIndex];
	}
	public boolean getRequireResponse()
	{
		return requireResponse;
	}
	public void update(Character character)
	{
		currentIndex=-1;
		System.out.println(character.getName());

		switch(character.getName())
		{
			case "NPC1":
				dialogueArray[0]="Hi, im npc1!";
				playerResponsePosition[0]=0;
				dialogueArray[1]="Anyway, nice to meet you!";
				playerResponsePosition[1]=0;
				dialogueArray[2]="";
				playerResponsePosition[2]=0;
				lastIndex=2;
				break;
			case "NPC2":
				dialogueArray[0]="What weapon do you like to use?";
				playerResponsePosition[0]=0;
				dialogueArray[1]="1. Sword    2. Bow    3. Axe";
				playerResponsePosition[1]=3;
				dialogueArray[2]="I like swords too";
				playerResponsePosition[2]=0;
				dialogueArray[3]="You must be a nice archer then";
				playerResponsePosition[3]=0;
				dialogueArray[4]="Never liked axes much";
				playerResponsePosition[4]=0;
				dialogueArray[5]="";
				playerResponsePosition[5]=0;
				lastIndex=5;
				break;
			case "NPC3":
				dialogueArray[0]="Go away";
				playerResponsePosition[0]=0;
				dialogueArray[1]="";
				playerResponsePosition[1]=0;
				lastIndex=1;
				break;
			default: break;
					
		}
		
//		charactersOnMap = world.getMap().getCharactersOnMap();
//		Iterator<Character> iterator = charactersOnMap.iterator();
//		while (iterator.hasNext())
//		{
//			Character currentCharacter = iterator.next();
//			
//		}
		
	}
	
	public void advanceDialogue(String key)
	{
		int temp2;
		if(currentIndex <0) currentIndex=0;
		if(currentIndex < lastIndex)
		{
			currentText = dialogueArray[currentIndex];
			int temp=playerResponsePosition[currentIndex];
			if(temp != 0) requireResponse=true;
			if(requireResponse==true)
			{
				if(key=="NUM_1" && temp>0) 
				{
					requireResponse=false;
					currentIndex+=1;
				}
				else if(key=="NUM_2" && temp>1)
				{
					requireResponse=false;
					currentIndex+=2;
				}
				else if(key=="NUM_3" && temp>2)
				{ 
					requireResponse=false;
					currentIndex+=3;
				}
				else{
					//index stays same until response
				}		
			}
			else{
				if(key=="E") currentIndex++;
				else
				{
					//index stays same until E hit.
				}
			}
			if(currentIndex+1 > lastIndex)
			{
				temp2 = 0;
			}
			else
			{
				temp2 = playerResponsePosition[currentIndex+1];
			}
			if(temp2!=0)
			{ //request player response
				requireResponse=true;
			}
			
		}
		else{
			System.out.println(lastIndex);
			requireResponse=false;
			currentText="";
			inDialogue=false;
		}
	}
	
	public void render()
	{
		float dialogueTextX = camera.position.x - camera.viewportWidth /2 +30;
		float dialogueTextY = camera.position.y + camera.viewportHeight / 2 - 350;
		dialogueFont.draw(batch, currentText,dialogueTextX, dialogueTextY);
	}
}
