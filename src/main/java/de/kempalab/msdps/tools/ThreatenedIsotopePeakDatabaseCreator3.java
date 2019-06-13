package de.kempalab.msdps.tools;

import java.io.IOException;
import java.util.ArrayList;

import de.kempalab.msdps.ElementFormula;
import de.kempalab.msdps.Fragment;
import de.kempalab.msdps.FragmentList;
import de.kempalab.msdps.FragmentsDatabase;
import de.kempalab.msdps.calculation.IsotopePatternCalculatorRequest;
import de.kempalab.msdps.constants.IntensityType;
import de.kempalab.msdps.constants.MetaboliteKey;
import de.kempalab.msdps.constants.PathConstants;
import de.kempalab.msdps.data.DataTable;
import de.kempalab.msdps.exception.FragmentNotFoundException;

public class ThreatenedIsotopePeakDatabaseCreator3 {
//	public static final MetaboliteKey[] METABOLITES = { MetaboliteKey.GLY };
	public static final MetaboliteKey[] METABOLITES = { 
			MetaboliteKey.GLN, MetaboliteKey.GLU, MetaboliteKey.ALA, MetaboliteKey.ASP,
			MetaboliteKey.ASN, MetaboliteKey.SER, MetaboliteKey.GLY
			};
//	public static final FragmentList FRAGMENTS = FragmentsDatabase.getAllFregments();
	public static final FragmentList FRAGMENTS = FragmentsDatabase.getFragments(METABOLITES);
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
			IsotopePatternCalculatorRequest calculatorRequest = new IsotopePatternCalculatorRequest();
			calculatorRequest.setFragments(new FragmentList(fragment));
			calculatorRequest.setCharge(1);
			calculatorRequest.setRoundedIntensityPrecision(PRECISION);
			calculatorRequest.setRoundedMassPrecision(PRECISION);
			calculatorRequest.setTargetIntensityType(IntensityType.RELATIVE);
			calculatorRequest.setMinimalIntensity(MIN_FREQUENCY);
			calculatorRequest.setAnalyseComposition(false);
			Thread thread = new Thread(new IsotopePeakPredictor3(calculatorRequest, table),
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
