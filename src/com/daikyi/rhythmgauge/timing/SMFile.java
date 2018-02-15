package com.daikyi.rhythmgauge.timing;

import java.util.ArrayList;

public class SMFile extends Song{

	protected ArrayList<SMBPM> bpms;
	private double offset;
	private String stepArtist;
	private ArrayList<SMChart> charts;
	
	public SMFile(){
		
		super();
		
		charts = new ArrayList<SMChart>();
	}
	
	public double getOffset(){
		
		return offset;
	}
	
	public void setOffset(double offset){
		
		this.offset = offset;
	}
	
	public String getStepArtist(){
		
		return stepArtist;
	}
	
	public void setStepArtist(String stepArtist){
		
		this.stepArtist = stepArtist;
	}
	
	public int numCharts(){
		
		return charts.size();
	}
	
	public SMChart getChart(int chartIndex){
		
		return chartIndex < numCharts() ? 
			charts.get(chartIndex) : null;
	}
	
	public void addChart(SMChart toAdd){
		
		charts.add(toAdd);
	}
	
	public SMFile(String title, String stepArtist){
		
		super(title, stepArtist);
	}
	
	public ArrayList<SMBPM> getBpms(){
		
		return bpms;
	}
	
	public void parseBpms(String bpmString){
		
		bpms = new ArrayList<SMBPM>();
		
        String[] bpmSplit = bpmString.substring(6, bpmString.length() - 1).split(",");
        for(int i = 0; i < bpmSplit.length; i++){
            SMBPM curBPM = new SMBPM();
            String singleBPM = bpmSplit[i];
            int eqSeparator = singleBPM.indexOf('=');
            curBPM.setBeatValue(Double.parseDouble(singleBPM.substring(0,eqSeparator)));
            curBPM.setValue(Double.parseDouble(singleBPM.substring(eqSeparator+1)));
            bpms.add(curBPM);
        }
	}
	
	
}
