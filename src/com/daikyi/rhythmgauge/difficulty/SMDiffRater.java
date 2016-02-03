package com.daikyi.rhythmgauge.difficulty;

import java.util.ArrayList;

import com.daikyi.rhythmgauge.timing.Chart;
import com.daikyi.rhythmgauge.timing.SMChart;

public class SMDiffRater extends DiffRater{

	@Override
	public ArrayList<Difficulty> getDiffSpectrum(Chart chart) {
		
		SMChart smchart = (SMChart) chart;
		
		return null;
	}

}
