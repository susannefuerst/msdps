package de.kempalab.msdps.correction;

import junit.framework.TestCase;
import de.kempalab.msdps.ElementFormula;
import de.kempalab.msdps.Fragment;
import de.kempalab.msdps.FragmentsDatabase;
import de.kempalab.msdps.IncorporationMap;
import de.kempalab.msdps.IsotopeFormula;
import de.kempalab.msdps.IsotopeList;
import de.kempalab.msdps.IsotopeSet;
import de.kempalab.msdps.MSShiftDatabase;
import de.kempalab.msdps.MassSpectrum;
import de.kempalab.msdps.constants.Element;
import de.kempalab.msdps.constants.FragmentKey;
import de.kempalab.msdps.constants.IncorporationType;
import de.kempalab.msdps.constants.Isotope;
import de.kempalab.msdps.exception.FragmentNotFoundException;
import de.kempalab.msdps.exception.FrequencyTypeMismatchException;
import de.kempalab.msdps.log.MyLogger;
import de.kempalab.msdps.simulation.IsotopePatternSimulator;
import de.kempalab.msdps.util.MathUtils;

public class CorrectionUtilsTest extends TestCase {
	
	public static final MyLogger LOGGER = MyLogger.getLogger(CorrectionUtilsTest.class);
	private static final Double ALLOWED_INC_ERROR = 0.006;
	//some of these tests take a lot of time so do not run them automatically
	private static final boolean TEST_INCS = false;
	
	
	public void testIncorporationRate01() throws FragmentNotFoundException, FrequencyTypeMismatchException {
		if (TEST_INCS) {
//		LOGGER.enableDebug();
//		IncorporationMap.LOG.enableDebug();
			for (int c = 0; c < 10; c++) {
				for (int n = 0; n < 10; n++) {
					for (int cn = 0; cn < 10; cn++) {
						if (cn + c + n >= 10) {
							continue;
						}
						final double INC_CN = 0.0 + cn * 0.1;
						final double INC_C = 0.0 + c * 0.1;
						final double INC_N = 0.0 + n * 0.1;
						final double NUMBER_OF_FRAGMENTS = 100000.0;
						final Integer PRECISION = 4;
						final double MIN_FREQUENCY = 0.001;
						
						final double INC = MathUtils.round(INC_C + INC_CN + INC_N, 2);
						
						Fragment fragmentCN = FragmentsDatabase.getFragment(FragmentKey.GLY_276);
						fragmentCN.changeCapacity("CN");
						Fragment fragmentC = FragmentsDatabase.getFragment(FragmentKey.GLY_276);
						fragmentC.changeCapacity("C");
						Fragment fragmentN = FragmentsDatabase.getFragment(FragmentKey.GLY_276);
						fragmentN.changeCapacity("N");
						
						IsotopeSet naturalSet = new IsotopeSet(fragmentCN, NUMBER_OF_FRAGMENTS * (1 - INC), IncorporationType.NATURAL);
						IsotopeSet markedSetCN = new IsotopeSet(fragmentCN, NUMBER_OF_FRAGMENTS * (INC_CN), IncorporationType.EXPERIMENTAL);
						IsotopeSet markedSetC = new IsotopeSet(fragmentC, NUMBER_OF_FRAGMENTS * (INC_C), IncorporationType.EXPERIMENTAL);
						IsotopeSet markedSetN = new IsotopeSet(fragmentN, NUMBER_OF_FRAGMENTS * (INC_N), IncorporationType.EXPERIMENTAL);
						
						MassSpectrum naturalSpectrum = naturalSet.simulateSpectrum();
						MassSpectrum markedSpectrumCN = markedSetCN.simulateSpectrum();
						MassSpectrum markedSpectrumC = markedSetC.simulateSpectrum();
						MassSpectrum markedSpectrumN = markedSetN.simulateSpectrum();
						MassSpectrum mixedSpectrum = naturalSpectrum.merge(markedSpectrumCN);
						mixedSpectrum = mixedSpectrum.merge(markedSpectrumC);
						mixedSpectrum = mixedSpectrum.merge(markedSpectrumN);
						
						
						naturalSpectrum = IsotopePatternSimulator.prepareSpectrum(naturalSpectrum, PRECISION, PRECISION, MIN_FREQUENCY);
						markedSpectrumCN = IsotopePatternSimulator.prepareSpectrum(markedSpectrumCN, PRECISION, PRECISION, MIN_FREQUENCY);
						markedSpectrumC = IsotopePatternSimulator.prepareSpectrum(markedSpectrumC, PRECISION, PRECISION, MIN_FREQUENCY);
						markedSpectrumN = IsotopePatternSimulator.prepareSpectrum(markedSpectrumN, PRECISION, PRECISION, MIN_FREQUENCY);
						mixedSpectrum = IsotopePatternSimulator.prepareSpectrum(mixedSpectrum, PRECISION, PRECISION, MIN_FREQUENCY);
						
						MSShiftDatabase msShiftDatabase = new MSShiftDatabase();
						msShiftDatabase.setIncorporatedTracers("CN,C,N");
						msShiftDatabase.setIncorporationRate(INC);
						msShiftDatabase.setFragmentKey(fragmentCN.getFragmentKey());
						msShiftDatabase.setNaturalSpectrum(naturalSpectrum);
						msShiftDatabase.setMarkedSpectrum(markedSpectrumCN);
						msShiftDatabase.setMixedSpectrum(mixedSpectrum);
						msShiftDatabase.setFragmentFormula(fragmentCN.getFormula());
						msShiftDatabase.analyseAllShifts();
						
						LOGGER.debug(msShiftDatabase);
						LOGGER.debugHorizontalLine();
						IncorporationMap incorporationMap = new IncorporationMap(
								msShiftDatabase.getMixedSpectrum(), msShiftDatabase.getMixedMassShifts(), new IsotopeList(Isotope.C_13, Isotope.N_15));
						LOGGER.debugValue("UncorrectedIncorporation", incorporationMap.asTable());
						
						ElementFormula fragmentFormula = ElementFormula.fromString(msShiftDatabase.getFragmentFormula());
						ElementFormula elementFormula = new ElementFormula();
						elementFormula.put(Element.C, fragmentFormula.get(Element.C));
						elementFormula.put(Element.N, fragmentFormula.get(Element.N));
						IncorporationMap correctedMap = incorporationMap.correctForNaturalAbundance(elementFormula);
						
						LOGGER.debugValue("correctedMap", correctedMap.asTable());
						LOGGER.debugHorizontalLine();
						LOGGER.info("Simulated incorporations");
						LOGGER.infoValue("INC_C", INC_C);
						LOGGER.infoValue("INC_N", INC_N);
						LOGGER.infoValue("INC_CN", INC_CN);
						LOGGER.horizontalLine();
						LOGGER.debug("Check C incorporation...");
						IsotopeFormula formulaC = new IsotopeFormula();
						formulaC.put(Isotope.C_13, 1);
						formulaC.put(Isotope.N_15, 0);
						LOGGER.debugValue("calculatedIncC", correctedMap.get(formulaC));
						LOGGER.debugValue("expectedIncC", INC_C);
						LOGGER.debugHorizontalLine();
						LOGGER.debug("Check N incorporation...");
						IsotopeFormula formulaN = new IsotopeFormula();
						formulaN.put(Isotope.C_13, 0);
						formulaN.put(Isotope.N_15, 1);
						LOGGER.debugValue("calculatedIncN", correctedMap.get(formulaN));
						LOGGER.debugValue("expectedIncN", INC_N);
						LOGGER.debugHorizontalLine();
						LOGGER.debug("Check CN incorporation...");
						IsotopeFormula formulaCN = new IsotopeFormula();
						formulaCN.put(Isotope.C_13, 1);
						formulaCN.put(Isotope.N_15, 1);
						LOGGER.debugValue("calculatedIncCN", correctedMap.get(formulaCN));
						LOGGER.debugValue("expectedIncCN", INC_CN);
						LOGGER.debugHorizontalLine();
						Double actualCN = correctedMap.get(formulaCN) != null ? correctedMap.get(formulaCN) : 0;
						Double actualC = correctedMap.get(formulaC) != null ? correctedMap.get(formulaC) : 0;
						Double actualN = correctedMap.get(formulaN) != null ? correctedMap.get(formulaN) : 0;
						assertTrue(MathUtils.approximatelyEquals(actualC, INC_C, ALLOWED_INC_ERROR));
						assertTrue(MathUtils.approximatelyEquals(actualN, INC_N, ALLOWED_INC_ERROR));
						assertTrue(MathUtils.approximatelyEquals(actualCN, INC_CN, ALLOWED_INC_ERROR));
					}
				}
			}
		}
	}
	
