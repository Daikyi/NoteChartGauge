package com.daikyi.rhythmgauge.timing;

public class SMNote extends Note implements SMTimeable{

	private double beatValue;
	NoteValue noteValue;
	private int column;//(0,1,2,3,etc)
	NoteType noteType;
	
	public NoteValue getNoteValue(){
		
		return noteValue;
	}
	
	public void setNoteValue(NoteValue noteValue){
		
		this.noteValue = noteValue;
	}
	
	public NoteType getNoteType(){
		
		return noteType;
	}
	
	public void setNoteType(NoteType noteType){
		
		this.noteType = noteType;
	}
	
	public double getBeatValue() {
		return beatValue;
	}

	public void setBeatValue(double beatValue) {
		
		this.beatValue = beatValue;
	}
	
	public int getColumn(){
		
		return column;
	}
	
	public void setColumn(int column){
		
		this.column = column;
	}

	public int compareTo(TimingObject o) {
		
        double result = this.getBeatValue() - ((SMTimeable)o).getBeatValue();

        if (result < .001 && result > -.001)
            return 0;

        return result < 0 ? -1 : 1;
	}
}
