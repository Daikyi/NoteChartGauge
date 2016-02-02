package com.daikyi.rhythmgauge.timing;

import java.util.*;

public abstract class Chart {

	private double difficulty;
	protected ArrayList<Bar> structure;
	protected ArrayList<NPS> nps;
	
	public Chart(){
		
		structure = new ArrayList<Bar>();
	}
	
	public double getDifficulty(){
		
		return difficulty;
	}
	
	public void setDifficulty(double difficulty){
		
		this.difficulty = difficulty;
	}
	
	public ArrayList<Bar> getStructure(){
		
		return structure;
	}
	
	public ArrayList<NPS> getNPS(){
		
		return nps;
	}
	public abstract void calcNPS();
	public abstract void stripBlankBars();
	public abstract void addToStructure(ArrayList<String> measure, int measureCount);
}
