package de.kempalab.msdps.tools;

import java.util.Map.Entry;

import de.kempalab.msdps.ExperimentalIncorporationCapacity;
import de.kempalab.msdps.Fragment;
import de.kempalab.msdps.FragmentList;
import de.kempalab.msdps.FragmentsDatabase;
import de.kempalab.msdps.IsotopeFormula;
import de.kempalab.msdps.IsotopeSet;
import de.kempalab.msdps.MSDatabase;
import de.kempalab.msdps.MSShiftDatabase;
import de.kempalab.msdps.MassSpectrum;
import de.kempalab.msdps.constants.FrequencyType;
import de.kempalab.msdps.constants.IncorporationType;
import de.kempalab.msdps.constants.MetaboliteKey;
import de.kempalab.msdps.data.DataTable;
import de.kempalab.msdps.data.IncorporationRate;
import de.kempalab.msdps.exception.FrequencyTypeMismatchException;
import de.kempalab.msdps.simulation.IsotopePatternSimulator;
import de.kempalab.msdps.simulation.IsotopePatternSimulatorRequest;
import de.kempalab.msdps.simulation.IsotopePatternSimulatorResponse;
import net.sf.mzmine.modules.isotopeincorporation.simulation.data.constants.Isotope;

public class IsotopePeakDatabaseCreator {

	public static final MetaboliteKey[] METABOLITES = { MetaboliteKey.GLN };
	public static final FragmentList FRAGMENTS = FragmentsDatabase.getFragments(METABOLITES);
	public static final double INC_CN = 0.2;
	public static final double INC_C = 0.2;
	public static final double INC_N = 0.2;
	public static final double NUMBER_OF_FRAGMENTS = 100000.0;
	public static final Integer PRECISION = 4;
	public static final double MIN_FREQUENCY = 0.1;
	public static final FrequencyType FREQUENCY_TYPE = FrequencyType.RELATIVE;

	public static final double INC = INC_C + INC_CN + INC_N;

