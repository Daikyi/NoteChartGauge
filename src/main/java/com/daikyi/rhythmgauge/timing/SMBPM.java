package com.daikyi.rhythmgauge.timing;

public class SMBPM extends BPM implements SMTimeable{

	private double beatValue;
	
	public double getBeatValue(){
		
		return beatValue;
	}
	
	public void setBeatValue(double beatValue){
		
		this.beatValue = beatValue;
	}
	
	public int compareTo(SMTimeable o) {
		
        double result = this.getBeatValue() - o.getBeatValue();

        if (result < .001 && result > -.001)
            return 0;

        return result < 0 ? -1 : 1;
	}
}
