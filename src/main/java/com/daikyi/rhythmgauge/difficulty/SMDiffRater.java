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

	private ArrayList<SMChart> columns;	//arraylist of the columns in chart form
	private SMChart chart;
	
	/*constants*/
	private static final double COL_NPS_HIGH = 11;	//
	private static final double CONV_K = .15; //these are used
	private static final double CONV_X = 24;  //for nps to 
	private static final double CONV_L = 100; //raw difficulty
	private static final double SPIKE_VAR = 10;	//spike difficulty modifier
	
	/**
	 * initializes the chart and multiple charts containing only the columns
	 * @param chart
	 */
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
	
	public double calculateChartDifficulty(){
		
		chart.calcNPS();	//make sure the chart has nps values
		ArrayList<NPS> masterNPS = chart.getNPS();
		double[] chartStats = deviationParse(masterNPS);
		Difficulty tempDiff = new Difficulty();
		//full chart calculation
			//using chart stats, generate 2 more NPS lists of the sections of the chart that are
			//above the upper standard deviation of the NPS, and
			//between the lower and upper standard deviations of the NPS
			ArrayList<NPS> upperOutliers = parseNPSBounds(masterNPS,chartStats[2]+chartStats[0],Double.MAX_VALUE, tempDiff);
			double[] upperStats = deviationParse(upperOutliers);
			double upperTime = tempDiff.getTimeStamp();
			
			ArrayList<NPS> mainChart = parseNPSBounds(masterNPS,chartStats[0]-chartStats[1],chartStats[0]+chartStats[2], tempDiff);
			double[] mainStats = deviationParse(mainChart);
			double mainTime = tempDiff.getTimeStamp();
	
			double mainRating = ((mainStats[0] +mainStats[2]+ mainStats[3]) / 2);// * (mainStats[4] / (mainStats[4] + upperStats[4])); 
			double upperRating = ((upperStats[0] +upperStats[2]+ upperStats[3]) / 2);// * (upperStats[4] / (mainStats[4] + upperStats[4]));
			
			//Tier 2 calculation will probably make spike factor calculation much more strong
			double spikeFactor = ((upperStats[0]+upperStats[2])/chartStats[0]) * (upperStats[4]/chartStats[4]);
			spikeFactor = 1+Math.pow(spikeFactor+upperTime/chartStats[4], SPIKE_VAR);

			
			double upperDiff = upperRating * upperStats[4]/(upperStats[4]+mainStats[4]);
			double lowerDiff = mainRating * (mainStats[4]/(upperStats[4]+mainStats[4]));
			double chartDiff = (upperDiff + lowerDiff)*spikeFactor;

			//System.out.println("|"+mainRating+"|"+upperRating+"|"+spikeFactor);
			//TODO stamina fudge
				//take only the upper chart for this calculation
			//System.out.println("main/upper/upperStat/chartdiff");
			//System.out.println(mainRating + " | " + upperRating + " | "+spikeDiff);
		//column calc
			
			double noteCount = 0;
			double[] colRatings = new double[columns.size()];
			int[] colNoteCount = new int[columns.size()];
			
			for(int i = 0; i < columns.size(); i++){
				
				SMChart column = columns.get(i);
				colNoteCount[i] = column.getStructure().size();
				noteCount += colNoteCount[i];
				colRatings[i] = getColumnUtil(column);
			}
			
			double colRating = 0;
			
			for(int i = 0; i < columns.size(); i++)
				colRating += colRatings[i] * (colNoteCount[i]/noteCount);	
			
			colRating *= columns.size();
		//hand based calc?
			//??????TODO
			SMChart left = new SMChart();
			left.setStructure(cheatChart(chart.getStructure(), true));
			SMChart right = new SMChart();
			right.setStructure(cheatChart(chart.getStructure(), false));
			
			double leftRating = getColumnUtil(left);
			double rightRating = getColumnUtil(right);
			
			int leftNoteCount = left.getStructure().size();
			int rightNoteCount = right.getStructure().size();
			int handNoteCount = leftNoteCount + rightNoteCount;
			double handRating = 3 * (leftRating * (((double)leftNoteCount)/handNoteCount) +
								rightRating * (((double)rightNoteCount)/handNoteCount));
			
		//System.out.print(chartDiff + " | " + colRating + " | ");
		
		double impact = Math.max(chartDiff,  colRating);
		double fudge = Math.min(chartDiff, colRating);
		double weirdNPS = ((impact - Math.sqrt(impact-fudge)) + handRating) / 2;
		
		//System.out.println("\nimpact/fudge/sipke: " + impact + " | " + fudge + "|" + spikeFactor);
		//System.out.println("finalDiff: " + npsToDiff(weirdNPS));
		//return npsToDiff(weirdNPS);
		return npsToDiff(weirdNPS);
	}
	
	private static double getColumnUtil(SMChart column){

		Difficulty tempDiff = new Difficulty();
		column.calcNPS();	//make sure the chart has nps values
		ArrayList<NPS> masterNPS = column.getNPS();
		double[] chartStats = deviationParse(masterNPS);
		
		ArrayList<NPS> mainChart = parseNPSBounds(masterNPS,chartStats[0],Double.MAX_VALUE,tempDiff);
		double[] mainStats = deviationParse(mainChart);
		double mainTime = tempDiff.getTimeStamp();
		
		double rating = (mainStats[0] + mainStats[2] + mainStats[3]) / 2;
		
		/*for column calc, will need to examine note to note relationships probably
		
		double upperDiff = upperRating * upperStats[4]/(upperStats[4]+mainStats[4]);
		double lowerDiff = mainRating * (mainStats[4]/(upperStats[4]+mainStats[4]));
		double calcDiff = (upperDiff + lowerDiff)*spikeFactor;
		TODO make sure that this is revisited to seek out minijacks and stuff?*/
		return rating;
	}
	
	/**
	 * Takes raw nps and converts to a "difficulty rating"
	 * @param nps
	 * @return
	 */
	public static double npsToDiff(double nps){
		return CONV_L / (1 + Math.exp(-1 * CONV_K * (nps - CONV_X)));
	}

	public ArrayList<ArrayList<Difficulty>> getColDiffs(){
		
		ArrayList<ArrayList<Difficulty>> toReturn = new ArrayList<ArrayList<Difficulty>>();
		//toReturn.add(getColDiff(0));
		//toReturn.add(getColDiff(1));
		//toReturn.add(getColDiff(2));
		//toReturn.add(getColDiff(3));
		
		toReturn.add(getChartNPS());
		
		return toReturn;
	}

	public ArrayList<Difficulty> getChartNPS(){

		ArrayList<Difficulty> toReturn = new ArrayList<Difficulty>();
		for(int i = 0; i < chart.getNPS().size(); i++){
			Difficulty toAdd = new Difficulty();
			NPS toConv = chart.getNPS().get(i);
			double newDiff = toConv.getValue();//npsToDiff(toConv.getValue());
			toAdd.setDifficulty(newDiff);
			toAdd.setTimeStamp(toConv.getTimeStamp());
			toReturn.add(toAdd);
		}
		
		return toReturn;
	}
	/*
	public double getDifficulty(){
		
		chart.calcNPS();
		ArrayList<NPS> nps = chart.getNPS();
		//ArrayList<Difficulty> diff = npsToRawDifficulty(nps);
		double[] stat = deviationParse(nps);
		
		//ArrayList<NPS> upperOutliers = parseNPSBounds(nps,stat[2]+stat[0],Double.MAX_VALUE);
		//ArrayList<NPS> mainChart = parseNPSBounds(nps,stat[0]-stat[1],stat[0]+stat[2]);
		
		//double upperAvg = avgNPS(upperOutliers);
		//double mainAvg = avgNPS(mainChart);
		
		//System.out.println("avg, minstddev, maxstddev, mainavg, upperavg");
		//System.out.println(stat[0] + " " + stat[1] + " " + stat[2] + " " + mainAvg + " " + upperAvg);
		//System.out.println(npsToDiff(stat[0]) + " xxx xxx " + npsToDiff(mainAvg) + " " + npsToDiff(upperAvg));
		
		//double npsNorm = ((stat[0] + 2*stat[2] + mainAvg + upperAvg) / 3);
		
		//double fakeDiff = (.75*npsToDiff(upperAvg) + npsToDiff(mainAvg) + 1.25*npsToDiff(stat[3]))/3;
		//System.out.println("est diff/highdiff - " + fakeDiff + ", " + npsToDiff(stat[3]));
		
		//System.out.println("Average Difficulty:" + avgDiff(toReturn));
		//return fakeDiff;
	}*/
	
	private static ArrayList<Difficulty> npsToRawDifficulty(ArrayList<NPS> nps){
		
		ArrayList<Difficulty> diff = new ArrayList<Difficulty>();
		
		//populate difficulty with nps
		for(int i = 0; i < nps.size(); i++){
			Difficulty toAdd = new Difficulty();
			toAdd.setDifficulty(npsToDiff(nps.get(i).getValue()));
			toAdd.setTimeStamp(nps.get(i).getTimeStamp());
			diff.add(toAdd);
		}
		return diff;
	}
	
	/**
	 * Finds the chart's average NPS given an ArrayList of nps values
	 * @param nps
	 * @return
	 */
	private static double avgNPS(ArrayList<NPS> nps){
		
		if(nps.size() == 0)
			return 0;
		
		double avgNPS = 0;
		double prevTimeStamp = 0;
		//get avg. nps
		for(int i = 0; i < nps.size(); i++){

			avgNPS += (nps.get(i).getValue() * (nps.get(i).getTimeStamp()-prevTimeStamp));
			prevTimeStamp = nps.get(i).getTimeStamp();
		}
		return avgNPS/(prevTimeStamp-nps.get(0).getTimeStamp());
	}
	
	/**
	 * returns a new arraylist of NPS that represents a chart that contains only the NPS values in 
	 * the given bound specified
	 * @param nps
	 * @param lowerBound
	 * @param upperBound
	 * @return
	 */
	private static ArrayList<NPS> parseNPSBounds(ArrayList<NPS> nps, double lowerBound, double upperBound, Difficulty lol){
		
		ArrayList<NPS> toReturn = new ArrayList<NPS>();
		double timePos = 0;
		double actualPos = 0;
		double recordTime = 0;
		double lolo = 0;
		for(int i = 0; i < nps.size(); i++){
			if(nps.get(i).getValue() >= lowerBound && nps.get(i).getValue() <= upperBound){
				
				NPS toAdd = new NPS();
				toAdd.setValue(nps.get(i).getValue());
				timePos += nps.get(i).getTimeStamp() - actualPos;
				toAdd.setTimeStamp(timePos);
				toReturn.add(toAdd);
				recordTime += nps.get(i).getTimeStamp() - actualPos;
			}
			else if(recordTime > lolo){
				lolo = recordTime;
				recordTime = 0;
			}
			actualPos = nps.get(i).getTimeStamp();
		}
		lol.setTimeStamp(recordTime);
		return toReturn;
	}
	
	/**
	 * calculates the stats
	 * @param nps
	 * @return an array of doubles that represents the stats of the song
	 * 		[0] - the average nps of the chart
	 * 		[1] - the standard deviation of the notes under the average nps
	 * 		[2] - the standard deviation of the notes above the average nps
	 * 		[3] - the maximum nps found in the chart
	 * 		[4] - the end time of the chart
	 * 		[5] - nothing this doesn't exist
	 */
	private static double[] deviationParse(ArrayList<NPS> nps){
		
		//avg nps, lower std dev, upper std dev
		double[] toReturn = new double[5];
		toReturn[0] = avgNPS(nps);
		toReturn[3] = -1;
		// std deviation
		double pstdDev = 0;
		double pPrev = 0;
		double mstdDev = 0;
		double mPrev = 0;
		
		double prevTimeStamp = 0;
		for(int i = 0; i < nps.size(); i++){

			if(nps.get(i).getValue() > toReturn[0]){
				pstdDev += Math.pow(nps.get(i).getValue() * (nps.get(i).getTimeStamp()-prevTimeStamp),2);
				pPrev += nps.get(i).getTimeStamp()-prevTimeStamp;
			}
			else if(nps.get(i).getValue() < toReturn[0]){
				mstdDev += Math.pow(nps.get(i).getValue() * (nps.get(i).getTimeStamp()-prevTimeStamp),2);
				mPrev += nps.get(i).getTimeStamp()-prevTimeStamp;
			}
			prevTimeStamp = nps.get(i).getTimeStamp();
			if(nps.get(i).getValue() > toReturn[3])
				toReturn[3] = nps.get(i).getValue();
		}
		if(pPrev != 0)
			pstdDev /= (pPrev);
		
		if(mPrev != 0)
			mstdDev /= (mPrev);
		toReturn[1] = Math.sqrt(mstdDev);
		toReturn[2] = Math.sqrt(pstdDev);
		toReturn[4] = prevTimeStamp;
		
		return toReturn;
	}

	@Override
	public double getDifficulty() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public static ArrayList<Bar> cheatChart(ArrayList<Bar> chart, boolean leftHand){
		
		//45 miliseconds for perf window (.045)
		ArrayList<Bar> toReturn = new ArrayList<Bar>();
		for(int i = 0; i < chart.size(); i++){
			
			Bar b = chart.get(i);
			SMBar newBar = new SMBar();
			ArrayList<Note> notes = b.getNotes();
			ArrayList<Note> newNotes = new ArrayList<Note>();
			
			for(Note n : notes){
				
				if(leftHand && (((SMNote)n).getColumn() == 0 || ((SMNote)n).getColumn() == 1))
					newNotes.add(n);
				else if(!leftHand && (((SMNote)n).getColumn() == 2 || ((SMNote)n).getColumn() == 3))
					newNotes.add(n);
			}
			
			if(newNotes.size() > 0){
				newBar.setNotes(newNotes);
				newBar.setTimeStamp(b.getTimeStamp());
				newBar.setBeatValue(((SMBar)b).getBeatValue());
				toReturn.add(newBar);
			}
		}

		int counter = 0;
		Bar prev = null;
		while(counter < toReturn.size()){
			
			Bar current = toReturn.get(counter);
			if(prev != null){
				if(Math.abs(current.getTimeStamp() - prev.getTimeStamp()) < .8){
					if(prev.getNumNotes() != 2){

						//System.out.print(prev.getNumNotes() + "|" + current.getNumNotes()+"|");
						int count = (((SMNote)prev.getNotes().get(0)).getColumn())%2 + 1;	//count is 1 or 2
						int count2 = 0;
						for(Note n : current.getNotes())
							count2 += (((SMNote)n).getColumn())%2 + 1;	//count is 1 2 or 3
						
						if(count != count2){
							//add things!
							if(count2 == 3){
								prev.addNote(current.getNotes().remove(count%2));
								counter++;
							}
							else{
								prev.addNote(current.getNotes().remove(0));
								toReturn.remove(counter);
							}
							//System.out.print(prev.getNumNotes()+"|"+current.getNumNotes());
						}
						else
							counter++;
					}
					else
						counter++;
				}
				else
					counter++;		
			}
			if(current.getNumNotes() != 0)
				prev = current;
		}
		
		
		for(Bar b : toReturn){
			if(b.getNumNotes() == 2)
				b.setNote(b.getNotes().get(0));
		}
		
		return toReturn;
	}
	
}
