package com.me.rpg.maps;

import java.io.IOException;
import java.io.ObjectInputStream;

import com.me.rpg.World;

public class RagnarokMap extends Map
{

	private static final long serialVersionUID = -9024283515215913361L;
	public static final String MAP_TMX_PATH = "maps/ragnarok_map/ragnarok_map.tmx";

	public RagnarokMap(World world)
	{
		super(world, MapType.RAGNAROK, MAP_TMX_PATH);
	}

	private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException
	{
		inputStream.defaultReadObject();
		setup();
	}

}
