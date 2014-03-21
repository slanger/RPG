package com.me.rpg.maps;

public enum MapType
{

	EXAMPLE					("example", 0),
	PROTOTYPE				("prototype_map", 1),
	WEST_TOWN				("west_town", 2),
	WEST_TOWN_INSIDE_HOUSE	("west_town_inside_house", 3);

	private String mapName;
	private int index;

	public String getMapName()
	{
		return mapName;
	}

	public int getMapIndex()
	{
		return index;
	}

	private MapType(String mapName, int index)
	{
		this.mapName = mapName;
		this.index = index;
	}

	public static MapType getMapType(String mapName)
	{
		for (MapType mapType : MapType.values())
		{
			if (mapType.getMapName().equals(mapName))
			{
				return mapType;
			}
		}
		throw new RuntimeException(String.format(
				"Could not find the Map with name: %s", mapName));
	}

}
