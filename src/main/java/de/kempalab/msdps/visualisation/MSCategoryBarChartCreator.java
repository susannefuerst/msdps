package de.kempalab.msdps.visualisation;

import java.awt.Color;
import java.awt.Font;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ExtendedCategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.ui.TextAnchor;

import de.kempalab.msdps.MSDatabase;
import de.kempalab.msdps.MSShiftDatabase;
import de.kempalab.msdps.constants.IncorporationType;
import de.kempalab.msdps.constants.MSDatasetOption;

/**
 * Class to create different charts to visualize mass spectra.
 * @author sfuerst
 *
 */
public class MSCategoryBarChartCreator {
	
	public static final String CHART_X_LABEL = "Mass (u)";
	public static final String CHART_Y_LABEL = "Mass Isotopomer Distribution";
	public static final String ISOTOPE_SUBABEL_FONT = "SansSerif";
	private static final double MAX_BAR_WIDTH = 0.05;
	
	public MSCategoryBarChartCreator() {
		
	}
	
	/**
	 * Creates a category bar chart where the spectra (all or single ones, depending on msBarChartType) from the {@link MSDatabase} are included.
	 * Masses are realized as categories. If the {@link MSDatabase} is an instance of a {@link MSShiftDatabase} there will
	 * be a tool tip for each bar that represents the incorporated isotopes at this point.
	 * @param msDatabase
	 * @param datasetOption
	 * @return a {@link JFreeChart} that may be used for visualization of the spectra in the {@link MSDatabase}.
	 */
	public static JFreeChart createMSBarChart(MSDatabase msDatabase, MSDatasetOption datasetOption) {
		MSCategoryDataset spectraDataset = new MSCategoryDataset(msDatabase, datasetOption);
		String title = "Fragment: " + msDatabase.getFragmentKey() + "_" + msDatabase.getFragmentFormula();
		String subtitle = "incorporated tracer: " + msDatabase.getIncorporatedTracers();
		JFreeChart chart = ChartFactory.createBarChart(title, CHART_X_LABEL , CHART_Y_LABEL, spectraDataset,
				PlotOrientation.VERTICAL, true, true, false);
		chart.addSubtitle(new TextTitle(subtitle));
		ExtendedCategoryAxis domainAxis = new ExtendedCategoryAxis(null);
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
		CategoryPlot categoryPlot = chart.getCategoryPlot();
		categoryPlot.setDomainAxis(domainAxis);
		categoryPlot.setBackgroundPaint(Color.white);
		BarRenderer renderer = (BarRenderer) categoryPlot.getRenderer();
		renderer.setMaximumBarWidth(MAX_BAR_WIDTH); // set maximum width relative to chart;
		renderer.setBaseToolTipGenerator(new MSCategoryToolTipGenerator());
		renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setBaseItemLabelsVisible(true);
        renderer.setBaseItemLabelPaint(Color.black);
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER, TextAnchor.CENTER, Math.PI / -2.0));
        renderer.setItemLabelAnchorOffset(20);
        renderer.setSeriesPaint(0, new Color(0, 0, 0)/*black*/);
        renderer.setSeriesPaint(1, new Color(51, 102, 153)/*blue*/);
        renderer.setSeriesPaint(2, new Color(102, 102, 153)/*purple*/);
        NumberAxis rangeAxis = (NumberAxis) categoryPlot.getRangeAxis();
        rangeAxis.setUpperMargin(0.10);
		MSShiftDatabase msshiftDatabase = (MSShiftDatabase) spectraDataset.getMsDatabase();
		if (!datasetOption.equals(MSDatasetOption.ALL_SPECTRA)) {
			for (Object massCategoryObject : spectraDataset.getColumnKeys()) {
				Double mass = (Double) massCategoryObject;
				String sublabel = null;
				if (datasetOption.equals(MSDatasetOption.NATURAL_SPECTRUM_ONLY)) {
					sublabel = msshiftDatabase.shiftInducingIsotopes(IncorporationType.NATURAL, mass);
				} else if (datasetOption.equals(MSDatasetOption.PARTIALLY_LABELED_SPECTRUM_ONLY)) {
					sublabel = msshiftDatabase.shiftInducingIsotopes(IncorporationType.MIXED, mass);
				} else {
					sublabel = msshiftDatabase.shiftInducingIsotopes(IncorporationType.MARKED, mass);
				}
				domainAxis.addSubLabel(mass, sublabel);
				domainAxis.setTickLabelFont(new Font(ISOTOPE_SUBABEL_FONT, Font.PLAIN, 14));
				domainAxis.setSubLabelFont(new Font(ISOTOPE_SUBABEL_FONT, Font.PLAIN, 14));
			}
		}
		return chart;
	}
}
