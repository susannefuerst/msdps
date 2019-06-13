package de.kempalab.msdps.tools;

import java.io.IOException;
import java.util.ArrayList;

import de.kempalab.msdps.ElementFormula;
import de.kempalab.msdps.Fragment;
import de.kempalab.msdps.FragmentList;
import de.kempalab.msdps.FragmentsDatabase;
import de.kempalab.msdps.constants.IntensityType;
import de.kempalab.msdps.constants.MetaboliteKey;
import de.kempalab.msdps.constants.PathConstants;
import de.kempalab.msdps.data.DataTable;
import de.kempalab.msdps.data.IncorporationRate;
import de.kempalab.msdps.exception.FragmentNotFoundException;
import de.kempalab.msdps.simulation.IsotopePatternSimulatorRequest;

public class ThreatenedIsotopePeakDatabaseCreator2 {

	public static final MetaboliteKey[] METABOLITES = { 
			MetaboliteKey.GLN, MetaboliteKey.GLU, MetaboliteKey.ALA, MetaboliteKey.ASP,
			MetaboliteKey.ASN, MetaboliteKey.SER, MetaboliteKey.GLY
			};
//	public static final FragmentList FRAGMENTS = FragmentsDatabase.getAllFregments();
	public static final FragmentList FRAGMENTS = FragmentsDatabase.getFragments(METABOLITES);
	public static final double NUMBER_OF_FRAGMENTS = 100000.0;
	public static final Integer PRECISION = 4;
	public static final double MIN_FREQUENCY = 0.0;
	public static final IntensityType FREQUENCY_TYPE = IntensityType.RELATIVE;
	public static final double INC = 0.0;

	public static void main(String[] args) throws InterruptedException, IOException, FragmentNotFoundException {
//		FragmentList FRAGMENTS = new FragmentList(FragmentsDatabase.getFragment(FragmentKey.PYR_189));
		ArrayList<Thread> threads = new ArrayList<>();
		DataTable table = new DataTable("ID", "exactMass", "RT", "identity", "formula", "predictedMass",
				"predictedIntensity", "heavyIsotopes", "incorporatedC", "incorporatedN");
		for (Fragment fragment : FRAGMENTS) {
			ElementFormula capacity = fragment.getTracerCapacity();
			if (capacity.isEmpty()) {
				continue;
			}
			IsotopePatternSimulatorRequest simulatorRequest = new IsotopePatternSimulatorRequest();
			simulatorRequest.setFragments(new FragmentList(fragment));
			simulatorRequest.setIncorporationRate(new IncorporationRate(INC));
			simulatorRequest.setMinimalIntensity(MIN_FREQUENCY);
			simulatorRequest.setAnalyzeMassShifts(false);
			simulatorRequest.setTotalNumberOfFragments(NUMBER_OF_FRAGMENTS);
			simulatorRequest.setRoundedMassPrecision(PRECISION);
			simulatorRequest.setTargetIntensityType(FREQUENCY_TYPE);
			Thread thread = new Thread(new IsotopePeakPredictor2(simulatorRequest, table),
					fragment.getFragmentKey().name());
			thread.start();
			threads.add(thread);

		}
		for (Thread thread : threads) {
			thread.join();
		}
		table.writeToCsv("N/A", true, PathConstants.FILE_OUTPUT_FOLDER.toAbsolutePath() + "db\\aminoacids.csv");
	}
}
