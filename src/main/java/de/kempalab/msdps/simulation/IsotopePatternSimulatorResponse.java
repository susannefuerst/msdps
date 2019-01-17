package de.kempalab.msdps.simulation;

import java.util.ArrayList;
import java.util.Map.Entry;

import de.kempalab.msdps.ElementFormula;
import de.kempalab.msdps.FragmentList;
import de.kempalab.msdps.IsotopeFormula;
import de.kempalab.msdps.IsotopeListList;
import de.kempalab.msdps.IsotopePattern;
import de.kempalab.msdps.MSDatabase;
import de.kempalab.msdps.MSDatabaseList;
import de.kempalab.msdps.MSShiftDatabase;
import de.kempalab.msdps.MassShiftDataSet;
import de.kempalab.msdps.MassShiftList;
import de.kempalab.msdps.MassSpectrum;
import de.kempalab.msdps.constants.Element;
import de.kempalab.msdps.constants.Isotope;

/**
 * Includes a list of {@link MSDatabase}s corresponding to the requested fragments and options from a
 * {@link IsotopePatternSimulatorRequest}.
 * @author sfuerst
 *
 */
public class IsotopePatternSimulatorResponse {
	
	private MSDatabaseList msDatabaseList;

	/**
	 * @return the msDatabaseList
	 */
	public MSDatabaseList getMsDatabaseList() {
		return msDatabaseList;
	}

	/**
	 * @param msDatabaseList the msDatabaseList to set
	 */
	public void setMsDatabaseList(MSDatabaseList msDatabaseList) {
		this.msDatabaseList = msDatabaseList;
	}

	/**
	 * 
	 * @param index index of the {@link Fragment} in the {@link FragmentList} of the
	 *              {@link IsotopePatternSimulatorRequest} that corresponds to the
	 *              spectrum you want to get.
	 * @return Simulated spectrum with corresponding formulas. Attention! This only
	 *         works if analyzeMassShifts was set to true in the request.
	 */
	public IsotopePattern getIsotopePattern(int index) {
		// TODO: Ensure that MassShifts have been analyzed.
		MSShiftDatabase database = ((MSShiftDatabase) getMsDatabaseList().get(index));
		MassSpectrum mixedSpectrum = database.getMixedSpectrum();
		MassShiftDataSet mixedShifts = database.getMixedMassShifts();
		ElementFormula compoundFormula = ElementFormula.fromString(database.getFragmentFormula());
		ArrayList<IsotopeFormula> isotopeFormulas = new ArrayList<IsotopeFormula>();
		for (Entry<MassShiftList, IsotopeListList> shiftEntry : mixedShifts.entrySet()) {
			IsotopeFormula shiftInducingIsotopes = shiftEntry.getValue().toIsotopeFormula();
			IsotopeFormula completeIsotopeFormula = new IsotopeFormula();
			for (Entry<Element, Integer> compoundFormulaEntry : compoundFormula.entrySet()) {
				Element element = compoundFormulaEntry.getKey();
				for (Isotope isotope : element.getIsotopes()) {
					int totalElementNumber = compoundFormula.get(element);
					if (shiftInducingIsotopes.get(isotope) != null) {
						int numberOfHeavyIsotopes = shiftInducingIsotopes.get(isotope);
						completeIsotopeFormula.put(element.lightestIsotope(),
								totalElementNumber - numberOfHeavyIsotopes);
						completeIsotopeFormula.put(isotope, numberOfHeavyIsotopes);
					} else {
						completeIsotopeFormula.put(element.lightestIsotope(), totalElementNumber);
					}
				}
			}
			isotopeFormulas.add(completeIsotopeFormula);
		}
		IsotopePattern pattern = new IsotopePattern(mixedSpectrum.getIntensityType(), isotopeFormulas);
		for (Entry<Double, Double> entry : mixedSpectrum.entrySet()) {
			pattern.put(entry.getKey(), entry.getValue());
		}
		return pattern;
	}

	/**
	 * 
	 * @param index index of the {@link Fragment} in the {@link FragmentList} of the
	 *              {@link IsotopePatternSimulatorRequest} that corresponds to the
	 *              spectrum you want to get.
	 * @return Simulated spectrum with corresponding formulas. Formulas only
	 *         represent the heavy isotopes that induced this peak. Attention! This
	 *         only works if analyzeMassShifts was set to true in the request.
	 */
	public IsotopePattern getIsotopePatternWithReducedFormulas(int index) {
		// TODO: Ensure that MassShifts have been analyzed.
		MSShiftDatabase database = ((MSShiftDatabase) getMsDatabaseList().get(index));
		MassSpectrum mixedSpectrum = database.getMixedSpectrum();
		MassShiftDataSet mixedShifts = database.getMixedMassShifts();
		ArrayList<IsotopeFormula> isotopeFormulas = new ArrayList<IsotopeFormula>();
		for (Entry<MassShiftList, IsotopeListList> shiftEntry : mixedShifts.entrySet()) {
			isotopeFormulas.add(shiftEntry.getValue().toIsotopeFormula());
		}
		IsotopePattern pattern = new IsotopePattern(mixedSpectrum.getIntensityType(), isotopeFormulas);
		for (Entry<Double, Double> entry : mixedSpectrum.entrySet()) {
			pattern.put(entry.getKey(), entry.getValue());
		}
		return pattern;
	}

	/**
	 * 
	 * @param index index of the {@link Fragment} in the {@link FragmentList} of the
	 *              {@link IsotopePatternSimulatorRequest} that corresponds to the
	 *              spectrum you want to get.
	 * @return Simulated spectrum.
	 */
	public MassSpectrum getSpectrum(int index) {
		// TODO: Ensure that MassShifts have been analyzed.
		MSShiftDatabase database = ((MSShiftDatabase) getMsDatabaseList().get(index));
		MassSpectrum mixedSpectrum = database.getMixedSpectrum();
		return mixedSpectrum;
	}

}
