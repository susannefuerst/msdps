package de.kempalab.msdps.tools;

import java.util.Map.Entry;

import de.kempalab.msdps.Fragment;
import de.kempalab.msdps.IsotopeFormula;
import de.kempalab.msdps.MSDatabase;
import de.kempalab.msdps.MSShiftDatabase;
import de.kempalab.msdps.MassSpectrum;
import de.kempalab.msdps.constants.IncorporationType;
import de.kempalab.msdps.constants.Isotope;
import de.kempalab.msdps.data.DataTable;
import de.kempalab.msdps.exception.TypeMismatchException;
import de.kempalab.msdps.log.MyLogger;
import de.kempalab.msdps.simulation.IsotopePatternSimulator;
import de.kempalab.msdps.simulation.IsotopePatternSimulatorRequest;
import de.kempalab.msdps.simulation.IsotopePatternSimulatorResponse;

public class IsotopePeakPredictor2 implements Runnable {
	public static final MyLogger LOGGER = MyLogger.getLogger(IsotopePeakPredictor2.class);

	DataTable table;
	IsotopePatternSimulatorRequest request;

	public IsotopePeakPredictor2(IsotopePatternSimulatorRequest request, DataTable table) {
		this.request = request;
		this.table = table;
	}

	@Override
	public void run() {
		Fragment fragment = request.getFragments().get(0);
		LOGGER.info("Started " + fragment.getFragmentKey().name());
		IsotopePatternSimulatorResponse response;
		try {
			response = IsotopePatternSimulator.simulate(request);
			MSDatabase msDatabase = response.getMsDatabaseList().get(0);
			synchronized (table) {
				addRows(msDatabase, table, fragment);
			}
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		}
		LOGGER.info("Terminated " + fragment.getFragmentKey().name());
	}

	private static void addRows(MSDatabase msDatabase, DataTable table, Fragment fragment) {
		String moleculeName = fragment.getFragmentKey().getMetaboliteKey().getMoleculeName();
		String baseID = moleculeName + fragment.getDerivate() + fragment.baseMass();
		Double retentionTime = fragment.getRetentionTime();
		String rt = retentionTime != null ? String.valueOf(retentionTime) : "NA";
		String identity = moleculeName + fragment.getDerivate() + "_" + fragment.baseMass();
		String formula = fragment.getFormula().toSimpleString();
		int entryCount = 0;
		MassSpectrum spectrum = msDatabase.getNaturalSpectrum();
		for (Entry<Double, Double> entry : spectrum.entrySet()) {
			String id = baseID + "_" + format(entryCount);
			Double exactMass = entry.getKey();
			String mass = exactMass.toString();
			String predictedIntensity = entry.getValue().toString();
			IsotopeFormula shiftInducingIsotopes;
			if(spectrum.getCompositions().isEmpty()) {
				shiftInducingIsotopes = ((MSShiftDatabase) msDatabase)
						.shiftInducingIsotopes(IncorporationType.MIXED, exactMass);
			} else {
				shiftInducingIsotopes = spectrum.getComposition(exactMass).getHeavyIsotopes();
			}
			if (shiftInducingIsotopes == null
					|| shiftInducingIsotopes.isEmpty()
					|| shiftInducingIsotopes.containsKey(Isotope.NONE)
					|| shiftInducingIsotopes.containsKey(Isotope.C_13)
					|| shiftInducingIsotopes.containsKey(Isotope.N_15)
					|| shiftInducingIsotopes.containsKey(Isotope.Si_29)
					|| shiftInducingIsotopes.containsKey(Isotope.Si_30)) {
				String heavyIsotopes = shiftInducingIsotopes.toSimpleString();
				String c = shiftInducingIsotopes.get(Isotope.C_13) != null
						? shiftInducingIsotopes.get(Isotope.C_13).toString() : "0";
				String n = shiftInducingIsotopes.get(Isotope.N_15) != null
						? shiftInducingIsotopes.get(Isotope.N_15).toString() : "0";
				table.addRow(id, mass, rt, identity + "_" + c +"_" + n, formula, mass, predictedIntensity, heavyIsotopes, c, n);
				entryCount++;
			}
		}
	}

	private static String format(int entryCount) {
		if (entryCount < 10) {
			return "0" + entryCount;
		}
		return String.valueOf(entryCount);
	}

}
