package de.kempalab.msdps.demo;

import java.io.IOException;

import de.kempalab.msdps.FragmentsDatabase;
import de.kempalab.msdps.MSDatabase;
import de.kempalab.msdps.constants.PathConstants;
import de.kempalab.msdps.data.IncorporationRate;
import de.kempalab.msdps.exception.FrequencyTypeMismatchException;
import de.kempalab.msdps.simulation.IsotopePatternSimulator;
import de.kempalab.msdps.simulation.IsotopePatternSimulatorRequest;
import de.kempalab.msdps.simulation.IsotopePatternSimulatorResponse;

public class SaveMassSpectraToCsvDemo {
	public static void main(String[] args) throws FrequencyTypeMismatchException, IOException {
		IsotopePatternSimulatorRequest simulatorRequest = new IsotopePatternSimulatorRequest();
		simulatorRequest.setFragments(FragmentsDatabase.getAllFregments());
		simulatorRequest.setIncorporationRate(new IncorporationRate(0.6));
		simulatorRequest.setMinimalRelativeFrequency(0.002);
		simulatorRequest.setAnalyzeMassShifts(true);
		IsotopePatternSimulatorResponse response = IsotopePatternSimulator.simulate(simulatorRequest);
		for (MSDatabase msDatabase : response.getMsDatabaseList()) {
			msDatabase.writeCsv(PathConstants.FILE_OUTPUT_FOLDER.toAbsolutePath(msDatabase.getFragmentKey().getMetaboliteKey().getAbbreviation() + "\\"));
		}
	}

}
