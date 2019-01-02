package de.kempalab.msdps.visualisation;

import java.util.List;
import java.util.Map.Entry;

import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.IntervalXYDataset;

import de.kempalab.msdps.MassSpectrum;

public class MSLineChartDataset extends AbstractXYDataset implements IntervalXYDataset {
	
	private MassSpectrum spectrum;
	List<Entry<Double,Double>> spectrumEntries;
	
	public MSLineChartDataset(MassSpectrum massSpectrum) {
		this.spectrum = massSpectrum;
		this.spectrumEntries = spectrum.toEntryList();
		
	}

	@Override
	public int getItemCount(int series) {
		if (spectrum == null) 
			return 0;
		return spectrum.size();
	}

	@Override
	public Number getX(int series, int item) {
		return spectrumEntries.get(item).getKey();
	}

	@Override
	public Number getY(int series, int item) {
		return spectrumEntries.get(item).getValue();
	}

	@Override
	public Number getStartX(int series, int item) {
		return getX(series, item).doubleValue();
	}

	@Override
	public double getStartXValue(int series, int item) {
		return getX(series, item).doubleValue();
	}

	@Override
	public Number getEndX(int series, int item) {
		return getX(series, item).doubleValue();
	}

	@Override
	public double getEndXValue(int series, int item) {
		return getX(series, item).doubleValue();
	}

	@Override
	public Number getStartY(int series, int item) {
		return getY(series, item).doubleValue();
	}

	@Override
	public double getStartYValue(int series, int item) {
		return getY(series, item).doubleValue();
	}

	@Override
	public Number getEndY(int series, int item) {
		return getY(series, item).doubleValue();
	}

	@Override
	public double getEndYValue(int series, int item) {
		return getY(series, item).doubleValue();
	}

	@Override
	public int getSeriesCount() {
		return 1;
	}

	@Override
	public Comparable getSeriesKey(int series) {
		return 1;
	}

}
