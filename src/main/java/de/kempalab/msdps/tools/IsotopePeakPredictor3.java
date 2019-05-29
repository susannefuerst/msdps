package de.kempalab.msdps.tools;

import java.util.Map.Entry;

import de.kempalab.msdps.ElementFormula;
import de.kempalab.msdps.Fragment;
import de.kempalab.msdps.IsotopeComposition;
import de.kempalab.msdps.IsotopeFormula;
import de.kempalab.msdps.IsotopePattern;
import de.kempalab.msdps.MassSpectrum;
import de.kempalab.msdps.calculation.IsotopePatternCalculator;
import de.kempalab.msdps.calculation.IsotopePatternCalculatorRequest;
import de.kempalab.msdps.calculation.IsotopePatternCalculatorResponse;
import de.kempalab.msdps.constants.Element;
import de.kempalab.msdps.constants.Isotope;
import de.kempalab.msdps.data.DataTable;
import de.kempalab.msdps.exception.TypeMismatchException;
import de.kempalab.msdps.log.MyLogger;

public class IsotopePeakPredictor3 implements Runnable {
	public static final MyLogger LOGGER = MyLogger.getLogger(IsotopePeakPredictor3.class);

	DataTable table;
	IsotopePatternCalculatorRequest request;

	public IsotopePeakPredictor3(IsotopePatternCalculatorRequest request, DataTable table) {
		this.request = request;
		this.table = table;
	}

	@Override
	public void run() {
		Fragment fragment = request.getFragments().get(0);
		LOGGER.info("Started " + fragment.getFragmentKey().name());
		IsotopePatternCalculatorResponse response;
		try {
			response = IsotopePatternCalculator.calculateNaturalPattern(request);
			MassSpectrum spectrum = response.getPatternList().get(0);
			synchronized (table) {
				addRows(spectrum, table, fragment);
			}
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		}
		LOGGER.info("Terminated " + fragment.getFragmentKey().name());
	}

	private static void addRows(MassSpectrum spectrum, DataTable table, Fragment fragment) {
		String moleculeName = fragment.getFragmentKey().getMetaboliteKey().getMoleculeName();
		String baseID = moleculeName + fragment.getDerivate() + fragment.baseMass();
		Double retentionTime = fragment.getRetentionTime();
		String rt = retentionTime != null ? String.valueOf(retentionTime) : "NA";
		String identity = moleculeName + fragment.getDerivate() + "_" + fragment.baseMass();
		String formula = fragment.getFormula().toSimpleString();
		int entryCount = 0;
		IsotopePattern pattern = new IsotopePattern(spectrum, true);
		IsotopeComposition composition = pattern.getPeakInducingHeavyIsotopes();
		Double maxShift = fragment.maximalExperimentalShift();
		Double highestMass = spectrum.toEntryList().get(0).getKey() + maxShift + 0.1;
		for (Entry<Double, Double> entry : spectrum.entrySet()) {
			String id = baseID + "_" + format(entryCount);
			Double exactMass = entry.getKey();
			if (exactMass >= highestMass) {
				break;
			}
			String mass = exactMass.toString();
			String predictedIntensity = entry.getValue().toString();
			IsotopeFormula shiftInducingIsotopes = composition.get(exactMass);
			ElementFormula capacity = fragment.getTracerCapacity();
			Double cCapacity = capacity.get(Element.C) != null ? capacity.get(Element.C) : 0.0;
			Double nCapacity = capacity.get(Element.N) != null ? capacity.get(Element.N) : 0.0;
			Double c13 = shiftInducingIsotopes.get(Isotope.C_13) != null ? shiftInducingIsotopes.get(Isotope.C_13) : 0.0;
			Double n15 = shiftInducingIsotopes.get(Isotope.N_15) != null ? shiftInducingIsotopes.get(Isotope.N_15) : 0.0;
			//TODO: make this nicer!!!
			if (c13> cCapacity || n15 > nCapacity) {
				continue;
			}
			if (containsRelevantIsotopes(shiftInducingIsotopes) && !containsIrrelevantIsotopes(shiftInducingIsotopes)) {
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
	
	private static boolean containsRelevantIsotopes(IsotopeFormula isotopes) {
		return (isotopes == null
				|| isotopes.isEmpty()
				|| isotopes.containsKey(Isotope.NONE)
				|| isotopes.containsKey(Isotope.C_13)
				|| isotopes.containsKey(Isotope.N_15)
				|| isotopes.containsKey(Isotope.Si_29)
				|| isotopes.containsKey(Isotope.Si_30));
	}
	
	private static boolean containsIrrelevantIsotopes(IsotopeFormula isotopes) {
		return (isotopes.containsKey(Isotope.O_17)
				|| isotopes.containsKey(Isotope.O_18)
				|| isotopes.containsKey(Isotope.H_2));
	}

	private static String format(int entryCount) {
		if (entryCount < 10) {
			return "0" + entryCount;
		}
		return String.valueOf(entryCount);
	}
}
