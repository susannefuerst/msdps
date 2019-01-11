package de.kempalab.msdps.tools;

import java.io.IOException;
import java.util.Map.Entry;

import de.kempalab.msdps.ExperimentalIncorporationCapacity;
import de.kempalab.msdps.Fragment;
import de.kempalab.msdps.FragmentList;
import de.kempalab.msdps.FragmentsDatabase;
import de.kempalab.msdps.IsotopeFormula;
import de.kempalab.msdps.MSDatabase;
import de.kempalab.msdps.MSShiftDatabase;
import de.kempalab.msdps.constants.Element;
import de.kempalab.msdps.constants.FrequencyType;
import de.kempalab.msdps.constants.IncorporationType;
import de.kempalab.msdps.constants.Isotope;
import de.kempalab.msdps.constants.MetaboliteKey;
import de.kempalab.msdps.data.DataTable;
import de.kempalab.msdps.data.IncorporationRate;
import de.kempalab.msdps.exception.FrequencyTypeMismatchException;
import de.kempalab.msdps.simulation.IsotopePatternSimulator;
import de.kempalab.msdps.simulation.IsotopePatternSimulatorRequest;
import de.kempalab.msdps.simulation.IsotopePatternSimulatorResponse;

public class IsotopePeakDatabaseCreator {

	public static final MetaboliteKey[] METABOLITES = { MetaboliteKey.GLN };
	public static final FragmentList FRAGMENTS = FragmentsDatabase.getAllFregments();
	public static final double INC_CN = 0.2;
	public static final double INC_C = 0.2;
	public static final double INC_N = 0.2;
	public static final double NUMBER_OF_FRAGMENTS = 100000.0;
	public static final Integer PRECISION = 4;
	public static final double MIN_FREQUENCY = 0.1;
	public static final FrequencyType FREQUENCY_TYPE = FrequencyType.RELATIVE;

	public static final double INC = INC_C + INC_CN + INC_N;

	public static void main(String[] args) throws FrequencyTypeMismatchException, IOException {
		DataTable table = new DataTable("ID", "exactMass", "RT", "identity", "formula", "predictedMass", "predictedIntensity", 
				"heavyIsotopes", "incorporatedC", "incorporatedN");
		for (Fragment fragment : FRAGMENTS) {
			ExperimentalIncorporationCapacity capacity = fragment.getExperimentalIncorporationCapacity();
			if (capacity.isEmpty()) {
				continue;
			}
			IsotopePatternSimulatorRequest simulatorRequest = new IsotopePatternSimulatorRequest();
			simulatorRequest.setFragments(new FragmentList(fragment));
			simulatorRequest.setIncorporationRate(new IncorporationRate(0.5));
			simulatorRequest.setMinimalFrequency(MIN_FREQUENCY);
			simulatorRequest.setAnalyzeMassShifts(true);
			simulatorRequest.setTotalNumberOfFragments(NUMBER_OF_FRAGMENTS);
			simulatorRequest.setRoundedMassPrecision(PRECISION);
			simulatorRequest.setTargetFrequencyType(FREQUENCY_TYPE);
			if (capacity.get(Element.C) == null || capacity.get(Element.N) == null) {
				IsotopePatternSimulatorResponse response = IsotopePatternSimulator.simulate(simulatorRequest);
				MSDatabase msDatabase = response.getMsDatabaseList().get(0);
				addRows(msDatabase, table, fragment);
			} else {
				simulatorRequest.setTracer1(Element.C);
				simulatorRequest.setTracer2(Element.N);
				simulatorRequest.setTracer1Inc(new IncorporationRate(INC_C));
				simulatorRequest.setTracer2Inc(new IncorporationRate(INC_N));
				simulatorRequest.setTracerAllInc(new IncorporationRate(INC_CN));
				IsotopePatternSimulatorResponse response = IsotopePatternSimulator
						.simulateIndependentTracerIncorporation(simulatorRequest);
				MSDatabase msDatabase = response.getMsDatabaseList().get(0);
				addRows(msDatabase, table, fragment);
			}
		}
		table.writeToCsv("N/A", true, "Z:\\data\\db\\predictedFragmentIsotopePattern.csv");
	}

	private static void addRows(MSDatabase msDatabase, DataTable table, Fragment fragment) {
		String moleculeName = fragment.getFragmentKey().getMetaboliteKey().getMoleculeName();
		String baseID = moleculeName + "_" + fragment.getDerivate() + "_" + fragment.baseMass();
		String rt = "NA";
		String identity = moleculeName + "_" + fragment.baseMass();
		String formula = fragment.getFormula();
		int entryCount = 0;
		for (Entry<Double, Double> entry : msDatabase.getMixedSpectrum().entrySet()) {
			String id = baseID + "_" + entryCount;
			Double exactMass = entry.getKey();
			String mass = exactMass.toString();
			String predictedIntensity = entry.getValue().toString();
			IsotopeFormula shiftInducingIsotopes = ((MSShiftDatabase) msDatabase)
					.shiftInducingIsotopes(IncorporationType.MIXED, exactMass);
			String heavyIsotopes = shiftInducingIsotopes.toSimpleString();
			String c = shiftInducingIsotopes.get(Isotope.C_13) != null
					? shiftInducingIsotopes.get(Isotope.C_13).toString()
					: "0";
			String n = shiftInducingIsotopes.get(Isotope.N_15) != null
					? shiftInducingIsotopes.get(Isotope.N_15).toString()
					: "0";
			table.addRow(id, mass, rt, identity, formula, mass, predictedIntensity, heavyIsotopes, c, n);
			entryCount++;
		}

	}

}
