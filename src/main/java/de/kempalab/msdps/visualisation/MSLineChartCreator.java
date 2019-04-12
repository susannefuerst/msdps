package de.kempalab.msdps.visualisation;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.TextTitle;

import de.kempalab.msdps.MassSpectrum;

public class MSLineChartCreator {
	
	public static final String CHART_X_LABEL = "m/z";
	public static final String CHART_Y_LABEL = "Intensity";
	
	public MSLineChartCreator() {
		
	}
	
	public static JFreeChart createMsLineChart(MassSpectrum spectrum) {
		MSLineChartDataset spectrumDataset = new MSLineChartDataset(spectrum);
		String title = "Test Spectrum";
		String subtitle = "test";
		JFreeChart chart = ChartFactory.createXYBarChart(title, CHART_X_LABEL , false, CHART_Y_LABEL, spectrumDataset,
				PlotOrientation.VERTICAL, true, true, false);
		chart.addSubtitle(new TextTitle(subtitle));
		XYPlot plot = chart.getXYPlot();
		XYItemRenderer renderer = plot.getRenderer();
//		renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		renderer.setSeriesPaint(0, new Color(0, 0, 0)/*black*/);
//		renderer.setBaseToolTipGenerator(new MSLineChartToolTipGenerator());
		renderer.setBaseItemLabelGenerator(new MSLineChartLabelGenerator());
        renderer.setBaseItemLabelsVisible(true);
        renderer.setBaseItemLabelPaint(Color.black);
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(XYPlot.DEFAULT_GRIDLINE_PAINT);
		plot.setRangeGridlinePaint(XYPlot.DEFAULT_GRIDLINE_PAINT);
		return chart;
	}

}
