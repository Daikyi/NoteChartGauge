package com.daikyi.rhythmgauge;

import com.daikyi.rhythmgauge.difficulty.SMDiffRater;
import com.daikyi.rhythmgauge.timing.SMFile;

public class Tester {

	public static final String[] dataSet = 
		{
		 "Lovely City.sm",
		 "02 Oops.sm",
		 "07 - I'm Getting Serious!.sm",
		 "Press Start.sm",
		 "03 faster! faster!! faster!!!.sm",
		 "I Don't Play KanColle But Shimakaze-chan Is Cute.sm",
		 "B11 - Mix a Little Foolishness.sm",
		 "Our Journey and Epilogue.sm",
		 "Mario Trap.sm",
		 "Rave 5.sm",
		 "Kakushinteki Metamaruphose!.sm",
		 "Kaguyahime.sm",
		 "207_where is my balls.sm",
		 "JtehOP.sm",
		 "Parousia-LAST JUDGEMENT-.sm",
		 "lost.sm",
		 "My Wolf Eats Preps.sm",
		 "Brave Shine.sm",
		 "Kobaryo - Pumpin' Junkies (Kobaryo's FTN-Remix).sm",
		 "sm.sm",
		 "divorce.sm"};
	
	public static void main(String args[]){
		
		for(int i = 0; i < dataSet.length; i++){
			System.out.print(dataSet[i] + " | ");
			FileParser file = new SMFileParser("sm/"+dataSet[i]);
			file.parseFile();
			SMFile song = (SMFile)file.getSong();
			SMDiffRater fileDiff = new SMDiffRater(song.getChart(0));
			
			System.out.println(fileDiff.calculateChartDifficulty());

		}
	}
}
