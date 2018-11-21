package de.kempalab.msdps.demo;

import de.kempalab.msdps.Fragment;
import de.kempalab.msdps.IsotopeSet;
import de.kempalab.msdps.MSShiftDatabase;
import de.kempalab.msdps.MassSpectrum;
import de.kempalab.msdps.constants.FragmentKey;
import de.kempalab.msdps.constants.IncorporationType;
import de.kempalab.msdps.constants.MSBarChartType;
import de.kempalab.msdps.exception.FragmentNotFoundException;
import de.kempalab.msdps.exception.FrequencyTypeMismatchException;
import de.kempalab.msdps.log.MyLogger;
import de.kempalab.msdps.simulation.IsotopePatternSimulator;
import de.kempalab.msdps.visualisation.MSBarChartApplicationWindow;

public class SimulateMixedIncorporations2 {
	
	public static final MyLogger LOGGER = MyLogger.getLogger(SimulateMixedIncorporations2.class);
	
	public static final double INC_C = 0.5;
	public static final double NUMBER_OF_FRAGMENTS = 100000.0;
	public static final Integer PRECISION = 4;
	public static final double MIN_FREQUENCY = 0.003;
	
	public static final double INC = INC_C;
	
	public static void main(String[] args) throws FragmentNotFoundException, FrequencyTypeMismatchException {
		Fragment fragmentC = new Fragment(FragmentKey.UNKNOWN, "C2", "C");

		IsotopeSet naturalSet = new IsotopeSet(fragmentC, NUMBER_OF_FRAGMENTS * (1 - INC), IncorporationType.NATURAL);
		IsotopeSet markedSetC = new IsotopeSet(fragmentC, NUMBER_OF_FRAGMENTS * (INC_C), IncorporationType.EXPERIMENTAL);
		
		MassSpectrum naturalSpectrum = naturalSet.simulateSpectrum();
		MassSpectrum markedSpectrumC = markedSetC.simulateSpectrum();
		MassSpectrum mixedSpectrum = naturalSpectrum.merge(markedSpectrumC);
		
		naturalSpectrum = IsotopePatternSimulator.prepareSpectrum(naturalSpectrum, PRECISION, PRECISION, MIN_FREQUENCY);
		markedSpectrumC = IsotopePatternSimulator.prepareSpectrum(markedSpectrumC, PRECISION, PRECISION, MIN_FREQUENCY);
		mixedSpectrum = IsotopePatternSimulator.prepareSpectrum(mixedSpectrum, PRECISION, PRECISION, MIN_FREQUENCY);
		
		MSShiftDatabase msShiftDatabase = new MSShiftDatabase();
		msShiftDatabase.setIncorporatedTracers("C");
		msShiftDatabase.setIncorporationRate(INC);
		msShiftDatabase.setFragmentKey(fragmentC.getFragmentKey());
		msShiftDatabase.setNaturalSpectrum(naturalSpectrum);
		msShiftDatabase.setMarkedSpectrum(markedSpectrumC);
		msShiftDatabase.setMixedSpectrum(mixedSpectrum);
		msShiftDatabase.setFragmentFormula(fragmentC.getFormula());
		msShiftDatabase.analyseAllShifts();
		
		LOGGER.info(msShiftDatabase);
		MSBarChartApplicationWindow demo = new MSBarChartApplicationWindow("Bar Demo 1", msShiftDatabase, MSBarChartType.ALL_SPECTRA);
		demo.pack();
		demo.setVisible(true);
	}
}
