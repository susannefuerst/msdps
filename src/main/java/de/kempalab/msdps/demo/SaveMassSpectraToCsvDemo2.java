package de.kempalab.msdps.demo;

import java.io.IOException;

import de.kempalab.msdps.Fragment;
import de.kempalab.msdps.FragmentList;
import de.kempalab.msdps.FragmentsDatabase;
import de.kempalab.msdps.IsotopePattern;
import de.kempalab.msdps.MSDatabase;
import de.kempalab.msdps.MassSpectrum;
import de.kempalab.msdps.constants.FragmentKey;
import de.kempalab.msdps.constants.IntensityType;
import de.kempalab.msdps.constants.PathConstants;
import de.kempalab.msdps.data.DataTable;
import de.kempalab.msdps.data.IncorporationRate;
import de.kempalab.msdps.exception.FragmentNotFoundException;
import de.kempalab.msdps.exception.IntensityTypeMismatchException;
import de.kempalab.msdps.log.MyLogger;
import de.kempalab.msdps.simulation.IsotopePatternSimulator;
import de.kempalab.msdps.simulation.IsotopePatternSimulatorRequest;
import de.kempalab.msdps.simulation.IsotopePatternSimulatorResponse;

public class SaveMassSpectraToCsvDemo2 {
	public static final MyLogger LOGGER = MyLogger.getLogger(MSBarChartApplicationWindowDemo2.class);

	public static void main(String[] args) throws IntensityTypeMismatchException, IOException, FragmentNotFoundException {
		IsotopePatternSimulatorRequest simulatorRequest = new IsotopePatternSimulatorRequest();
		Fragment fragment = FragmentsDatabase.getFragment(FragmentKey.GLN_156);
		fragment.changeCapacity("C4");
		simulatorRequest.setFragments(new FragmentList(fragment));
		simulatorRequest.setIncorporationRate(new IncorporationRate(0.5));
		simulatorRequest.setMinimalIntensity(0.001);
		simulatorRequest.setAnalyzeMassShifts(false);
		simulatorRequest.setTotalNumberOfFragments(10000.0);
		simulatorRequest.setRoundedMassPrecision(4);
		simulatorRequest.setTargetIntensityType(IntensityType.RELATIVE);
		IsotopePatternSimulatorResponse response = IsotopePatternSimulator.simulate(simulatorRequest);
		for (MSDatabase msDatabase : response.getMsDatabaseList()) {
			LOGGER.info(msDatabase);
			MassSpectrum spectrum = msDatabase.getMixedSpectrum();
			IsotopePattern pattern = new IsotopePattern(spectrum);
			DataTable dataTable = pattern.toDataTable();
			dataTable.addHeader("Compound");
			dataTable.addConstantValueColumn(msDatabase.getFragmentKey().name());
			dataTable.addHeader("Tracer");
			dataTable.addConstantValueColumn(msDatabase.getIncorporatedTracers());
			dataTable.addHeader("IncRate");
			dataTable.addConstantValueColumn(msDatabase.getIncorporationRate());
			dataTable.writeToCsv("N/A", true, PathConstants.FILE_OUTPUT_FOLDER
					.toAbsolutePath(msDatabase.getFragmentKey().getMetaboliteKey().getAbbreviation() + "\\test"));
		}
	}

}