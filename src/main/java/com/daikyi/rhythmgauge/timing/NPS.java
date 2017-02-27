package com.daikyi.rhythmgauge.timing;

public class NPS extends TimingObject implements Comparable<TimingObject>{

	private int value;
	
	public void setValue(int value){
		
		this.value = value;
	}
	
	public int getValue(){
		
		return value;
	}

	public int compareTo(TimingObject o) {
		
        double result = this.getTimeStamp() - o.getTimeStamp();

        if (result < .00001 && result > -.00001)
            return 0;

        return result < 0 ? -1 : 1;
	}
}
