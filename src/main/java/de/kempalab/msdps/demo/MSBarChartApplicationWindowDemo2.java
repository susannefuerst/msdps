package de.kempalab.msdps.demo;

import de.kempalab.msdps.Fragment;
import de.kempalab.msdps.FragmentList;
import de.kempalab.msdps.FragmentsDatabase;
import de.kempalab.msdps.MSDatabase;
import de.kempalab.msdps.constants.FragmentKey;
import de.kempalab.msdps.constants.MSBarChartType;
import de.kempalab.msdps.data.IncorporationRate;
import de.kempalab.msdps.exception.FragmentNotFoundException;
import de.kempalab.msdps.exception.FrequencyTypeMismatchException;
import de.kempalab.msdps.log.MyLogger;
import de.kempalab.msdps.simulation.IsotopePatternSimulator;
import de.kempalab.msdps.simulation.IsotopePatternSimulatorRequest;
import de.kempalab.msdps.simulation.IsotopePatternSimulatorResponse;
import de.kempalab.msdps.visualisation.MSBarChartApplicationWindow;

public class MSBarChartApplicationWindowDemo2 {

public static final MyLogger LOGGER = MyLogger.getLogger(MSBarChartApplicationWindowDemo2.class);
	
	public static void main(String[] args) throws FrequencyTypeMismatchException, FragmentNotFoundException {
		IsotopePatternSimulatorRequest simulatorRequest = new IsotopePatternSimulatorRequest();
		Fragment fragment1 = FragmentsDatabase.getFragment(FragmentKey.ALA_116);
		Fragment fragment2 = FragmentsDatabase.getFragment(FragmentKey.ALA_116);
		fragment2.changeCapacity("C2");
		Fragment fragment3 = FragmentsDatabase.getFragment(FragmentKey.ALA_116);
		fragment3.changeCapacity("N");
		simulatorRequest.setFragments(new FragmentList(fragment1, fragment2, fragment3));
		simulatorRequest.setIncorporationRate(new IncorporationRate(0.6));
		simulatorRequest.setMinimalRelativeFrequency(0.001);
		simulatorRequest.setAnalyzeMassShifts(true);
		simulatorRequest.setTotalNumberOfFragments(10000.0);
		IsotopePatternSimulatorResponse response = IsotopePatternSimulator.simulate(simulatorRequest);
		for (MSDatabase msDatabase : response.getMsDatabaseList()) {
			LOGGER.info(msDatabase);
			MSBarChartApplicationWindow demo = new MSBarChartApplicationWindow("Bar Demo 1", msDatabase, MSBarChartType.ALL_SPECTRA);
			demo.pack();
			demo.setVisible(true);
		}
	}
}
