package de.kempalab.msdps.demo;

import java.io.IOException;

import de.kempalab.msdps.Fragment;
import de.kempalab.msdps.FragmentList;
import de.kempalab.msdps.MSDatabase;
import de.kempalab.msdps.constants.FragmentKey;
import de.kempalab.msdps.constants.MSDatasetOption;
import de.kempalab.msdps.constants.PathConstants;
import de.kempalab.msdps.data.IncorporationRate;
import de.kempalab.msdps.exception.FrequencyTypeMismatchException;
import de.kempalab.msdps.simulation.IsotopePatternSimulator;
import de.kempalab.msdps.simulation.IsotopePatternSimulatorRequest;
import de.kempalab.msdps.simulation.IsotopePatternSimulatorResponse;

public class SaveMassSpectraToJpegDemo {
	public static void main(String[] args) throws FrequencyTypeMismatchException, IOException {
		IsotopePatternSimulatorRequest simulatorRequest = new IsotopePatternSimulatorRequest();
		Fragment fragment = new Fragment(FragmentKey.UNKNOWN, "CO2", "C");
		simulatorRequest.setFragments(new FragmentList(fragment));
		simulatorRequest.setIncorporationRate(new IncorporationRate(0.6));
		simulatorRequest.setMinimalFrequency(0.002);
		simulatorRequest.setAnalyzeMassShifts(true);
		IsotopePatternSimulatorResponse response = IsotopePatternSimulator.simulate(simulatorRequest);
		MSDatabase msDatabase = response.getMsDatabaseList().get(0);
		msDatabase.saveMSCategoryBarChartAsJPEG(PathConstants.FILE_OUTPUT_FOLDER.toAbsolutePath(fragment.metaboliteAbbreviation() + "\\"), MSDatasetOption.ALL_SPECTRA);
	}

}
