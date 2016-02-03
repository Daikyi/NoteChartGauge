package com.daikyi.rhythmgauge;
import com.daikyi.rhythmgauge.timing.*;

public abstract class FileParser {

	protected String fileName;
	protected Song song;
	public FileParser(String file){
		fileName = file;
	}
	
	public abstract void parseFile();
	
	public Song getSong(){
		return song;
	}
}
