package de.kempalab.msdps.visualisation;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;

import de.kempalab.msdps.MSDatabase;
import de.kempalab.msdps.MSShiftDatabase;

/**
 * Class to create different charts to visualize mass spectra.
 * @author sfuerst
 *
 */
public class MSCategoryBarChartCreator {
	
	public static final String CHART_X_LABEL = "Mass (u)";
	public static final String CHART_Y_LABEL = "Mass Isotopomer Distribution";
	private static final double MAX_BAR_WIDTH = 0.05;
	
	public MSCategoryBarChartCreator() {
		
	}
	
	/**
	 * Creates a category bar chart where the natural, marked and mixed spectrum from the {@link MSDatabase} is included.
	 * Masses are realized as categories. If the {@link MSDatabase} is an instance of a {@link MSShiftDatabase} there will
	 * be a tool tip for each bar that represents the incorporated isotopes at this point.
	 * @param msDatabase
	 * @return a {@link JFreeChart} that may be used for visualization of the spectra in the {@link MSDatabase}.
	 */
	public static JFreeChart createCombinedMSBarChart(MSDatabase msDatabase) {
		MSCategoryDataset spectraDataset = new MSCategoryDataset(msDatabase);
		String title = "Fragment: " + msDatabase.getFragmentKey() + "_" + msDatabase.getFragmentFormula() + ", incorpoprated tracer: " + msDatabase.getIncorporatedTracers();
		JFreeChart chart = ChartFactory.createBarChart(title, CHART_X_LABEL , CHART_Y_LABEL, spectraDataset,
				PlotOrientation.VERTICAL, true, true, false);
		CategoryPlot categoryPlot = chart.getCategoryPlot();
		BarRenderer renderer = (BarRenderer) categoryPlot.getRenderer();
		renderer.setMaximumBarWidth(MAX_BAR_WIDTH); // set maximum width relative to chart;
		renderer.setBaseToolTipGenerator(new MSCategoryToolTipGenerator());
		CategoryAxis domainAxis = categoryPlot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
		return chart;
	}

}
