package com.daikyi.rhythmgauge.difficulty;

import com.daikyi.rhythmgauge.timing.TimingObject;

public class Difficulty extends TimingObject{
	
	double diffRating;
	
	public double getDifficulty(){
		
		return diffRating;
	}
	
	public void setDifficulty(double difficulty){
		
		diffRating = difficulty;
	}

	@Override
	public int compareTo(TimingObject o) {
		
		double result = getTimeStamp() - o.getTimeStamp();
		if(result > 0.0001)
			return 1;
		else if(result < -0.0001)
			return -1;
		return 0;
	}
}
