package com.daikyi.rhythmgauge.timing;

public abstract class TimingObject implements Comparable<TimingObject>{

	double timeStamp;
	
	public double getTimeStamp(){
		
		return timeStamp;
	}
	
	public void setTimeStamp(double timeStamp){
		
		this.timeStamp = timeStamp;
	}
}
