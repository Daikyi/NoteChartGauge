package com.daikyi.rhythmgauge.timing;

import java.util.ArrayList;

public class Bar extends TimingObject{

	private ArrayList<Note> notes;
	
	public ArrayList<Note> getNotes(){
		
		return notes;
	}
	
	public int getNumNotes(){
		
		return notes.size();
	}
	
	public void setNotes(ArrayList<Note> notes){
		
		this.notes = notes;
	}
	
	public int compareTo(TimingObject o) {
		
        double result = this.getTimeStamp() - o.getTimeStamp();

        if (result < .0001 && result > -.0001)
            return 0;

        return result < 0 ? -1 : 1;
	}
}
