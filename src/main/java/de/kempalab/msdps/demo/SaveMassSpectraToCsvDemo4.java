package de.kempalab.msdps.demo;

import java.io.IOException;

import de.kempalab.msdps.Fragment;
import de.kempalab.msdps.FragmentList;
import de.kempalab.msdps.FragmentsDatabase;
import de.kempalab.msdps.IsotopePattern;
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

public class SaveMassSpectraToCsvDemo4 {
	public static final MyLogger LOGGER = MyLogger.getLogger(SaveMassSpectraToCsvDemo4.class);

	public static void main(String[] args)
			throws IntensityTypeMismatchException, IOException, FragmentNotFoundException {
		IsotopePatternSimulatorRequest simulatorRequest = new IsotopePatternSimulatorRequest();
		Fragment fragment = FragmentsDatabase.getFragment(FragmentKey.ASN_419);
		simulatorRequest.setFragments(new FragmentList(fragment));
		simulatorRequest.setTracer1(Element.C);
		simulatorRequest.setTracer2(Element.N);
		simulatorRequest.setTracer1Inc(new IncorporationRate(0.0));
		simulatorRequest.setTracer2Inc(new IncorporationRate(0.0));
		simulatorRequest.setTracerAllInc(new IncorporationRate(1.0));
		simulatorRequest.setMinimalIntensity(0.1);
		simulatorRequest.setAnalyzeMassShifts(false);
		simulatorRequest.setTotalNumberOfFragments(10000.0);
		simulatorRequest.setRoundedMassPrecision(4);
		simulatorRequest.setTargetIntensityType(IntensityType.RELATIVE);
		simulatorRequest.setCharge(1);
		IsotopePatternSimulatorResponse response = IsotopePatternSimulator
				.simulateIndependentTracerIncorporation(simulatorRequest);

		IsotopePatternSimulatorRequest simulatorRequest2 = new IsotopePatternSimulatorRequest();
		Fragment fragment2 = FragmentsDatabase.getFragment(fragment.getFragmentKey());
		fragment2.changeCapacity("N");
		simulatorRequest2.setFragments(new FragmentList(fragment2));
		simulatorRequest2.setTracer1(Element.C);
		simulatorRequest2.setTracer2(Element.N);
		simulatorRequest2.setTracer1Inc(new IncorporationRate(0.0));
		simulatorRequest2.setTracer2Inc(new IncorporationRate(1.0));
		simulatorRequest2.setTracerAllInc(new IncorporationRate(0.0));
		simulatorRequest2.setMinimalIntensity(0.1);
		simulatorRequest2.setAnalyzeMassShifts(false);
		simulatorRequest2.setTotalNumberOfFragments(10000.0);
		simulatorRequest2.setRoundedMassPrecision(4);
		simulatorRequest2.setTargetIntensityType(IntensityType.RELATIVE);
		simulatorRequest2.setCharge(1);
		IsotopePatternSimulatorResponse response2 = IsotopePatternSimulator
				.simulateIndependentTracerIncorporation(simulatorRequest2);

		MassSpectrum spectrum1 = response.getSpectrum(0);
		MassSpectrum spectrum2 = response2.getSpectrum(0);
		MassSpectrum spectrum = spectrum1.merge(spectrum2);
		spectrum = IsotopePatternSimulator.prepareSpectrum(spectrum, simulatorRequest.getRoundedMassPrecision(),
				simulatorRequest.getRoundedIntensityPrecision(), simulatorRequest.getMinimalIntensity(),
				simulatorRequest.getTargetIntensityType());

		IsotopePattern pattern = new IsotopePattern(spectrum);
		DataTable dataTable = pattern.toDataTable();
		dataTable.addHeader("Compound");
		dataTable.addConstantValueColumn(fragment.getFragmentKey().name());
		dataTable.addHeader("Tracer");
		dataTable.addConstantValueColumn("");
		dataTable.addHeader("IncRate");
		dataTable.addConstantValueColumn("");
		dataTable.writeToCsv("N/A", true, PathConstants.FILE_OUTPUT_FOLDER
				.toAbsolutePath(fragment.getFragmentKey().getMetaboliteKey().getAbbreviation() + "\\asn_419_mixC"));
		LOGGER.info(dataTable.toString("N/A", true));

	}

}
