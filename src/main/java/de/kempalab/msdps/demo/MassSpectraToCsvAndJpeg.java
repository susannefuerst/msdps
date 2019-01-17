package de.kempalab.msdps.demo;

import java.io.IOException;

import de.kempalab.msdps.FragmentsDatabase;
import de.kempalab.msdps.MSDatabase;
import de.kempalab.msdps.constants.MSBarChartType;
import de.kempalab.msdps.constants.PathConstants;
import de.kempalab.msdps.data.IncorporationRate;
import de.kempalab.msdps.exception.IntensityTypeMismatchException;
import de.kempalab.msdps.simulation.IsotopePatternSimulator;
import de.kempalab.msdps.simulation.IsotopePatternSimulatorRequest;
import de.kempalab.msdps.simulation.IsotopePatternSimulatorResponse;

public class MassSpectraToCsvAndJpeg {
	
	public static void main(String[] args) throws IntensityTypeMismatchException, IOException {
		IsotopePatternSimulatorRequest simulatorRequest = new IsotopePatternSimulatorRequest();
		simulatorRequest.setFragments(FragmentsDatabase.getAllFregments());
		simulatorRequest.setIncorporationRate(new IncorporationRate(0.6));
		simulatorRequest.setMinimalIntensity(0.002);
		simulatorRequest.setAnalyzeMassShifts(true);
		IsotopePatternSimulatorResponse response = IsotopePatternSimulator.simulate(simulatorRequest);
		for (MSDatabase msDatabase : response.getMsDatabaseList()) {
			String metaboliteAbbreviation = msDatabase.getFragmentKey().getMetaboliteKey().getAbbreviation();
			msDatabase.writeCsv(PathConstants.FILE_OUTPUT_FOLDER.toAbsolutePath(metaboliteAbbreviation + "\\"));
			msDatabase.saveMSCategoryBarChartAsJPEG(PathConstants.FILE_OUTPUT_FOLDER.toAbsolutePath(metaboliteAbbreviation + "\\"), MSBarChartType.ALL_SPECTRA);
		}
	}

}
