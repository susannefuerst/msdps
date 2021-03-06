package de.kempalab.msdps.demo;

import de.kempalab.msdps.Fragment;
import de.kempalab.msdps.FragmentList;
import de.kempalab.msdps.FragmentsDatabase;
import de.kempalab.msdps.constants.FragmentKey;
import de.kempalab.msdps.data.IncorporationRate;
import de.kempalab.msdps.exception.FragmentNotFoundException;
import de.kempalab.msdps.exception.TypeMismatchException;
import de.kempalab.msdps.simulation.IsotopePatternSimulator;
import de.kempalab.msdps.simulation.IsotopePatternSimulatorRequest;

public class IsotopePatternSimulationDemo {
	
	public static void main(String[] args) throws FragmentNotFoundException, TypeMismatchException {
		IsotopePatternSimulatorRequest simulatorRequest = new IsotopePatternSimulatorRequest();
		Fragment fragment = FragmentsDatabase.getFragment(FragmentKey.ASP_130);
		fragment.changeCapacity("N");
//		Fragment fragment = new Fragment(FragmentKey.UNKNOWN, "N10", "N");
		simulatorRequest.setFragments(new FragmentList(fragment));
		simulatorRequest.setIncorporationRate(new IncorporationRate(0.6));
		simulatorRequest.setMinimalIntensity(0.002);
		simulatorRequest.setAnalyzeMassShifts(true);
		IsotopePatternSimulator.simulate(simulatorRequest);
	}
}