	public void testIncorporationRate02() throws FragmentNotFoundException, FrequencyTypeMismatchException {
		LOGGER.enableDebug();
		IncorporationMap.LOG.enableDebug();
		double maxError = 0.0;
		for (int c = 0; c < 10; c++) {
			final double INC_C = 0.0 + c * 0.1;
			final double NUMBER_OF_FRAGMENTS = 100000.0;
			final Integer PRECISION = 4;
			final double MIN_FREQUENCY = 0.001;
			
			Fragment fragmentC = FragmentsDatabase.getFragment(FragmentKey.GLY_276);
			fragmentC.changeCapacity("C");
			
			IsotopeSet naturalSet = new IsotopeSet(fragmentC, NUMBER_OF_FRAGMENTS * (1 - INC_C), IncorporationType.NATURAL);
			IsotopeSet markedSetC = new IsotopeSet(fragmentC, NUMBER_OF_FRAGMENTS * (INC_C), IncorporationType.EXPERIMENTAL);
			
			MassSpectrum naturalSpectrum = naturalSet.simulateSpectrum();
			MassSpectrum markedSpectrumC = markedSetC.simulateSpectrum();
			MassSpectrum mixedSpectrum = naturalSpectrum.merge(markedSpectrumC);

			naturalSpectrum = IsotopePatternSimulator.prepareSpectrum(naturalSpectrum, PRECISION, PRECISION, MIN_FREQUENCY);
			markedSpectrumC = IsotopePatternSimulator.prepareSpectrum(markedSpectrumC, PRECISION, PRECISION, MIN_FREQUENCY);
			mixedSpectrum = IsotopePatternSimulator.prepareSpectrum(mixedSpectrum, PRECISION, PRECISION, MIN_FREQUENCY);
			
			MSShiftDatabase msShiftDatabase = new MSShiftDatabase();
			msShiftDatabase.setIncorporatedTracers("C");
			msShiftDatabase.setIncorporationRate(INC_C);
			msShiftDatabase.setFragmentKey(fragmentC.getFragmentKey());
			msShiftDatabase.setNaturalSpectrum(naturalSpectrum);
			msShiftDatabase.setMarkedSpectrum(markedSpectrumC);
			msShiftDatabase.setMixedSpectrum(mixedSpectrum);
			msShiftDatabase.setFragmentFormula(fragmentC.getFormula());
			msShiftDatabase.analyseAllShifts();
			
			LOGGER.debug(msShiftDatabase);
			LOGGER.debugHorizontalLine();
			IncorporationMap incorporationMap = new IncorporationMap(
					msShiftDatabase.getMixedSpectrum(), msShiftDatabase.getMixedMassShifts(), new IsotopeList(Isotope.C_13));
			LOGGER.debugValue("UncorrectedIncorporation", incorporationMap.asTable());
			
			ElementFormula fragmentFormula = ElementFormula.fromString(msShiftDatabase.getFragmentFormula());
			ElementFormula elementFormula = new ElementFormula();
			elementFormula.put(Element.C, fragmentFormula.get(Element.C));
			IncorporationMap correctedMap = incorporationMap.correctForNaturalAbundance(elementFormula);
			
			LOGGER.debugValue("correctedMap", correctedMap.asTable());
			LOGGER.debugHorizontalLine();
			LOGGER.info("Simulated incorporation");
			LOGGER.infoValue("INC_C", INC_C);
			LOGGER.horizontalLine();
			LOGGER.debug("Check C incorporation...");
			IsotopeFormula formulaC = new IsotopeFormula();
			formulaC.put(Isotope.C_13, 1);
			LOGGER.debugValue("calculatedIncC", correctedMap.get(formulaC));
			LOGGER.debugValue("expectedIncC", INC_C);
			LOGGER.debugHorizontalLine();
			Double actualC = correctedMap.get(formulaC) != null ? correctedMap.get(formulaC) : 0;
			if (Math.abs(actualC - INC_C) > maxError) {
				maxError = Math.abs(actualC - INC_C);
			}
			assertTrue(MathUtils.approximatelyEquals(actualC, INC_C, ALLOWED_INC_ERROR));
		}
		LOGGER.debugValue("maxError", maxError);
	}
	
