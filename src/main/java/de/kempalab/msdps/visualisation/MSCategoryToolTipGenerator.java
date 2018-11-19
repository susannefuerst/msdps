package de.kempalab.msdps.visualisation;

import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.data.category.CategoryDataset;

import de.kempalab.msdps.MSShiftDatabase;
import de.kempalab.msdps.constants.IncorporationType;

/**
 * Generates tool tips over peaks/bars for an MSBarChart. The tool tip displays the isotopes that induced the shift from the bar representing
 * the p_0 peak to the bar representing the peak p_k (where the mouse is over)
 * @author sfuerst
 *
 */
@SuppressWarnings("serial")
public class MSCategoryToolTipGenerator extends StandardCategoryToolTipGenerator {
	
	/**
	 * Generates tool tips over peaks/bars for an MSBarChart. The tool tip displays the isotopes that induced the shift from the bar representing
	 * the p_0 peak to the bar representing the peak p_k (where the mouse is over)
	 */
	@Override
    public String generateToolTip(CategoryDataset dataset, int legendIncorporation, int massCategory) {
		MSCategoryDataset spectraDataset = ((MSCategoryDataset) dataset);
		if (!(spectraDataset.getMsDatabase() instanceof MSShiftDatabase)) {
			return super.generateToolTip(dataset, legendIncorporation, massCategory);
		}
		MSShiftDatabase msshiftDatabase = (MSShiftDatabase) spectraDataset.getMsDatabase();
		int naturalIncorporation = dataset.getRowIndex(MSCategoryDataset.NATURAL_INC_LABEL);
		int completeIncorporation = dataset.getRowIndex(MSCategoryDataset.COPLETE_INC_LABEL);
		Double mass = (Double) dataset.getColumnKey(massCategory);
		IncorporationType incorporationType = IncorporationType.MIXED;
		if (naturalIncorporation == legendIncorporation) {
			incorporationType = IncorporationType.NATURAL;
		}
		if (completeIncorporation == legendIncorporation) {
			incorporationType = IncorporationType.MARKED;
		}
		String isotopesString = msshiftDatabase.shiftInducingIsotopes(incorporationType, mass);
        return isotopesString;
    }

}
