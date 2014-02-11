package com.me.rpg.reputation;

import java.util.ArrayList;

public class ReputationSystem {
	private static ArrayList<ReputationEvent> MasterEventList;
	private EventTemplate[] EventTemplateList = new EventTemplate[10];
	
	ReputationSystem()
	{
		initializeTemplates();
		setMasterEventList(new ArrayList<ReputationEvent>());
	}

	public static ArrayList<ReputationEvent> getMasterEventList() {
		return MasterEventList;
	}

	public static void setMasterEventList(ArrayList<ReputationEvent> masterEventList) {
		MasterEventList = masterEventList;
	}
	
	public void initializeTemplates()
	{
		EventTemplateList[0] = new EventTemplate("Attacked", 10);
		EventTemplateList[1] = new EventTemplate("Killed", 50);
		EventTemplateList[2] = new EventTemplate("Stole From", 5);
		EventTemplateList[3] = new EventTemplate("Completed Easy Quest For", 10);
		EventTemplateList[4] = new EventTemplate("Completed Medium Quest For", 30);
		EventTemplateList[5] = new EventTemplate("Completed Hard Quest For", 50);
	}
}
