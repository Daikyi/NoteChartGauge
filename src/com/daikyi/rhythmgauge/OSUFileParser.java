package com.daikyi.rhythmgauge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import com.daikyi.rhythmgauge.timing.NPS;
import com.daikyi.rhythmgauge.timing.Note;
import com.daikyi.rhythmgauge.timing.SMBar;
import com.daikyi.rhythmgauge.timing.SMChart;
import com.daikyi.rhythmgauge.timing.SMFile;
import com.daikyi.rhythmgauge.timing.SMNote;
import com.daikyi.rhythmgauge.timing.Song;

public class OSUFileParser extends FileParser{
	
	public OSUFileParser(String fileName){
		super(fileName);
	}
	
	public OSUFileParser(File file){
		super(file);
	}
	
	public void parseFile() {
		
		SMFile smsong = new SMFile();
		SMChart chart = new SMChart();
        try {
        	
            Scanner scanner;
            if(file != null)
            	scanner = new Scanner(file);
            else
            	scanner = new Scanner(Paths.get(fileName));

            //ty to cpot for massive osu parsing help w/ code
            boolean inHits = false;
            boolean didMode = false;
            while (scanner.hasNextLine()) {
            	String inLine = scanner.nextLine().trim();
                if(inHits){
                	
                	String[] hitObject = inLine.split(",");
                	int time = Integer.parseInt(hitObject[2]);
                	int noteIndex = Integer.parseInt(hitObject[0]);
                	if(noteIndex<100)
                		noteIndex=0;
                	else if(noteIndex < 250)
                		noteIndex=1;
                	else if(noteIndex < 400)
                		noteIndex = 2;
                	else if(noteIndex < 500)
                		noteIndex = 3;
                	
                	SMNote note = new SMNote();
                	note.setTimeStamp(time/1000.0);
                	note.setColumn(noteIndex);
                	SMBar bar = new SMBar();
                	bar.setNote(note);
                	chart.addBar(bar);
                }
                else{
                	if(inLine.toLowerCase().indexOf("circlesize:") != -1){	//only 4k
                        if(Integer.parseInt(inLine.substring(11).trim()) != 4)
                        	return;
                	}
                    else if(inLine.toLowerCase().indexOf("mode:") != -1){
                        // Abort mission if not a mania map
                        if(Integer.parseInt(inLine.substring(5).trim()) != 3) return;
                        didMode = true;
                    }else if(inLine.toLowerCase().equals("[hitobjects]")){
                        // Abort mission if not a mania map
                        if (!didMode) return;
                        	inHits = true;
                    }
                }
            }
            scanner.close();
        }catch(IOException fne){System.out.println("couldn't find the file");}	
		
        smsong.addChart(chart);
        song = smsong;
	}
}
