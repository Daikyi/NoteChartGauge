package com.daikyi.rhythmgauge.timing;

public class SMBar extends Bar implements SMTimeable{

	private double beatValue;
	//notevalue
	
	public double getBeatValue() {
		return beatValue;
	}

	public void setBeatValue(double beatValue) {
		
		this.beatValue = beatValue;
	}

	public int compareTo(SMTimeable o) {
		
        double result = this.getBeatValue() - o.getBeatValue();

        if (result < .0001 && result > -.0001)
            return 0;

        return result < 0 ? -1 : 1;
	}
}
