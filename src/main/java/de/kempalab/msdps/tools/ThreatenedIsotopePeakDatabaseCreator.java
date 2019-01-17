package de.kempalab.msdps.tools;

import java.io.IOException;
import java.util.ArrayList;

import de.kempalab.msdps.ExperimentalIncorporationCapacity;
import de.kempalab.msdps.Fragment;
import de.kempalab.msdps.FragmentList;
import de.kempalab.msdps.FragmentsDatabase;
import de.kempalab.msdps.constants.Element;
import de.kempalab.msdps.constants.FragmentKey;
import de.kempalab.msdps.constants.IntensityType;
import de.kempalab.msdps.constants.MetaboliteKey;
import de.kempalab.msdps.data.DataTable;
import de.kempalab.msdps.data.IncorporationRate;
import de.kempalab.msdps.exception.FragmentNotFoundException;
import de.kempalab.msdps.simulation.IsotopePatternSimulatorRequest;

public class ThreatenedIsotopePeakDatabaseCreator {

	public static final MetaboliteKey[] METABOLITES = { MetaboliteKey.GLN };
//	public static final FragmentList FRAGMENTS = FragmentsDatabase.getAllFregments();
//	public static final FragmentList FRAGMENTS = FragmentsDatabase.getFragments(METABOLITES); 
	public static final double INC_CN = 0.2;
	public static final double INC_C = 0.2;
	public static final double INC_N = 0.2;
	public static final double NUMBER_OF_FRAGMENTS = 100000.0;
	public static final Integer PRECISION = 4;
	public static final double MIN_FREQUENCY = 0.1;
	public static final IntensityType FREQUENCY_TYPE = IntensityType.RELATIVE;
	public static final double INC = INC_C + INC_CN + INC_N;

	public static void main(String[] args) throws InterruptedException, IOException, FragmentNotFoundException {
		FragmentList FRAGMENTS = new FragmentList(FragmentsDatabase.getFragment(FragmentKey.GLN_156));
		ArrayList<Thread> threads = new ArrayList<>();
		DataTable table = new DataTable("ID", "exactMass", "RT", "identity", "formula", "predictedMass",
				"predictedIntensity", "heavyIsotopes", "incorporatedC", "incorporatedN");
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
			simulatorRequest.setTracer1(Element.C);
			simulatorRequest.setTracer2(Element.N);
			simulatorRequest.setTracer1Inc(new IncorporationRate(INC_C));
			simulatorRequest.setTracer2Inc(new IncorporationRate(INC_N));
			simulatorRequest.setTracerAllInc(new IncorporationRate(INC_CN));
			Thread thread = new Thread(new IsotopePeakPredictor(simulatorRequest, table),
					fragment.getFragmentKey().name());
			thread.start();
			threads.add(thread);
			
		}
		for (Thread thread : threads) {
			thread.join();
		}
		table.writeToCsv("N/A", true, "Z:\\data\\db\\gln_test.csv");
	}
}
