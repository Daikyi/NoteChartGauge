package com.daikyi.rhythmgauge.timing;

public class BPM extends TimingObject{

	double value;
	
	public double getValue(){
		
		return value;
	}
	
	public void setValue(double value){
		
		this.value = value;
	}
	
	public int compareTo(TimingObject o) {
		
        double result = value - ((BPM)o).value;

        if (result < .001 && result > -.001)
            return 0;

        return result < 0 ? -1 : 1;
	}
	
}
