package de.kempalab.msdps.demo;

import de.kempalab.msdps.Fragment;
import de.kempalab.msdps.FragmentList;
import de.kempalab.msdps.FragmentsDatabase;
import de.kempalab.msdps.constants.FragmentKey;
import de.kempalab.msdps.constants.IntensityType;
import de.kempalab.msdps.data.IncorporationRate;
import de.kempalab.msdps.exception.FragmentNotFoundException;
import de.kempalab.msdps.exception.TypeMismatchException;
import de.kempalab.msdps.simulation.IsotopePatternSimulator;
import de.kempalab.msdps.simulation.IsotopePatternSimulatorRequest;
import de.kempalab.msdps.simulation.IsotopePatternSimulatorResponse;

public class IsotopePatternSimulationDemo2 {
	public static void main(String[] args) throws FragmentNotFoundException, TypeMismatchException {
		IsotopePatternSimulatorRequest simulatorRequest = new IsotopePatternSimulatorRequest();
		Fragment fragment = FragmentsDatabase.getFragment(FragmentKey.PYR_174);
		simulatorRequest.setFragments(new FragmentList(fragment));
		simulatorRequest.setAnalyzeMassShifts(false);
		simulatorRequest.setCharge(1);
		simulatorRequest.setRoundedIntensityPrecision(4);
		simulatorRequest.setRoundedMassPrecision(4);
		simulatorRequest.setTargetIntensityType(IntensityType.RELATIVE);
		simulatorRequest.setIncorporationRate(new IncorporationRate(0.6));
		simulatorRequest.setMinimalIntensity(0.1);
		simulatorRequest.setAnalyzeMassShifts(true);
		IsotopePatternSimulatorResponse response = IsotopePatternSimulator.simulate(simulatorRequest);
		System.out.println(response.getMsDatabaseList().get(0).getMixedSpectrum().analyseCompositions(null).toString());
	}
}