	public static void main(String[] args) throws FrequencyTypeMismatchException {
		DataTable table = new DataTable("ID", "exactMass", "RT", "identity", "formula", "predictedMass", "predictedIntensity", 
				"heavyIsotopes", "incorporatedC", "incorporatedN");
		for (Fragment fragment : FRAGMENTS) {
			ExperimentalIncorporationCapacity capacity = fragment.getExperimentalIncorporationCapacity();
			if (capacity.isEmpty()) {
				continue;
			}
			String baseID = fragment.getFragmentKey().getMetaboliteKey().getMoleculeName() + "_"
					+ fragment.getDerivate() + "_" + fragment.baseMass();
			String rt = "NA";
			String identity = fragment.getFragmentKey().getMetaboliteKey().getMoleculeName() + "_"
					+ fragment.baseMass();
			String formula = fragment.getFormula();
			if (capacity.get("C") == null || capacity.get("N") == null) {
				IsotopePatternSimulatorRequest simulatorRequest = new IsotopePatternSimulatorRequest();
				simulatorRequest.setFragments(new FragmentList(fragment));
				simulatorRequest.setIncorporationRate(new IncorporationRate(0.5));
				simulatorRequest.setMinimalRelativeFrequency(MIN_FREQUENCY);
				simulatorRequest.setAnalyzeMassShifts(true);
				simulatorRequest.setTotalNumberOfFragments(NUMBER_OF_FRAGMENTS);
				simulatorRequest.setRoundedMassPrecision(PRECISION);
				simulatorRequest.setTargetFrequencyType(FREQUENCY_TYPE);
				IsotopePatternSimulatorResponse response = IsotopePatternSimulator.simulate(simulatorRequest);
				MSDatabase msDatabase = response.getMsDatabaseList().get(0);
				MassSpectrum mixedSpectrum = msDatabase.getMixedSpectrum();
				int entryCount = 0;
				for (Entry<Double, Double> entry : mixedSpectrum.entrySet()) {
					String id = baseID + entryCount;
					Double exactMass = entry.getKey();
					Double predictedMass = exactMass;
					Double predictedIntensity = entry.getValue();
					IsotopeFormula shiftInducingIsotopes = ((MSShiftDatabase) msDatabase).shiftInducingIsotopes(IncorporationType.MIXED, exactMass);
					String heavyIsotopes = shiftInducingIsotopes.toSimpleString();
					Integer c = shiftInducingIsotopes.get(Isotope.C_13) != null ? shiftInducingIsotopes.get(Isotope.C_13) : 0;
					Integer n = shiftInducingIsotopes.get(Isotope.N_15) != null ? shiftInducingIsotopes.get(Isotope.N_15) : 0;
					table.addColumn(id);
					table.addColumn(exactMass.toString());
					table.addColumn("N/A");
					table.addColumn(identity);
					table.addColumn(formula);
					table.addColumn(predictedMass.toString());
					table.addColumn(predictedIntensity.toString());
					table.addColumn(heavyIsotopes);
					table.addColumn(c.toString());
					table.addColumn(n.toString());
					entryCount++;
				}
			} else {
				String cCapacity = "C" + capacity.get("C");
				String nCapacity = "N" + capacity.get("N");
				Fragment fragmentCN = fragment.copy();
				Fragment fragmentC = fragment.copy();
				fragmentC.changeCapacity(cCapacity);
				Fragment fragmentN = fragment.copy();
				fragmentN.changeCapacity(nCapacity);

				IsotopeSet naturalSet = new IsotopeSet(fragmentCN, NUMBER_OF_FRAGMENTS * (1 - INC),
						IncorporationType.NATURAL);
				IsotopeSet markedSetCN = new IsotopeSet(fragmentCN, NUMBER_OF_FRAGMENTS * (INC_CN),
						IncorporationType.EXPERIMENTAL);
				IsotopeSet markedSetC = new IsotopeSet(fragmentC, NUMBER_OF_FRAGMENTS * (INC_C),
						IncorporationType.EXPERIMENTAL);
				IsotopeSet markedSetN = new IsotopeSet(fragmentN, NUMBER_OF_FRAGMENTS * (INC_N),
						IncorporationType.EXPERIMENTAL);

				MassSpectrum naturalSpectrum = naturalSet.simulateSpectrum(1);
				MassSpectrum markedSpectrumCN = markedSetCN.simulateSpectrum(1);
				MassSpectrum markedSpectrumC = markedSetC.simulateSpectrum(1);
				MassSpectrum markedSpectrumN = markedSetN.simulateSpectrum(1);
				MassSpectrum mixedSpectrum = naturalSpectrum.merge(markedSpectrumCN);
				mixedSpectrum = mixedSpectrum.merge(markedSpectrumC);
				mixedSpectrum = mixedSpectrum.merge(markedSpectrumN);

				naturalSpectrum = IsotopePatternSimulator.prepareSpectrum(naturalSpectrum, PRECISION, PRECISION,
						MIN_FREQUENCY, FREQUENCY_TYPE);
				markedSpectrumCN = IsotopePatternSimulator.prepareSpectrum(markedSpectrumCN, PRECISION, PRECISION,
						MIN_FREQUENCY, FREQUENCY_TYPE);
				markedSpectrumC = IsotopePatternSimulator.prepareSpectrum(markedSpectrumC, PRECISION, PRECISION,
						MIN_FREQUENCY, FREQUENCY_TYPE);
				markedSpectrumN = IsotopePatternSimulator.prepareSpectrum(markedSpectrumN, PRECISION, PRECISION,
						MIN_FREQUENCY, FREQUENCY_TYPE);
				mixedSpectrum = IsotopePatternSimulator.prepareSpectrum(mixedSpectrum, PRECISION, PRECISION,
						MIN_FREQUENCY, FREQUENCY_TYPE);

				MSShiftDatabase msShiftDatabase = new MSShiftDatabase();
				msShiftDatabase.setIncorporatedTracers("CN,C,N");
				msShiftDatabase.setIncorporationRate(INC);
				msShiftDatabase.setFragmentKey(fragmentCN.getFragmentKey());
				msShiftDatabase.setNaturalSpectrum(naturalSpectrum);
				msShiftDatabase.setMarkedSpectrum(markedSpectrumC);
				msShiftDatabase.setMixedSpectrum(mixedSpectrum);
				msShiftDatabase.setFragmentFormula(fragmentCN.getFormula());

				int entryCount = 0;
				for (Entry<Double, Double> entry : mixedSpectrum.entrySet()) {
					String id = baseID + entryCount;
					Double exactMass = entry.getKey();
					Double predictedMass = exactMass;
					Double predictedIntensity = entry.getValue();
					IsotopeFormula shiftInducingIsotopes = ((MSShiftDatabase) msShiftDatabase)
							.shiftInducingIsotopes(IncorporationType.MIXED, exactMass);
					String heavyIsotopes = shiftInducingIsotopes.toSimpleString();
					Integer c = shiftInducingIsotopes.get(Isotope.C_13) != null
							? shiftInducingIsotopes.get(Isotope.C_13)
							: 0;
					Integer n = shiftInducingIsotopes.get(Isotope.N_15) != null
							? shiftInducingIsotopes.get(Isotope.N_15)
							: 0;
					table.addColumn(id);
					table.addColumn(exactMass.toString());
					table.addColumn("N/A");
					table.addColumn(identity);
					table.addColumn(formula);
					table.addColumn(predictedMass.toString());
					table.addColumn(predictedIntensity.toString());
					table.addColumn(heavyIsotopes);
					table.addColumn(c.toString());
					table.addColumn(n.toString());
					entryCount++;
				}
			}
		}
	}

}
