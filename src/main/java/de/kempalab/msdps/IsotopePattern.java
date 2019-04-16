/*
 * (C) Copyright 2015-2017 by MSDK Development Team
 *
 * This software is dual-licensed under either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1 as published by the Free
 * Software Foundation
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by the Eclipse Foundation.
 */
package de.kempalab.msdps;

import java.util.ArrayList;
import java.util.Map.Entry;

import de.kempalab.msdps.data.DataTable;

/**
 * A {@link MassSpectrum} where each peak corresponds to an isotopologue of the parent peak
 * molecule. This class also provides a member to store the isotope compositions.
 * 
 * @author Susanne Fürst, susannefuerst@freenet.de, susanne.fuerst@mdc-berlin.de
 *
 */
public class IsotopePattern extends MassSpectrum {

	/*
	 * ordered as the entries in the MassSpectrum
	 */
	private IsotopeComposition isotopeComposition = new IsotopeComposition();
	private IsotopeComposition peakInducingHeavyIsotopes = new IsotopeComposition();

	public IsotopePattern(MassSpectrum massSpectrum, boolean analyseHeavyIsotopes) {
		super(massSpectrum.getIntensityType(), massSpectrum.getSpectrumType());
		for (Entry<Double, Double> entry : massSpectrum.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
		if (!massSpectrum.getCompositions().removeNullValues().isEmpty()) {
			isotopeComposition = massSpectrum.getCompositions();
			if (analyseHeavyIsotopes) {
				for (Entry<Double,IsotopeFormula> entry : massSpectrum.getCompositions().entrySet()) {
					if (entry.getValue() == null) {
						peakInducingHeavyIsotopes.put(entry.getKey(), null);
					} else {
						IsotopeFormula heavyIsotopes = entry.getValue().getHeavyIsotopes();
						peakInducingHeavyIsotopes.put(entry.getKey(), heavyIsotopes);
					}
				}
			}
		}
	}

	public IsotopeComposition getIsotopeComposition() {
		return isotopeComposition;
	}

	public void setIsotopeComposition(IsotopeComposition isotopeComposition) {
		this.isotopeComposition = isotopeComposition;
	}

	public IsotopeComposition getPeakInducingHeavyIsotopes() {
		return peakInducingHeavyIsotopes;
	}

	public void setPeakInducingHeavyIsotopes(IsotopeComposition peakInducingHeavyIsotopes) {
		this.peakInducingHeavyIsotopes = peakInducingHeavyIsotopes;
	}

	public DataTable toDataTable() {
		DataTable dataTable = new DataTable("Mass", "Frequency", "Formula", "HeavyIsotopes");
		dataTable.addColumn(this);
		ArrayList<String> formulaStr = new ArrayList<>();
		for (Entry<Double,IsotopeFormula> entry : compositions.entrySet()) {
			if (entry.getValue() == null) {
				formulaStr.add("");
			} else {
				formulaStr.add(entry.getValue().toSimpleString());				
			}
		}
		dataTable.addColumn(formulaStr);
		ArrayList<String> heavyIsotopesStr = new ArrayList<>();
		for (Entry<Double,IsotopeFormula> entry : peakInducingHeavyIsotopes.entrySet()) {
			if (entry.getValue() == null) {
				heavyIsotopesStr.add("");
			} else {
				heavyIsotopesStr.add(entry.getValue().toSimpleString());				
			}
		}
		dataTable.addColumn(heavyIsotopesStr);
		return dataTable;
	}
	
	@Override
	public String toString() {
		return toDataTable().toString("NA", true);
	}
}
