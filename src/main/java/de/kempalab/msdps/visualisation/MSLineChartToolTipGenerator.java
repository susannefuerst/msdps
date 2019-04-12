package de.kempalab.msdps.visualisation;

import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.data.xy.XYDataset;

import de.kempalab.msdps.IsotopeComposition;
import de.kempalab.msdps.IsotopeFormula;
import de.kempalab.msdps.IsotopePattern;
import de.kempalab.msdps.MassSpectrum;

public class MSLineChartToolTipGenerator extends StandardXYToolTipGenerator {
	
	@Override
	public String generateToolTip(XYDataset msLineChartDataset, int series, int item) {
		MassSpectrum spectrum = ((MSLineChartDataset) msLineChartDataset).getSpectrum();
		IsotopeComposition composition;
		if(spectrum instanceof IsotopePattern) {
			composition = ((IsotopePattern) spectrum).getPeakInducingHeavyIsotopes();
		} else {
			IsotopePattern pattern = new IsotopePattern(spectrum, true);
			((MSLineChartDataset)msLineChartDataset).setSpectrum(pattern);
			composition = pattern.getPeakInducingHeavyIsotopes();
		}
		String tooltip = "Mass: " + msLineChartDataset.getXValue(series, item) + "\n"
						+ "Intensity: " + msLineChartDataset.getYValue(series, item);
		IsotopeFormula formula = composition.get(msLineChartDataset.getXValue(series, item));
		if (formula != null) {
			return tooltip + "\n" + "Isotopes: " + formula.toSimpleString();
		}
		return tooltip;
		
	}
	
}
