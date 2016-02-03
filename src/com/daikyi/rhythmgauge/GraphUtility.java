package com.daikyi.rhythmgauge;

import java.util.ArrayList;

import com.daikyi.rhythmgauge.difficulty.Difficulty;
import com.daikyi.rhythmgauge.difficulty.SMDiffRater;
import com.daikyi.rhythmgauge.timing.Chart;
import com.daikyi.rhythmgauge.timing.NPS;

import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;

public class GraphUtility{
	
	public static Scene getNPSGraph(Chart chart){

		return chartNPS(chart.getNPS());
	}
	
	public static Scene getColGraph(SMDiffRater smdr){

		return columnNPS(smdr.getColDiffs());
	}

	private static Scene chartNPS(ArrayList<NPS> nps) {
		
		double max = 0;
		for(int i = 0; i < nps.size(); i++)
			if(nps.get(i).getValue() > max)
				max = nps.get(i).getValue();
		
        NumberAxis xAxis = new NumberAxis(0,nps.get(nps.size() - 1).getTimeStamp(),20);
        NumberAxis yAxis = new NumberAxis(0,max,1);
        xAxis.setLabel("Time in seconds");
        yAxis.setLabel("Notes Per Second");
        AreaChart<Number,Number> ac = 
            new AreaChart<Number,Number>(xAxis,yAxis);
        ac.setTitle("NPS Chart");
        ac.setCreateSymbols(false);
        ac.setLegendVisible(false);
        
        XYChart.Series<Number,Number> seriesNPS= new Series<Number, Number>();
        seriesNPS.setName("NPS");
        for(int i = 0; i < nps.size(); i++)
        	seriesNPS.getData().add(new Data<Number, Number>(nps.get(i).getTimeStamp(), nps.get(i).getValue()));
        
        Scene scene  = new Scene(ac,800,600);
        ac.getData().add(seriesNPS);
        
        return scene;
    }
	
	private static Scene columnNPS(ArrayList<ArrayList<Difficulty>> diff) {
		
		double max = 35;
		
		double tmax = 0;
		for(int i = 0; i < 4; i++)
			if(diff.get(i).get(diff.get(i).size() - 1).getTimeStamp() > tmax)
				tmax = diff.get(i).get(diff.get(i).size() - 1).getTimeStamp();
		
		ArrayList<XYChart.Series> series = new ArrayList<XYChart.Series>();
		
        NumberAxis xAxis = new NumberAxis(0,tmax,20);
        NumberAxis yAxis = new NumberAxis(0,max,1);
        xAxis.setLabel("Time in seconds");
        yAxis.setLabel("Notes Per Second");
        StackedAreaChart<Number,Number> sac = 
            new StackedAreaChart<Number,Number>(xAxis,yAxis);
        sac.setTitle("Column NPS Chart");
        sac.setCreateSymbols(false);
        sac.setLegendVisible(true);
        
        Scene scene  = new Scene(sac,800,600);
        
        for(int i = 0; i < 4; i++){
        	series.add(new XYChart.Series<Number, Number>());
        	series.get(i).setName("Column " + (i+1));
        	ArrayList<Difficulty> colDiff = diff.get(i);
            for(int j = 0; j < colDiff.size(); j++)
            	series.get(i).getData().add(new Data<Number, Number>(colDiff.get(j).getTimeStamp(), colDiff.get(j).getDifficulty()));
      
      
        }
        sac.getData().addAll(series.get(0),series.get(1),series.get(2),series.get(3));
        return scene;
    }
}
