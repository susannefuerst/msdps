package de.kempalab.msdps.demo;

import de.kempalab.msdps.Fragment;
import de.kempalab.msdps.FragmentsDatabase;
import de.kempalab.msdps.IsotopeSet;
import de.kempalab.msdps.MSShiftDatabase;
import de.kempalab.msdps.MassSpectrum;
import de.kempalab.msdps.constants.FragmentKey;
import de.kempalab.msdps.constants.FrequencyType;
import de.kempalab.msdps.constants.IncorporationType;
import de.kempalab.msdps.constants.MSBarChartType;
import de.kempalab.msdps.exception.FragmentNotFoundException;
import de.kempalab.msdps.exception.FrequencyTypeMismatchException;
import de.kempalab.msdps.log.MyLogger;
import de.kempalab.msdps.simulation.IsotopePatternSimulator;
import de.kempalab.msdps.visualisation.MSBarChartApplicationWindow;

public class SimulateMixedIncorporations {
	
	public static final MyLogger LOGGER = MyLogger.getLogger(SimulateMixedIncorporations.class);
	
	public static final double INC_CN = 0.2;
	public static final double INC_C = 0.2;
	public static final double INC_N = 0.2;
	public static final double NUMBER_OF_FRAGMENTS = 100000.0;
	public static final Integer PRECISION = 4;
	public static final double MIN_FREQUENCY = 0.1;
	public static final FrequencyType FREQUENCY_TYPE = FrequencyType.RELATIVE;
	
	public static final double INC = INC_C + INC_CN + INC_N;
	
	public static void main(String[] args) throws FragmentNotFoundException, FrequencyTypeMismatchException {
		Fragment fragmentCN = FragmentsDatabase.getFragment(FragmentKey.GLN_156);
		fragmentCN.changeCapacity("C4N");
		Fragment fragmentC = FragmentsDatabase.getFragment(FragmentKey.GLN_156);
		fragmentC.changeCapacity("C4");
		Fragment fragmentN = FragmentsDatabase.getFragment(FragmentKey.GLN_156);
		fragmentN.changeCapacity("N");

		IsotopeSet naturalSet = new IsotopeSet(fragmentCN, NUMBER_OF_FRAGMENTS * (1 - INC), IncorporationType.NATURAL);
		IsotopeSet markedSetCN = new IsotopeSet(fragmentCN, NUMBER_OF_FRAGMENTS * (INC_CN), IncorporationType.EXPERIMENTAL);
		IsotopeSet markedSetC = new IsotopeSet(fragmentC, NUMBER_OF_FRAGMENTS * (INC_C), IncorporationType.EXPERIMENTAL);
		IsotopeSet markedSetN = new IsotopeSet(fragmentN, NUMBER_OF_FRAGMENTS * (INC_N), IncorporationType.EXPERIMENTAL);
		
		MassSpectrum naturalSpectrum = naturalSet.simulateSpectrum(1);
		MassSpectrum markedSpectrumCN = markedSetCN.simulateSpectrum(1);
		MassSpectrum markedSpectrumC = markedSetC.simulateSpectrum(1);
		MassSpectrum markedSpectrumN = markedSetN.simulateSpectrum(1);
		MassSpectrum mixedSpectrum = naturalSpectrum.merge(markedSpectrumCN);
		mixedSpectrum = mixedSpectrum.merge(markedSpectrumC);
		mixedSpectrum = mixedSpectrum.merge(markedSpectrumN);
//		MassSpectrum mixedSpectrum = naturalSpectrum.merge(markedSpectrumN);
//		mixedSpectrum = mixedSpectrum.merge(markedSpectrumC2);
		
		naturalSpectrum = IsotopePatternSimulator.prepareSpectrum(naturalSpectrum, PRECISION, PRECISION, MIN_FREQUENCY,
				FREQUENCY_TYPE);
		markedSpectrumCN = IsotopePatternSimulator.prepareSpectrum(markedSpectrumCN, PRECISION, PRECISION,
				MIN_FREQUENCY, FREQUENCY_TYPE);
		markedSpectrumC = IsotopePatternSimulator.prepareSpectrum(markedSpectrumC, PRECISION, PRECISION,
				MIN_FREQUENCY, FREQUENCY_TYPE);
		markedSpectrumN = IsotopePatternSimulator.prepareSpectrum(markedSpectrumN, PRECISION, PRECISION, MIN_FREQUENCY,
				FREQUENCY_TYPE);
		mixedSpectrum = IsotopePatternSimulator.prepareSpectrum(mixedSpectrum, PRECISION, PRECISION, MIN_FREQUENCY,
				FREQUENCY_TYPE);
		
		MSShiftDatabase msShiftDatabase = new MSShiftDatabase();
		msShiftDatabase.setIncorporatedTracers("CN,C,N");
		msShiftDatabase.setIncorporationRate(INC);
		msShiftDatabase.setFragmentKey(fragmentCN.getFragmentKey());
		msShiftDatabase.setNaturalSpectrum(naturalSpectrum);
		msShiftDatabase.setMarkedSpectrum(markedSpectrumC);
		msShiftDatabase.setMixedSpectrum(mixedSpectrum);
		msShiftDatabase.setFragmentFormula(fragmentCN.getFormula());
//		msShiftDatabase.analyseAllShifts();
		
		LOGGER.info(msShiftDatabase);
		MSBarChartApplicationWindow demo = new MSBarChartApplicationWindow("Bar Demo 1", msShiftDatabase,
				MSBarChartType.ALL_SPECTRA);
		demo.pack();
		demo.setVisible(true);
	}
}
