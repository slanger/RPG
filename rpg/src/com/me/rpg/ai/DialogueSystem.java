package com.me.rpg.ai;

import java.io.File;
import java.io.FileNotFoundException;
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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.me.rpg.characters.GameCharacter;

public class DialogueSystem implements Serializable
{

	private static final long serialVersionUID = -4602444631718545740L;

	private Queue<Node> nodeStorage;
	private ArrayList<Node> rootNodes;
	
	private transient BitmapFont dialogueFont;
	private transient Stage debugStage;
	private transient Table table;
	private transient Texture texture;
	
	//per conversation stuff
	private boolean inDialogue = false;
	private Node currentDialogueNode = null;
	private GameCharacter player = null;
	private GameCharacter conversingNPC = null;
	private boolean finishedDialogue = false;
	private int choiceIndex;
	private int showIndex;
	private String npcStatement = null;
	private ArrayList<String> responses;
	
	public DialogueSystem()
	{
		//initialize renderer stuff
		dialogueFont = new BitmapFont();
		inDialogue = false;
		table = new Table();
		texture = new Texture(Gdx.files.internal("images/DialogueBackground.png"));
		
		initializeDialogueSystem();
	}
	
	public void initializeDialogueSystem()
	{
		nodeStorage = new LinkedList<Node>();
		rootNodes = new ArrayList<Node>();
		responses = new ArrayList<String>();
		readDialogueFile();
		
		while(nodeStorage.isEmpty() == false)
		{
			Node newRootNode = nodeStorage.poll(); 
			rootNodes.add(newRootNode);
			newRootNode.addChildNode(assembleTree(newRootNode));
		}
	}
	
	public boolean startConversation(GameCharacter player, GameCharacter conversingNPC)
	{
		this.player = player;
		this.conversingNPC = conversingNPC;
		
		for(Node rootNode : rootNodes)
		{
			if(rootNode.getObjectID().equalsIgnoreCase(conversingNPC.getName()))
			{
				//npc has dialogue tree
				inDialogue = true;
				currentDialogueNode = rootNode;
				choiceIndex = 0;
				showIndex = 0;
				return true;
			}
		}
		return false;
	}
	
	public boolean advanceDialogue(String key) //returns true if dialogue ended
	{
		if(key.equals("ENTER"))
		{		
			if(currentDialogueNode.getNumChildren() > 0 )
			{
				currentDialogueNode = currentDialogueNode.getChild(choiceIndex);
			}
			else
			{
				inDialogue = false;
				return true;
			}
			
			//activate trigger for this child
			
			responses.clear();
			npcStatement = currentDialogueNode.getDialogue();
			for(int i = 0; i < currentDialogueNode.getNumChildren(); i++)
			{
				responses.add(currentDialogueNode.getChild(i).getDialogue());
			}
			choiceIndex = 0;
			showIndex = 0;
		}
		else if(key.equals("UP"))
		{
			if(choiceIndex > 0)
			{
				choiceIndex--;
				if(choiceIndex > 1 && (showIndex > 0)) showIndex--;
			}
			
		}
		else if(key.equals("DOWN"))
		{
			if(choiceIndex < currentDialogueNode.getNumChildren()-1) 
			{
				choiceIndex++;
				if((choiceIndex > 2) && (showIndex < currentDialogueNode.getNumChildren()-3))
				{
					showIndex++;
				}
					
			}
		}
		System.out.println("choiceIndex: " +choiceIndex);
		System.out.println("showIndex: "+showIndex);
		return false;
	}
	
	
	public void render(SpriteBatch batch, OrthographicCamera camera)
	{
		//stage = new Stage();
		
		float desiredY = (camera.viewportHeight);
		
		float dialoguePositionX = camera.position.x - camera.viewportWidth / 2;
		float dialoguePositionY = camera.position.y - camera.viewportHeight / 2;
		
		float dialogueWidth = camera.viewportWidth;
		//float dialogueHeight = (camera.viewportHeight - 8*desiredX);;
		float dialogueHeight = 250.0f;
	
		LabelStyle style = new LabelStyle(dialogueFont, Color.WHITE);
		LabelStyle selectedStyle = new LabelStyle(dialogueFont, Color.BLUE);
		LabelStyle separatorStyle = new LabelStyle(dialogueFont, Color.GRAY );
		
////---------------------------------------------------------------------------------	
//		Label objectName = new Label("John" , style);
//		Label npcStatement1 = new Label("NPC statement ------ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd---", style);
//		Label separator = new Label("------------------------------------------------------------------"
//				+ "------------------------------------------------", separatorStyle);
////---------------------------------------------------------------------------------
//		Label response1 = new Label("Response1 -------------- ", style);
//		Label response2 = new Label("Response2 ----------------", style);
//		Label response3 = new Label("Response2 ---------------", style);
		
		
		Label objectName = new Label(conversingNPC.getName() , style);
		Label npcStatement1 = new Label(npcStatement, style);
		Label separator = new Label("------------------------------------------------------------------"
				+ "------------------------------------------------", separatorStyle);
		
		
		
		int selectedIndex = choiceIndex - showIndex;
		
		Label response1 = new Label("", style);
		Label response2 = new Label("", style);
		Label response3 = new Label("", style);
		
		if((currentDialogueNode.getNumChildren()-1) >= showIndex)
		{
			response1.setText(responses.get(showIndex));
		}
		if((currentDialogueNode.getNumChildren()-1) >= showIndex+1)
		{
			response2.setText(responses.get(showIndex+1));

		}
		if((currentDialogueNode.getNumChildren()-1) >= showIndex+2)
		{
			response3.setText(responses.get(showIndex+2));
		}
		
		//response1.setStyle(separatorStyle);
		if(selectedIndex == 0)
		{
			response1.setStyle(selectedStyle);
		}
		else if(selectedIndex == 1)
		{
			response2.setStyle(selectedStyle);
		}
		else
		{
			response3.setStyle(selectedStyle);
		}
		
		npcStatement1.setWrap(true);
		
//		System.out.println("camera position x "+camera.position.x);
//		System.out.println("camera position y "+camera.position.y);
//		
//		System.out.println("dialogue position x: "+dialoguePositionX);
//		System.out.println("dialogue position y: "+dialoguePositionY);
	    
		
	    table.setBounds(dialoguePositionX, dialoguePositionY, dialogueWidth, dialogueHeight);
	    
	    table.add(objectName).width(dialogueWidth*0.9f);
	    table.row();
	    table.add(npcStatement1).width(dialogueWidth*0.9f);
	    table.row();
	    table.add(separator).width(dialogueWidth);
	    table.row();
	    table.add(response1).width(dialogueWidth*0.9f);
	    table.row();
	    table.add(response2).width(dialogueWidth*0.9f);
	    table.row();
	    table.add(response3).width(dialogueWidth*0.9f);
	    table.row();
	    
	    //table.setScale(0.5f);
	    //table.debug();
		//stage.addActor(table);
		
	 	batch.draw(texture, dialoguePositionX, dialoguePositionY, dialogueWidth, dialogueHeight);
	
		table.draw(batch, 1.0f);
		table.clear();
		//Table.drawDebug(stage);
	}
	
