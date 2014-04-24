package com.me.rpg.maps;

import java.io.IOException;
import java.io.ObjectInputStream;

import com.me.rpg.World;

public class PathfindingExampleMap extends Map
{

	private static final long serialVersionUID = 5399987406900496825L;

	public static final String MAP_TMX_PATH = "maps/example/pathfinding_example.tmx";

	public PathfindingExampleMap(World world)
	{
		super(world, MapType.PATHFINDING, MAP_TMX_PATH);
	}

	private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException
	{
		inputStream.defaultReadObject();
		setup();
	}

}
