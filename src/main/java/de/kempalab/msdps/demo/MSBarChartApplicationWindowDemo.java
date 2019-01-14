package de.kempalab.msdps.demo;

import de.kempalab.msdps.Fragment;
import de.kempalab.msdps.FragmentList;
import de.kempalab.msdps.FragmentsDatabase;
import de.kempalab.msdps.MSDatabase;
import de.kempalab.msdps.constants.FragmentKey;
import de.kempalab.msdps.constants.FrequencyType;
import de.kempalab.msdps.constants.MSBarChartType;
import de.kempalab.msdps.data.IncorporationRate;
import de.kempalab.msdps.exception.FragmentNotFoundException;
import de.kempalab.msdps.exception.FrequencyTypeMismatchException;
import de.kempalab.msdps.log.MyLogger;
import de.kempalab.msdps.simulation.IsotopePatternSimulator;
import de.kempalab.msdps.simulation.IsotopePatternSimulatorRequest;
import de.kempalab.msdps.simulation.IsotopePatternSimulatorResponse;
import de.kempalab.msdps.visualisation.MSBarChartApplicationWindow;

public class MSBarChartApplicationWindowDemo {
	
	public static final MyLogger LOGGER = MyLogger.getLogger(MSBarChartApplicationWindowDemo.class);
	
	public static void main(String[] args) throws FrequencyTypeMismatchException, FragmentNotFoundException {
		IsotopePatternSimulatorRequest simulatorRequest = new IsotopePatternSimulatorRequest();
		Fragment fragment = FragmentsDatabase.getFragment(FragmentKey.GLN_156);
		fragment.changeCapacity("C4");
		simulatorRequest.setFragments(new FragmentList(fragment));
		simulatorRequest.setIncorporationRate(new IncorporationRate(0.5));
		simulatorRequest.setMinimalFrequency(0.01);
		simulatorRequest.setAnalyzeMassShifts(true);
		simulatorRequest.setTotalNumberOfFragments(10000.0);
		simulatorRequest.setRoundedMassPrecision(4);
		simulatorRequest.setTargetFrequencyType(FrequencyType.RELATIVE);
		IsotopePatternSimulatorResponse response = IsotopePatternSimulator.simulate(simulatorRequest);
		MSDatabase msDatabase =  response.getMsDatabaseList().get(0);
		LOGGER.info(msDatabase);
		MSBarChartApplicationWindow demo = new MSBarChartApplicationWindow("Bar Demo 1", msDatabase,
				MSBarChartType.NATURAL_SPECTRUM_ONLY);
		demo.pack();
		demo.setVisible(true);
	}
}
