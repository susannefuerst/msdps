package de.kempalab.msdps.visualisation;

import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.data.xy.XYDataset;

import de.kempalab.msdps.IsotopeComposition;
import de.kempalab.msdps.IsotopeFormula;
import de.kempalab.msdps.IsotopePattern;
import de.kempalab.msdps.MassSpectrum;

public class MSLineChartLabelGenerator extends StandardXYItemLabelGenerator {
	
	@Override
	public String generateLabel(XYDataset msLineChartDataset, int series, int item) {
		MassSpectrum spectrum = ((MSLineChartDataset) msLineChartDataset).getSpectrum();
		IsotopeComposition composition;
		if(spectrum instanceof IsotopePattern) {
			composition = ((IsotopePattern) spectrum).getPeakInducingHeavyIsotopes();
		} else {
			IsotopePattern pattern = new IsotopePattern(spectrum, true);
			((MSLineChartDataset)msLineChartDataset).setSpectrum(pattern);
			composition = pattern.getPeakInducingHeavyIsotopes();
		}
		IsotopeFormula formula = composition.get(msLineChartDataset.getXValue(series, item));
		if (formula != null) {
			return formula.toSimpleString();
		}
		return "";
	}
	

}
