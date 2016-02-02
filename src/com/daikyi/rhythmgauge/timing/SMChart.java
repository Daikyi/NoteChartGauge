package com.daikyi.rhythmgauge.timing;

import java.util.ArrayList;
import java.util.Collections;

public class SMChart extends Chart{

	private String difficultyName;
	
	public SMChart(){}
	
	public void setDifficultyName(String difficultyName){
		
		this.difficultyName = difficultyName;
	}
	
	public String getDifficultyName(){
		
		return difficultyName;
	}
	
	public void stripBlankBars(){
		
		for(int i = structure.size() - 1; i >= 0; i--)		
			if(structure.get(i).getNumNotes() == 0)
				structure.remove(i);
	}

	public void addToStructure(ArrayList<String> measure, int measureCount) {
		
        //the given input for this is a set of strings, each representing a bar
        int beatNum = measure.size()/4;
        for(int i = 0; i < 4; i++){

            for(int j = 0; j < beatNum; j++){

                SMBar toAdd = new SMBar();
                ArrayList<Note> barNotes = new ArrayList<Note>();
                String temp = measure.get(((i*beatNum)+j));
                double beatValue = (double)measureCount*4 + i + (j/(double)(beatNum));
                toAdd.setBeatValue(beatValue);

                for(int k = 0; k < temp.length(); k++){
                	
                    int tempCol = Character.getNumericValue(temp.charAt(k));
                    if(tempCol != 0){
                    	SMNote noteToAdd = new SMNote();
                    	noteToAdd.setBeatValue(beatValue);
                    	noteToAdd.setColumn(k);
                    	//tempNote.settype TODO
                    	barNotes.add(noteToAdd);
                    }
                }
                toAdd.setNotes(barNotes);
                structure.add(toAdd);

            }
        }
	}
	
	public void timeStamp(ArrayList<SMBPM> bpms){
		
		ArrayList<SMTimeable> fullChart = new ArrayList<SMTimeable>();
		
        int bpmCounter = 0;
        int barCounter = 0;

        while(bpmCounter < bpms.size() && barCounter < structure.size()){

            SMBPM tempBPM = bpms.get(bpmCounter);
            SMBar tempBar = (SMBar) structure.get(barCounter);
            double compare = tempBPM.getBeatValue()- tempBar.getBeatValue();

            if(compare < -.0001) {

                fullChart.add(tempBPM);
                bpmCounter++;
            }
            else if(compare > .0001){

                fullChart.add(tempBar);
                barCounter++;
            }
            else{
                fullChart.add(tempBPM);
                fullChart.add(tempBar);
                barCounter++;
                bpmCounter++;
            }
        }
        if(bpmCounter == bpms.size())
            for(int i = barCounter; i < structure.size(); i++)
                fullChart.add((SMTimeable) structure.get(i));
        else if(barCounter == structure.size())
            for(int i = bpmCounter; i < bpms.size(); i++)
                fullChart.add(bpms.get(i));

        //now to time the whole chart
        SMTimeable tempTO = fullChart.get(0);
        double secsPerBeat = 1/(((BPM) tempTO).getValue()/60);
        double prevBeat = tempTO.getBeatValue();
        ((TimingObject) tempTO).setTimeStamp(0);
        double prevTimeStamp = 0;

        for(int i = 1; i < fullChart.size(); i++){

            tempTO = fullChart.get(i);
            prevTimeStamp = prevTimeStamp + ((tempTO.getBeatValue() - prevBeat) * secsPerBeat);
            ((TimingObject) tempTO).setTimeStamp(prevTimeStamp);
            prevBeat = tempTO.getBeatValue();
            if(tempTO instanceof BPM)
                secsPerBeat = 1/(((BPM) tempTO).getValue()/60);
        }
	}

	@Override
	public void calcNPS() {
		
		nps = new ArrayList<NPS>();
		ArrayList<TimingObject> barNPS = new ArrayList<TimingObject>();
		for(int i = 0; i < structure.size(); i++){
			NPS tempNPS = new NPS();
			tempNPS.setTimeStamp(structure.get(i).getTimeStamp()+1.001);
			barNPS.add(tempNPS);
			tempNPS = new NPS();
			tempNPS.setTimeStamp(structure.get(i).getTimeStamp()+.001);
			barNPS.add(tempNPS);
			tempNPS = new NPS();
			tempNPS.setTimeStamp(structure.get(i).getTimeStamp()-.001);
			barNPS.add(tempNPS);
			barNPS.add(structure.get(i));
		}
		
		Collections.sort(barNPS);
		int tesapo = 0;
		for(int i = 0; i < barNPS.size(); i++){
            
			TimingObject curObj = barNPS.get(i);
			
			if(curObj instanceof Bar){
				
				int notes = ((SMBar)curObj).getNumNotes();
				tesapo += notes;
				double initTime = curObj.getTimeStamp();
                int tempCounter = i;
                boolean flag = true;
            	TimingObject tempObj;

            	while(tempCounter < barNPS.size() && flag){
            		tempObj = barNPS.get(tempCounter);
            		if(tempObj.getTimeStamp() - initTime <= 1) {

            			if(tempObj instanceof NPS){
            				NPS tempNPS = ((NPS) tempObj);
            				tempNPS.setValue(tempNPS.getValue() + notes);
            			}
            			tempCounter++;
            		}
            		else
            			flag = false;
            	}
			}	
		}
		System.out.println(tesapo);
        for(int i = 0; i < barNPS.size(); i++){
        	
        	TimingObject temp = barNPS.get(i);
        	double endTimeStamp = this.getStructure().get(this.getStructure().size()-1).getTimeStamp();
        	
        	if(temp instanceof NPS){

        		if(temp.getTimeStamp() > endTimeStamp)
        			return;
        		
        		nps.add((NPS)temp);
        	}
        }
	}
}
