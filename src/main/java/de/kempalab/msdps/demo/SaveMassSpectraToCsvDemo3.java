package de.kempalab.msdps.demo;

import java.io.IOException;

import de.kempalab.msdps.Fragment;
import de.kempalab.msdps.FragmentList;
import de.kempalab.msdps.FragmentsDatabase;
import de.kempalab.msdps.IsotopePattern;
import de.kempalab.msdps.MSDatabase;
import de.kempalab.msdps.MassSpectrum;
import de.kempalab.msdps.constants.Element;
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

public class SaveMassSpectraToCsvDemo3 {
	public static final MyLogger LOGGER = MyLogger.getLogger(MSBarChartApplicationWindowDemo2.class);

	public static void main(String[] args) throws IntensityTypeMismatchException, IOException, FragmentNotFoundException {
		IsotopePatternSimulatorRequest simulatorRequest = new IsotopePatternSimulatorRequest();
		Fragment fragment = FragmentsDatabase.getFragment(FragmentKey.GLN_156);
		fragment.changeCapacity("C4N");
		simulatorRequest.setFragments(new FragmentList(fragment));
		simulatorRequest.setTracer1(Element.C);
		simulatorRequest.setTracer2(Element.N);
		simulatorRequest.setTracer1Inc(new IncorporationRate(0.2));
		simulatorRequest.setTracer2Inc(new IncorporationRate(0.3));
		simulatorRequest.setTracerAllInc(new IncorporationRate(0.1));
		simulatorRequest.setMinimalIntensity(0.003);
		simulatorRequest.setAnalyzeMassShifts(false);
		simulatorRequest.setTotalNumberOfFragments(10000.0);
		simulatorRequest.setRoundedMassPrecision(4);
		simulatorRequest.setTargetIntensityType(IntensityType.MID);
		simulatorRequest.setCharge(1);
		IsotopePatternSimulatorResponse response = IsotopePatternSimulator.simulateIndependentTracerIncorporation(simulatorRequest);
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
					.toAbsolutePath(msDatabase.getFragmentKey().getMetaboliteKey().getAbbreviation() + "\\gln_156_inc_20c4_30n_10c4n"));
		}
	}

}