package com.daikyi.rhythmgauge;
import com.daikyi.rhythmgauge.timing.*;

public abstract class FileParser {

	protected String fileName;
	protected double[] timeStamps;
	protected int[] npsValues;
	
	public FileParser(String file){
		fileName = file;
	}
	
	public abstract Song parseFile();
	public abstract double[] getTimeStamps();
	public abstract int[] getNPS();
}
