package com.daikyi.rhythmgauge;

import java.util.ArrayList;

import com.daikyi.rhythmgauge.timing.Chart;
import com.daikyi.rhythmgauge.timing.NPS;

import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;

public class GraphUtility{
	
	public static Scene getGraph(Chart chart){

		return createScene(chart.getNPS());
	}

	private static Scene createScene(ArrayList<NPS> nps) {
		
		double max = 0;
		for(int i = 0; i < nps.size(); i++)
			if(nps.get(i).getValue() > max)
				max = nps.get(i).getValue();
		
        final NumberAxis xAxis = new NumberAxis(0,nps.get(nps.size() - 1).getTimeStamp(),20);
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
        for(int i = 0; i < nps.size(); i++)
        	seriesNPS.getData().add(new Data<Number, Number>(nps.get(i).getTimeStamp(), nps.get(i).getValue()));
        
        Scene scene  = new Scene(ac,800,600);
        ac.getData().add(seriesNPS);
        
        return scene;
    }
}
