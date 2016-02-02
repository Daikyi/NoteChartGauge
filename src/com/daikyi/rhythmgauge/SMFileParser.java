package com.daikyi.rhythmgauge;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import com.daikyi.rhythmgauge.timing.NPS;
import com.daikyi.rhythmgauge.timing.SMChart;
import com.daikyi.rhythmgauge.timing.SMFile;
import com.daikyi.rhythmgauge.timing.Song;

public class SMFileParser extends FileParser{
	
	public SMFileParser(String fileName){
		super(fileName);
	}
	
	public double[] getTimeStamps(){
		return timeStamps;
	}
	
	public int[] getNPS(){
		return npsValues;
	}
	
	public Song parseFile() {
		
		SMFile toReturn = new SMFile();
	
        try {
            Scanner scanner = new Scanner(Paths.get(fileName));

            while (scanner.hasNextLine()) {
            	
                String curLine = scanner.nextLine().trim();
                if(!curLine.equals("")){
	                if (curLine.substring(0, 7).equals("#TITLE:"))
	                    toReturn.setTitle(curLine.substring(7, curLine.length() - 1));
	                //else if offset, stepartist, songartist 
	                else if (curLine.substring(0, 6).equals("#BPMS:")) 
	                	toReturn.parseBpms(curLine);
	                else if (curLine.equals("#NOTES:")) {
	
	                	SMChart curChart = new SMChart();
	                	
	                    //unneeded input
	                    scanner.nextLine();
	                    scanner.nextLine();
	                    curChart.setDifficultyName(scanner.nextLine());
	                    String tempDiff = scanner.nextLine().trim();
	                    curChart.setDifficulty(Double.parseDouble(tempDiff.substring(0, tempDiff.length()-1)));
	                    scanner.nextLine();
	
	                    String temp;
	                    int measureCount = 0;
	                    
	                    ArrayList<String> measure = new ArrayList<String>();
	                    while ((temp = scanner.nextLine().trim()).length() > 0 && temp.charAt(0) != ';') {
	                        if (temp.charAt(0) == ',') {
	                            curChart.addToStructure(measure, measureCount);
	                            measure = new ArrayList<String>();
	                            measureCount++;
	                        }
	                        else if(!temp.substring(0,2).equals("//"))
	                            measure.add(temp);
	
	                    }
	                    curChart.addToStructure(measure, measureCount);
	                    curChart.stripBlankBars();
	                    curChart.timeStamp(toReturn.getBpms());
	                    curChart.calcNPS();
	                    toReturn.addChart(curChart);
	                }
                }
            }
            scanner.close();
        }catch(IOException fne){System.out.println("couldn't find the file");}	
		
        ArrayList<NPS> nps = toReturn.getChart(0).getNPS();
        timeStamps = new double[nps.size()];
        npsValues = new int[nps.size()];
        try {
            PrintWriter writer = new PrintWriter("output.txt");
            for(int i = 0; i < nps.size(); i++){
                NPS tempBar = nps.get(i);
                writer.println(tempBar.getTimeStamp() + "\t" + tempBar.getValue());
                timeStamps[i] = tempBar.getTimeStamp();
                npsValues[i] = tempBar.getValue();
            }
            writer.close();
        }catch(FileNotFoundException fe){}

		return toReturn;
	}
}
