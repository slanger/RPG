package com.me.rpg.maps;

public enum MapType
{

	EXAMPLE		("example"),
	PROTOTYPE	("prototype_map"),
	WEST_TOWN	("west_town");

	private String mapName;

	public String getMapName()
	{
		return mapName;
	}

	private MapType(String mapName)
	{
		this.mapName = mapName;
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
