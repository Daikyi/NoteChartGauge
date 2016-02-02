package com.daikyi.rhythmgauge;

import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;

public class GraphUtility{
	
	public static Scene getGraph(FileParser file){
		file.parseFile();
		return createScene(file.getTimeStamps(), file.getNPS());
	}

	private static Scene createScene(double[] timeStamp, int[] npsCount) {
		
		double max = 0;
		for(int i = 0; i < npsCount.length; i++)
			if(npsCount[i] > max)
				max = npsCount[i];
		
        final NumberAxis xAxis = new NumberAxis(0,timeStamp[timeStamp.length - 1],20);
        final NumberAxis yAxis = new NumberAxis(0,max,1);
        xAxis.setLabel("Time in seconds");
        yAxis.setLabel("Notes Per Second");
        final AreaChart<Number,Number> ac = 
            new AreaChart<Number,Number>(xAxis,yAxis);
        ac.setTitle("NPS Chart");
        ac.setCreateSymbols(false);
        ac.setLegendVisible(false);
        
        XYChart.Series<Number,Number> seriesNPS= new Series<Number, Number>();
        seriesNPS.setName("NPS");
        for(int i = 0; i < npsCount.length; i++)
        	seriesNPS.getData().add(new Data<Number, Number>(timeStamp[i], npsCount[i]));
        
        Scene scene  = new Scene(ac,800,600);
        ac.getData().add(seriesNPS);
        
        return scene;
    }
}