	public void testIncorporationRate03() throws FragmentNotFoundException, FrequencyTypeMismatchException {
		LOGGER.enableDebug();
		IncorporationMap.LOG.enableDebug();
		Double maxError = 0.0;
		for (int n = 0; n < 10; n++) {
			final double INC_N = 0.0 + n * 0.1;
			final double NUMBER_OF_FRAGMENTS = 100000.0;
			final Integer PRECISION = 4;
			final double MIN_FREQUENCY = 0.001;
			Fragment fragmentN = FragmentsDatabase.getFragment(FragmentKey.GLY_276);
			fragmentN.changeCapacity("N");
			
			IsotopeSet naturalSet = new IsotopeSet(fragmentN, NUMBER_OF_FRAGMENTS * (1 - INC_N), IncorporationType.NATURAL);
			IsotopeSet markedSetN = new IsotopeSet(fragmentN, NUMBER_OF_FRAGMENTS * (INC_N), IncorporationType.EXPERIMENTAL);
			
			MassSpectrum naturalSpectrum = naturalSet.simulateSpectrum();
			MassSpectrum markedSpectrumN = markedSetN.simulateSpectrum();
			MassSpectrum mixedSpectrum = naturalSpectrum.merge(markedSpectrumN);

			naturalSpectrum = IsotopePatternSimulator.prepareSpectrum(naturalSpectrum, PRECISION, PRECISION, MIN_FREQUENCY);
			markedSpectrumN = IsotopePatternSimulator.prepareSpectrum(markedSpectrumN, PRECISION, PRECISION, MIN_FREQUENCY);
			mixedSpectrum = IsotopePatternSimulator.prepareSpectrum(mixedSpectrum, PRECISION, PRECISION, MIN_FREQUENCY);
			
			MSShiftDatabase msShiftDatabase = new MSShiftDatabase();
			msShiftDatabase.setIncorporatedTracers("N");
			msShiftDatabase.setIncorporationRate(INC_N);
			msShiftDatabase.setFragmentKey(fragmentN.getFragmentKey());
			msShiftDatabase.setNaturalSpectrum(naturalSpectrum);
			msShiftDatabase.setMarkedSpectrum(markedSpectrumN);
			msShiftDatabase.setMixedSpectrum(mixedSpectrum);
			msShiftDatabase.setFragmentFormula(fragmentN.getFormula());
			msShiftDatabase.analyseAllShifts();
			
			LOGGER.debug(msShiftDatabase);
			LOGGER.debugHorizontalLine();
			IncorporationMap incorporationMap = new IncorporationMap(
					msShiftDatabase.getMixedSpectrum(), msShiftDatabase.getMixedMassShifts(), new IsotopeList(Isotope.N_15));
			LOGGER.debugValue("UncorrectedIncorporation", incorporationMap.asTable());
			
			ElementFormula fragmentFormula = ElementFormula.fromString(msShiftDatabase.getFragmentFormula());
			ElementFormula elementFormula = new ElementFormula();
			elementFormula.put(Element.N, fragmentFormula.get(Element.N));
			IncorporationMap correctedMap = incorporationMap.correctForNaturalAbundance(elementFormula);
			
			LOGGER.debugValue("correctedMap", correctedMap.asTable());
			LOGGER.debugHorizontalLine();
			LOGGER.info("Simulated incorporation");
			LOGGER.infoValue("INC_N", INC_N);
			LOGGER.horizontalLine();
			LOGGER.debug("Check N incorporation...");
			IsotopeFormula formulaN = new IsotopeFormula();
			formulaN.put(Isotope.N_15, 1);
			LOGGER.debugValue("calculatedIncN", correctedMap.get(formulaN));
			LOGGER.debugValue("expectedIncN", INC_N);
			LOGGER.debugHorizontalLine();
			Double actualN = correctedMap.get(formulaN) != null ? correctedMap.get(formulaN) : 0;
			if (Math.abs(actualN - INC_N) > maxError) {
				maxError = Math.abs(actualN - INC_N);
			}
			assertTrue(MathUtils.approximatelyEquals(actualN, INC_N, ALLOWED_INC_ERROR));
		}
		LOGGER.debugValue("maxError", maxError);
	}
	
