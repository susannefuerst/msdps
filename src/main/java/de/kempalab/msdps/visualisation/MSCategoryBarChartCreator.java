package de.kempalab.msdps.visualisation;

import java.awt.Font;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ExtendedCategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;

import de.kempalab.msdps.MSDatabase;
import de.kempalab.msdps.MSShiftDatabase;
import de.kempalab.msdps.constants.IncorporationType;
import de.kempalab.msdps.constants.MSBarChartType;

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
		MSCategoryDataset spectraDataset = new MSCategoryDataset(msDatabase, MSBarChartType.ALL_SPECTRA);
		String title = "Fragment: " + msDatabase.getFragmentKey() + "_" + msDatabase.getFragmentFormula() + ", incorporated tracer: " + msDatabase.getIncorporatedTracers();
		JFreeChart chart = ChartFactory.createBarChart(title, CHART_X_LABEL , CHART_Y_LABEL, spectraDataset,
				PlotOrientation.VERTICAL, true, true, false);
		CategoryPlot categoryPlot = chart.getCategoryPlot();
		BarRenderer renderer = (BarRenderer) categoryPlot.getRenderer();
		renderer.setMaximumBarWidth(MAX_BAR_WIDTH); // set maximum width relative to chart;
		renderer.setBaseToolTipGenerator(new MSCategoryToolTipGenerator());
		ExtendedCategoryAxis domainAxis = new ExtendedCategoryAxis(null);
		categoryPlot.setDomainAxis(domainAxis);
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
		MSShiftDatabase msshiftDatabase = (MSShiftDatabase) spectraDataset.getMsDatabase();
		if (!(msshiftDatabase.includesMarkedSpectrum())/*the chart only contains a natural spectrum */) {
			for (Object massCategoryObject : spectraDataset.getColumnKeys()) {
				Double mass = (Double) massCategoryObject;
				String isotopesString = msshiftDatabase.shiftInducingIsotopes(IncorporationType.NATURAL, mass);
				domainAxis.addSubLabel(mass, isotopesString);
			}
			
		}
		return chart;
	}
	
	/**
	 * Creates a category bar chart where the spectra (all or single ones, depending on msBarChartType) from the {@link MSDatabase} are included.
	 * Masses are realized as categories. If the {@link MSDatabase} is an instance of a {@link MSShiftDatabase} there will
	 * be a tool tip for each bar that represents the incorporated isotopes at this point.
	 * @param msDatabase
	 * @param msBarChartType
	 * @return a {@link JFreeChart} that may be used for visualization of the spectra in the {@link MSDatabase}.
	 */
	public static JFreeChart createMSBarChart(MSDatabase msDatabase, MSBarChartType msBarChartType) {
		MSCategoryDataset spectraDataset = new MSCategoryDataset(msDatabase, msBarChartType);
		String title = "Fragment: " + msDatabase.getFragmentKey() + "_" + msDatabase.getFragmentFormula() + ", incorporated tracer: " + msDatabase.getIncorporatedTracers();
		JFreeChart chart = ChartFactory.createBarChart(title, CHART_X_LABEL , CHART_Y_LABEL, spectraDataset,
				PlotOrientation.VERTICAL, true, true, false);
		CategoryPlot categoryPlot = chart.getCategoryPlot();
		BarRenderer renderer = (BarRenderer) categoryPlot.getRenderer();
		renderer.setMaximumBarWidth(MAX_BAR_WIDTH); // set maximum width relative to chart;
		renderer.setBaseToolTipGenerator(new MSCategoryToolTipGenerator());
		ExtendedCategoryAxis domainAxis = new ExtendedCategoryAxis(null);
		categoryPlot.setDomainAxis(domainAxis);
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
		MSShiftDatabase msshiftDatabase = (MSShiftDatabase) spectraDataset.getMsDatabase();
		if (!msBarChartType.equals(MSBarChartType.ALL_SPECTRA)) {
			for (Object massCategoryObject : spectraDataset.getColumnKeys()) {
				Double mass = (Double) massCategoryObject;
				String sublabel = null;
				if (msBarChartType.equals(MSBarChartType.NATURAL_SPECTRUM_ONLY)) {
					sublabel = msshiftDatabase.shiftInducingIsotopes(IncorporationType.NATURAL, mass);
				} else if (msBarChartType.equals(MSBarChartType.PARTIALLY_LABELED_SPECTRUM_ONLY)) {
					sublabel = msshiftDatabase.shiftInducingIsotopes(IncorporationType.MIXED, mass);
				} else {
					sublabel = msshiftDatabase.shiftInducingIsotopes(IncorporationType.MARKED, mass);
				}
				domainAxis.addSubLabel(mass, sublabel);
				domainAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 14));
				domainAxis.setSubLabelFont(new Font("SansSerif", Font.PLAIN, 14));
			}
		}
		return chart;
	}

}
