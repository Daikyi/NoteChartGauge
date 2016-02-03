package com.daikyi.rhythmgauge.difficulty;

import java.util.ArrayList;

import com.daikyi.rhythmgauge.timing.Bar;
import com.daikyi.rhythmgauge.timing.Chart;
import com.daikyi.rhythmgauge.timing.NPS;
import com.daikyi.rhythmgauge.timing.Note;
import com.daikyi.rhythmgauge.timing.SMBar;
import com.daikyi.rhythmgauge.timing.SMChart;
import com.daikyi.rhythmgauge.timing.SMNote;

public class SMDiffRater extends DiffRater{

	private ArrayList<SMChart> columns;
	private SMChart chart;
	
	public SMDiffRater(SMChart chart){
		
		columns = new ArrayList<SMChart>();
		this.chart = chart;
		ArrayList<Bar> struct = chart.getStructure();
		
		//process all the notes into 2 columns
		for(int i = 0; i < 4; i++)
			columns.add(new SMChart());
		
		for(int i = 0; i < struct.size(); i++){
			
			SMBar temp = (SMBar)struct.get(i);
			SMBar bar = new SMBar();
			for(int j = 0; j < temp.getNumNotes(); j++){
			
				SMNote toAdd = (SMNote)(temp.getNotes().get(j));
				bar.setTimeStamp(temp.getTimeStamp());
				bar.setNote(toAdd);
				columns.get(toAdd.getColumn()).addBar(bar);
			}
		}
	}
	
	@Override
	public ArrayList<Difficulty> getDiffSpectrum() {
		

		//check number of keys somehow?
		//if keys =4
		
		return null;
	}

	public ArrayList<ArrayList<Difficulty>> getColDiffs(){
		
		ArrayList<ArrayList<Difficulty>> toReturn = new ArrayList<ArrayList<Difficulty>>();
		toReturn.add(getColDiff(0));
		toReturn.add(getColDiff(1));
		toReturn.add(getColDiff(2));
		toReturn.add(getColDiff(3));
		return toReturn;
	}
	
	public ArrayList<Difficulty> getColDiff(int column){

		System.out.println(chart.getStructure().get(4).getTimeStamp());
		return calcDifficulty(columns.get(column));
	}
	
	private static ArrayList<Difficulty> calcDifficulty(SMChart toCalc){
		toCalc.calcNPS();
		ArrayList<NPS> nps = toCalc.getNPS();
		ArrayList<Difficulty> diff = new ArrayList<Difficulty>();
	
		for(int i = 0; i < nps.size(); i++){
			Difficulty toAdd = new Difficulty();
			toAdd.setDifficulty(nps.get(i).getValue());
			toAdd.setTimeStamp(nps.get(i).getTimeStamp());
			diff.add(toAdd);
		}
		return diff;
	}
}