	public void testIncorporationRate04() throws FragmentNotFoundException, FrequencyTypeMismatchException {
		if (TEST_INCS) {
		LOGGER.enableDebug();
		IncorporationMap.LOG.enableDebug();
			for (int c2 = 0; c2 < 10; c2++) {
				for (int n = 0; n < 10; n++) {
					if (c2 + n >= 10) {
						continue;
					}
					final double INC_C2 = 0.0 + c2 * 0.1;
					final double INC_N = 0.0 + n * 0.1;
					final double NUMBER_OF_FRAGMENTS = 100000.0;
					final Integer PRECISION = 4;
					final double MIN_FREQUENCY = 0.001;
					
					final double INC = MathUtils.round(INC_C2 + INC_N, 2);
					
					Fragment fragmentC2N = FragmentsDatabase.getFragment(FragmentKey.GLY_276);
					fragmentC2N.changeCapacity("C2N");
					Fragment fragmentC2 = FragmentsDatabase.getFragment(FragmentKey.GLY_276);
					fragmentC2.changeCapacity("C2");
					Fragment fragmentN = FragmentsDatabase.getFragment(FragmentKey.GLY_276);
					fragmentN.changeCapacity("N");
					
					IsotopeSet naturalSet = new IsotopeSet(fragmentC2N, NUMBER_OF_FRAGMENTS * (1 - INC), IncorporationType.NATURAL);
					IsotopeSet markedSetC2 = new IsotopeSet(fragmentC2, NUMBER_OF_FRAGMENTS * (INC_C2), IncorporationType.EXPERIMENTAL);
					IsotopeSet markedSetN = new IsotopeSet(fragmentN, NUMBER_OF_FRAGMENTS * (INC_N), IncorporationType.EXPERIMENTAL);
					
					MassSpectrum naturalSpectrum = naturalSet.simulateSpectrum();
					MassSpectrum markedSpectrumC2 = markedSetC2.simulateSpectrum();
					MassSpectrum markedSpectrumN = markedSetN.simulateSpectrum();
					MassSpectrum mixedSpectrum = naturalSpectrum.merge(markedSpectrumC2);
					mixedSpectrum = mixedSpectrum.merge(markedSpectrumN);
					
					
					naturalSpectrum = IsotopePatternSimulator.prepareSpectrum(naturalSpectrum, PRECISION, PRECISION, MIN_FREQUENCY);
					markedSpectrumC2 = IsotopePatternSimulator.prepareSpectrum(markedSpectrumC2, PRECISION, PRECISION, MIN_FREQUENCY);
					markedSpectrumN = IsotopePatternSimulator.prepareSpectrum(markedSpectrumN, PRECISION, PRECISION, MIN_FREQUENCY);
					mixedSpectrum = IsotopePatternSimulator.prepareSpectrum(mixedSpectrum, PRECISION, PRECISION, MIN_FREQUENCY);
					
					MSShiftDatabase msShiftDatabase = new MSShiftDatabase();
					msShiftDatabase.setIncorporatedTracers("C2,N");
					msShiftDatabase.setIncorporationRate(INC);
					msShiftDatabase.setFragmentKey(fragmentC2N.getFragmentKey());
					msShiftDatabase.setNaturalSpectrum(naturalSpectrum);
					msShiftDatabase.setMarkedSpectrum(markedSpectrumC2);
					msShiftDatabase.setMixedSpectrum(mixedSpectrum);
					msShiftDatabase.setFragmentFormula(fragmentC2N.getFormula());
					msShiftDatabase.analyseAllShifts();
					
					LOGGER.debug(msShiftDatabase);
					LOGGER.debugHorizontalLine();
					IncorporationMap incorporationMap = new IncorporationMap(
							msShiftDatabase.getMixedSpectrum(), msShiftDatabase.getMixedMassShifts(), new IsotopeList(Isotope.C_13, Isotope.N_15));
					LOGGER.debugValue("UncorrectedIncorporation", incorporationMap.asTable());
					
					ElementFormula fragmentFormula = ElementFormula.fromString(msShiftDatabase.getFragmentFormula());
					ElementFormula elementFormula = new ElementFormula();
					elementFormula.put(Element.C, fragmentFormula.get(Element.C));
					elementFormula.put(Element.N, fragmentFormula.get(Element.N));
					IncorporationMap correctedMap = incorporationMap.correctForNaturalAbundance(elementFormula);
					
					LOGGER.debugValue("correctedMap", correctedMap.asTable());
					LOGGER.debugHorizontalLine();
					LOGGER.info("Simulated incorporations");
					LOGGER.infoValue("INC_C2", INC_C2);
					LOGGER.infoValue("INC_N", INC_N);
					LOGGER.horizontalLine();
					LOGGER.debug("Check C incorporation...");
					IsotopeFormula formulaC2 = new IsotopeFormula();
					formulaC2.put(Isotope.C_13, 2);
					formulaC2.put(Isotope.N_15, 0);
					LOGGER.debugValue("calculatedIncC2", correctedMap.get(formulaC2));
					LOGGER.debugValue("expectedIncC2", INC_C2);
					LOGGER.debugHorizontalLine();
					LOGGER.debug("Check N incorporation...");
					IsotopeFormula formulaN = new IsotopeFormula();
					formulaN.put(Isotope.C_13, 0);
					formulaN.put(Isotope.N_15, 1);
					LOGGER.debugValue("calculatedIncN", correctedMap.get(formulaN));
					LOGGER.debugValue("expectedIncN", INC_N);
					LOGGER.debugHorizontalLine();
					LOGGER.debug("Check CN incorporation...");
					IsotopeFormula formulaCN = new IsotopeFormula();
					formulaCN.put(Isotope.C_13, 2);
					formulaCN.put(Isotope.N_15, 1);
					LOGGER.debugValue("calculatedIncCN", correctedMap.get(formulaCN));
					LOGGER.debugValue("expectedIncCN", 0.0);
					LOGGER.debugHorizontalLine();
					Double actualCN = correctedMap.get(formulaCN) != null ? correctedMap.get(formulaCN) : 0;
					Double actualC = correctedMap.get(formulaC2) != null ? correctedMap.get(formulaC2) : 0;
					Double actualN = correctedMap.get(formulaN) != null ? correctedMap.get(formulaN) : 0;
					assertTrue(MathUtils.approximatelyEquals(actualC, INC_C2, ALLOWED_INC_ERROR));
					assertTrue(MathUtils.approximatelyEquals(actualN, INC_N, ALLOWED_INC_ERROR));
					assertTrue(MathUtils.approximatelyEquals(actualCN, 0.0, ALLOWED_INC_ERROR));
				}
			}
		}
	}

}
