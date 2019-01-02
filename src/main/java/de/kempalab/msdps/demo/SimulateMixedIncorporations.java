package de.kempalab.msdps.demo;

import de.kempalab.msdps.Fragment;
import de.kempalab.msdps.FragmentsDatabase;
import de.kempalab.msdps.IsotopeSet;
import de.kempalab.msdps.MSShiftDatabase;
import de.kempalab.msdps.MassSpectrum;
import de.kempalab.msdps.constants.FragmentKey;
import de.kempalab.msdps.constants.FrequencyType;
import de.kempalab.msdps.constants.IncorporationType;
import de.kempalab.msdps.constants.MSDatasetOption;
import de.kempalab.msdps.exception.FragmentNotFoundException;
import de.kempalab.msdps.exception.FrequencyTypeMismatchException;
import de.kempalab.msdps.log.MyLogger;
import de.kempalab.msdps.simulation.IsotopePatternSimulator;
import de.kempalab.msdps.visualisation.MSBarChartApplicationWindow;

public class SimulateMixedIncorporations {
	
	public static final MyLogger LOGGER = MyLogger.getLogger(SimulateMixedIncorporations.class);
	
	public static final double INC_C2N = 0.0;
	public static final double INC_C2 = 0.01;
	public static final double INC_N = 0.5;
	public static final double NUMBER_OF_FRAGMENTS = 100000.0;
	public static final Integer PRECISION = 4;
	public static final double MIN_FREQUENCY = 0.003;
	public static final FrequencyType FREQUENCY_TYPE = FrequencyType.MID;
	
	public static final double INC = INC_C2 + INC_C2N + INC_N;
	
	public static void main(String[] args) throws FragmentNotFoundException, FrequencyTypeMismatchException {
		Fragment fragmentC2N = FragmentsDatabase.getFragment(FragmentKey.GLY_276);
		fragmentC2N.changeCapacity("CN");
		Fragment fragmentC2 = FragmentsDatabase.getFragment(FragmentKey.GLY_276);
		fragmentC2.changeCapacity("C");
		Fragment fragmentN = FragmentsDatabase.getFragment(FragmentKey.GLY_276);
		fragmentN.changeCapacity("N");
		
//		Fragment fragmentC2N = FragmentsDatabase.getFragment(FragmentKey.SER_116);
//		fragmentC2N.changeCapacity("C2N");
//		Fragment fragmentC2 = FragmentsDatabase.getFragment(FragmentKey.SER_116);
//		fragmentC2.changeCapacity("C2");
//		Fragment fragmentN = FragmentsDatabase.getFragment(FragmentKey.SER_116);
//		fragmentN.changeCapacity("N");

		IsotopeSet naturalSet = new IsotopeSet(fragmentC2N, NUMBER_OF_FRAGMENTS * (1 - INC), IncorporationType.NATURAL);
		IsotopeSet markedSetC2N = new IsotopeSet(fragmentC2N, NUMBER_OF_FRAGMENTS * (INC_C2N), IncorporationType.EXPERIMENTAL);
		IsotopeSet markedSetC2 = new IsotopeSet(fragmentC2, NUMBER_OF_FRAGMENTS * (INC_C2), IncorporationType.EXPERIMENTAL);
		IsotopeSet markedSetN = new IsotopeSet(fragmentN, NUMBER_OF_FRAGMENTS * (INC_N), IncorporationType.EXPERIMENTAL);
		
		MassSpectrum naturalSpectrum = naturalSet.simulateSpectrum(0);
		MassSpectrum markedSpectrumC2N = markedSetC2N.simulateSpectrum(0);
		MassSpectrum markedSpectrumC2 = markedSetC2.simulateSpectrum(0);
		MassSpectrum markedSpectrumN = markedSetN.simulateSpectrum(0);
		MassSpectrum mixedSpectrum = naturalSpectrum.merge(markedSpectrumC2N);
		mixedSpectrum = mixedSpectrum.merge(markedSpectrumC2);
		mixedSpectrum = mixedSpectrum.merge(markedSpectrumN);
//		MassSpectrum mixedSpectrum = naturalSpectrum.merge(markedSpectrumN);
//		mixedSpectrum = mixedSpectrum.merge(markedSpectrumC2);
		
		naturalSpectrum = IsotopePatternSimulator.prepareSpectrum(naturalSpectrum, PRECISION, PRECISION, MIN_FREQUENCY,
				FREQUENCY_TYPE);
		markedSpectrumC2N = IsotopePatternSimulator.prepareSpectrum(markedSpectrumC2N, PRECISION, PRECISION,
				MIN_FREQUENCY, FREQUENCY_TYPE);
		markedSpectrumC2 = IsotopePatternSimulator.prepareSpectrum(markedSpectrumC2, PRECISION, PRECISION,
				MIN_FREQUENCY, FREQUENCY_TYPE);
		markedSpectrumN = IsotopePatternSimulator.prepareSpectrum(markedSpectrumN, PRECISION, PRECISION, MIN_FREQUENCY,
				FREQUENCY_TYPE);
		mixedSpectrum = IsotopePatternSimulator.prepareSpectrum(mixedSpectrum, PRECISION, PRECISION, MIN_FREQUENCY,
				FREQUENCY_TYPE);
		
		MSShiftDatabase msShiftDatabase = new MSShiftDatabase();
		msShiftDatabase.setIncorporatedTracers("CN,C,N");
		msShiftDatabase.setIncorporationRate(INC);
		msShiftDatabase.setFragmentKey(fragmentC2N.getFragmentKey());
		msShiftDatabase.setNaturalSpectrum(naturalSpectrum);
		msShiftDatabase.setMarkedSpectrum(markedSpectrumC2);
		msShiftDatabase.setMixedSpectrum(mixedSpectrum);
		msShiftDatabase.setFragmentFormula(fragmentC2N.getFormula());
		msShiftDatabase.analyseAllShifts();
		
		LOGGER.info(msShiftDatabase);
		MSBarChartApplicationWindow demo = new MSBarChartApplicationWindow("Bar Demo 1", msShiftDatabase, MSDatasetOption.ALL_SPECTRA);
		demo.pack();
		demo.setVisible(true);
	}
}
