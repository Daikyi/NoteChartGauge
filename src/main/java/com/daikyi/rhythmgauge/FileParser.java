package com.daikyi.rhythmgauge;
import java.io.File;

import com.daikyi.rhythmgauge.timing.*;

public abstract class FileParser {

	protected String fileName;
	protected Song song;
	protected File file;
	
	public FileParser(String file){
		fileName = file;
		file = null;
	}
	
	public FileParser(File file){
		fileName = file.getName();
		this.file = file;
	}
	
	public abstract void parseFile();
	
	public Song getSong(){
		return song;
	}
}