	public void setInDialogue(boolean inDialogue)
	{
		this.inDialogue = inDialogue;
	}
	
	public boolean getInDialogue()
	{
		return inDialogue;
	}
	
	class Node implements Serializable
	{

		private static final long serialVersionUID = -8686936011407735399L;

		// root node only
		private String objectType;
		private String objectID;
		private String disposition;

		private String dialogue;
		private String trigger;
		private int numChildren;
		public ArrayList<Node> children;
		private Node parentNode;
		
		private boolean isRoot;
		
		public Node(String dialogue, String trigger, Node parentNode, int numChildren)
		{
			objectType = null;
			objectID = null;
			disposition = null;
			
			this.dialogue = dialogue;
			this.trigger = trigger;
			this.numChildren=numChildren;
			children = new ArrayList<Node>();
			this.parentNode = parentNode;
			
			isRoot = false;
		}
		
		public Node(String objectType, String objectID, String disposition) //root node
		{
			this.objectType = objectType;
			this.objectID = objectID;
			this.disposition = disposition;
			
			dialogue = null;
			trigger = null;
			numChildren = 1;
			children = new ArrayList<Node>();
			parentNode = null;
			
			isRoot = true;
		}
		
		public String getDialogue()
		{
			return dialogue;
		}
		
		public String getTrigger()
		{
			return trigger;
		}
		
		public int getNumChildren()
		{
			return numChildren;
		}
		
		public void addChildNode(Node childNode)
		{
			children.add(childNode);
		}
		
		public String getObjectType()
		{
			return objectType;
		}
		
		public String getObjectID()
		{
			return objectID;
		}
		
		public String getDisposition()
		{
			return disposition;
		}
		
		public boolean getIsRoot()
		{
			return isRoot;
		}
		
		public void setParentNode(Node parentNode)
		{
			this.parentNode = parentNode;
		}
		
		public Node getChild(int index)
		{
			return children.get(index);
		}
		
	}
	
	
	public Node assembleTree(Node parentNode)
	{
		Node currentNode = nodeStorage.poll(); 
		if(currentNode != null)
		{
			currentNode.setParentNode(parentNode);
			for(int i = 0; i < currentNode.getNumChildren(); i++)
			{
				Node nextNode = assembleTree(currentNode);
				// System.out.println("added node "+nextNode.getDialogue()+ " under "+currentNode.getDialogue());
				currentNode.addChildNode(nextNode);
			}
		}
		return currentNode;
	}
	
	public void readDialogueFile()
	{
		Node currentNode = null;
		Scanner scanner = null;
		try
		{
			scanner = new Scanner(new File("DialogueFile.txt"));
		}
		catch(FileNotFoundException error)
		{
			scanner = null;
			System.err.println("File DialogueFile.txt not found.");
		}
		if(scanner!=null)	
		{
			while(scanner.hasNext()){
				
				String line = scanner.nextLine();
				String delimiters = "[ ]+";
				String[] tokens = line.split(delimiters);
				for(int i = 0; i<tokens.length; i++)
				{
					tokens[i] = tokens[i].trim();
				}
				
				if(tokens[0].equalsIgnoreCase("NPC"))
				{
					String objectType = tokens[0];
					String objectID = tokens[1];
					String disposition = tokens[2];
					
					Node rootNode = new Node(objectType, objectID, disposition);
					nodeStorage.add(rootNode);
				}
				
				if(tokens[0].equalsIgnoreCase("responses"))
				{
					int numChildren = Integer.parseInt(tokens[1]);
					String trigger = tokens[3];
					String dialogue = "";
					for(int i = 5; i<tokens.length ;i++)
					{
						dialogue = dialogue + tokens[i]+" ";
					}
					Node newNode = new Node(dialogue, trigger, currentNode, numChildren);
					nodeStorage.add(newNode);
	
				}
			}
			scanner.close();
		}
		
	}
}
