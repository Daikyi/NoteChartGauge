package com.daikyi.rhythmgauge;

import java.io.File;
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
	
	public SMFileParser(File file){
		super(file);
	}
	
	public void parseFile() {
		
		SMFile smsong = new SMFile();
	
        try {
        	
            Scanner scanner;
            if(file != null)
            	scanner = new Scanner(file);
            else
            	scanner = new Scanner(Paths.get(fileName));

            while (scanner.hasNextLine()) {
            	
                String curLine = scanner.nextLine().trim();
                if(!curLine.equals("") && curLine.charAt(0) == '#'){
	                if (curLine.substring(0, 7).equals("#TITLE:"))
	                    smsong.setTitle(curLine.substring(7, curLine.length() - 1));
	                //else if offset, stepartist, songartist 
	                else if (curLine.substring(0, 6).equals("#BPMS:")) 
	                	smsong.parseBpms(curLine);
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
	                    curChart.timeStamp(smsong.getBpms());
	                    curChart.calcNPS();
	                    smsong.addChart(curChart);
	                }
                }
            }
            scanner.close();
        }catch(IOException fne){System.out.println("couldn't find the file");}	
		
        //only outputs the first chart nps
        ArrayList<NPS> nps = smsong.getChart(0).getNPS();
        try {
        	
            PrintWriter writer = new PrintWriter("output.txt");
            for(int i = 0; i < nps.size(); i++){
            	
                NPS tempBar = nps.get(i);
                writer.println(tempBar.getTimeStamp() + "\t" + tempBar.getValue());
            }
            writer.close();
        }catch(FileNotFoundException fe){}
        song = smsong;
	}
}
