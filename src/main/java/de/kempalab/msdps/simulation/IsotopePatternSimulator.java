package de.kempalab.msdps.simulation;

import java.util.Map.Entry;

import de.kempalab.msdps.Fragment;
import de.kempalab.msdps.IsotopeSet;
import de.kempalab.msdps.MSDatabase;
import de.kempalab.msdps.MSDatabaseList;
import de.kempalab.msdps.MSShiftDatabase;
import de.kempalab.msdps.MassSpectrum;
import de.kempalab.msdps.constants.IncorporationType;
import de.kempalab.msdps.exception.FrequencyTypeMismatchException;
import de.kempalab.msdps.log.MyLogger;

public class IsotopePatternSimulator {
	
	public static final MyLogger LOGGER = MyLogger.getLogger(IsotopePatternSimulator.class);
	
	public static IsotopePatternSimulatorResponse simulate(IsotopePatternSimulatorRequest request) throws FrequencyTypeMismatchException {
		final double incRate = request.getIncorporationRate().getRateValue();
		final double naturalFragments = request.getTotalNumberOfFragments() * (1 - incRate);
		final double experimentalFragments = request.getTotalNumberOfFragments() * incRate;
		final boolean analyzeMassShifts = request.getAnalyzeMassShifts();
		MSDatabaseList msDatabaseList = new MSDatabaseList();
		for (Fragment fragment : request.getFragments()) {
			IsotopeSet naturalSet = new IsotopeSet(fragment, naturalFragments, IncorporationType.NATURAL);
			IsotopeSet markedSet = new IsotopeSet(fragment, experimentalFragments, IncorporationType.EXPERIMENTAL);
			MassSpectrum naturalSpectrum = naturalSet.simulateSpectrum();
			MassSpectrum markedSpectrum = markedSet.simulateSpectrum();
			MassSpectrum mixedSpectrum = naturalSpectrum.merge(markedSpectrum);
			Integer roundMassesPrecision = request.getRoundedMassPrecision();
			Integer roundFrequenciesPrecision = request.getRoundedFrequenciesPrecision();
			Double minimalRelativeFrequency = request.getMinimalRelativeFrequency();
			naturalSpectrum = prepareSpectrum(naturalSpectrum, roundMassesPrecision, roundFrequenciesPrecision, minimalRelativeFrequency);
			markedSpectrum = prepareSpectrum(markedSpectrum, roundMassesPrecision, roundFrequenciesPrecision, minimalRelativeFrequency);
			mixedSpectrum =	prepareSpectrum(mixedSpectrum, roundMassesPrecision, roundFrequenciesPrecision, minimalRelativeFrequency);
			if (analyzeMassShifts) {
				MSShiftDatabase msShiftDatabase = new MSShiftDatabase();
				msShiftDatabase.setIncorporatedTracers(fragment.getCapacityFormula());
				msShiftDatabase.setIncorporationRate(incRate);
				msShiftDatabase.setFragmentKey(fragment.getFragmentKey());
				msShiftDatabase.setNaturalSpectrum(naturalSpectrum);
				msShiftDatabase.setMarkedSpectrum(markedSpectrum);
				msShiftDatabase.setMixedSpectrum(mixedSpectrum);
				msShiftDatabase.setFragmentFormula(fragment.getFormula());
				msShiftDatabase.analyseAllShifts();
				LOGGER.debug("\n" + msShiftDatabase);
				msDatabaseList.add(msShiftDatabase);
			} else {
				MSDatabase msDatabase = new MSDatabase();
				msDatabase.setIncorporatedTracers(fragment.getCapacityFormula());
				msDatabase.setIncorporationRate(incRate);
				msDatabase.setFragmentKey(fragment.getFragmentKey());
				msDatabase.setNaturalSpectrum(naturalSpectrum);
				msDatabase.setMarkedSpectrum(markedSpectrum);
				msDatabase.setMixedSpectrum(mixedSpectrum);
				msDatabase.setFragmentFormula(fragment.getFormula());
				LOGGER.debug("\n" + msDatabase);
				msDatabaseList.add(msDatabase);
			}
		}
		IsotopePatternSimulatorResponse simulationResponse = new IsotopePatternSimulatorResponse();
		simulationResponse.setMsDatabaseList(msDatabaseList);
		return simulationResponse;
	}
	
	public static MassSpectrum prepareSpectrum(MassSpectrum spectrum, Integer roundMassesPrecision,
			Integer roundFrequenciesPrecision, Double minimalRelativeFrequency) {
		if (roundMassesPrecision != null) {
			spectrum = spectrum.roundMasses(roundMassesPrecision);
		}
		spectrum = spectrum.toRelativeFrequency();
		if (roundFrequenciesPrecision != null) {
			spectrum = spectrum.roundFrequencies(roundFrequenciesPrecision);
		}
		if (minimalRelativeFrequency != null) {
			spectrum = spectrum.skipLowFrequencies(minimalRelativeFrequency);
		}
		Double sumOfFrequencies = 0.0;
		for (Entry<Double,Double> entry : spectrum.entrySet()) {
			sumOfFrequencies = sumOfFrequencies + entry.getValue();
		}
		return spectrum.sortAscendingByMass();
	}
}
