package de.kempalab.msdps.correction;

import de.kempalab.msdps.ElementFormula;
import de.kempalab.msdps.Fragment;
import de.kempalab.msdps.FragmentsDatabase;
import de.kempalab.msdps.IncorporationMap;
import de.kempalab.msdps.IsotopeFormula;
import de.kempalab.msdps.IsotopeList;
import de.kempalab.msdps.IsotopeListList;
import de.kempalab.msdps.IsotopeSet;
import de.kempalab.msdps.MSShiftDatabase;
import de.kempalab.msdps.MassShift;
import de.kempalab.msdps.MassShiftDataSet;
import de.kempalab.msdps.MassShiftList;
import de.kempalab.msdps.MassSpectrum;
import de.kempalab.msdps.constants.Element;
import de.kempalab.msdps.constants.FragmentKey;
import de.kempalab.msdps.constants.FrequencyType;
import de.kempalab.msdps.constants.IncorporationType;
import de.kempalab.msdps.constants.Isotope;
import de.kempalab.msdps.exception.FragmentNotFoundException;
import de.kempalab.msdps.exception.FrequencyTypeMismatchException;
import de.kempalab.msdps.log.MyLogger;
import de.kempalab.msdps.simulation.IsotopePatternSimulator;
import de.kempalab.msdps.util.MathUtils;
import junit.framework.TestCase;

public class CorrectionUtilsTest extends TestCase {
	
	public static final MyLogger LOGGER = MyLogger.getLogger(CorrectionUtilsTest.class);
	private static final Double ALLOWED_INC_ERROR = 0.007;
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
						
						MassSpectrum naturalSpectrum = naturalSet.simulateSpectrum(0);
						MassSpectrum markedSpectrumCN = markedSetCN.simulateSpectrum(0);
						MassSpectrum markedSpectrumC = markedSetC.simulateSpectrum(0);
						MassSpectrum markedSpectrumN = markedSetN.simulateSpectrum(0);
						MassSpectrum mixedSpectrum = naturalSpectrum.merge(markedSpectrumCN);
						mixedSpectrum = mixedSpectrum.merge(markedSpectrumC);
						mixedSpectrum = mixedSpectrum.merge(markedSpectrumN);
						
						
						naturalSpectrum = IsotopePatternSimulator.prepareSpectrum(naturalSpectrum, PRECISION, PRECISION,
								MIN_FREQUENCY, FrequencyType.MID);
						markedSpectrumCN = IsotopePatternSimulator.prepareSpectrum(markedSpectrumCN, PRECISION,
								PRECISION, MIN_FREQUENCY, FrequencyType.MID);
						markedSpectrumC = IsotopePatternSimulator.prepareSpectrum(markedSpectrumC, PRECISION, PRECISION,
								MIN_FREQUENCY, FrequencyType.MID);
						markedSpectrumN = IsotopePatternSimulator.prepareSpectrum(markedSpectrumN, PRECISION, PRECISION,
								MIN_FREQUENCY, FrequencyType.MID);
						mixedSpectrum = IsotopePatternSimulator.prepareSpectrum(mixedSpectrum, PRECISION, PRECISION,
								MIN_FREQUENCY, FrequencyType.MID);
						
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
			
			MassSpectrum naturalSpectrum = naturalSet.simulateSpectrum(0);
			MassSpectrum markedSpectrumC = markedSetC.simulateSpectrum(0);
			MassSpectrum mixedSpectrum = naturalSpectrum.merge(markedSpectrumC);

			naturalSpectrum = IsotopePatternSimulator.prepareSpectrum(naturalSpectrum, PRECISION, PRECISION,
					MIN_FREQUENCY, FrequencyType.MID);
			markedSpectrumC = IsotopePatternSimulator.prepareSpectrum(markedSpectrumC, PRECISION, PRECISION,
					MIN_FREQUENCY, FrequencyType.MID);
			mixedSpectrum = IsotopePatternSimulator.prepareSpectrum(mixedSpectrum, PRECISION, PRECISION, MIN_FREQUENCY,
					FrequencyType.MID);
			
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
			
			MassSpectrum naturalSpectrum = naturalSet.simulateSpectrum(0);
			MassSpectrum markedSpectrumN = markedSetN.simulateSpectrum(0);
			MassSpectrum mixedSpectrum = naturalSpectrum.merge(markedSpectrumN);

			naturalSpectrum = IsotopePatternSimulator.prepareSpectrum(naturalSpectrum, PRECISION, PRECISION,
					MIN_FREQUENCY, FrequencyType.MID);
			markedSpectrumN = IsotopePatternSimulator.prepareSpectrum(markedSpectrumN, PRECISION, PRECISION,
					MIN_FREQUENCY, FrequencyType.MID);
			mixedSpectrum = IsotopePatternSimulator.prepareSpectrum(mixedSpectrum, PRECISION, PRECISION, MIN_FREQUENCY,
					FrequencyType.MID);
			
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
					
					MassSpectrum naturalSpectrum = naturalSet.simulateSpectrum(0);
					MassSpectrum markedSpectrumC2 = markedSetC2.simulateSpectrum(0);
					MassSpectrum markedSpectrumN = markedSetN.simulateSpectrum(0);
					MassSpectrum mixedSpectrum = naturalSpectrum.merge(markedSpectrumC2);
					mixedSpectrum = mixedSpectrum.merge(markedSpectrumN);
					
					
					naturalSpectrum = IsotopePatternSimulator.prepareSpectrum(naturalSpectrum, PRECISION, PRECISION,
							MIN_FREQUENCY, FrequencyType.MID);
					markedSpectrumC2 = IsotopePatternSimulator.prepareSpectrum(markedSpectrumC2, PRECISION, PRECISION,
							MIN_FREQUENCY, FrequencyType.MID);
					markedSpectrumN = IsotopePatternSimulator.prepareSpectrum(markedSpectrumN, PRECISION, PRECISION,
							MIN_FREQUENCY, FrequencyType.MID);
					mixedSpectrum = IsotopePatternSimulator.prepareSpectrum(mixedSpectrum, PRECISION, PRECISION,
							MIN_FREQUENCY, FrequencyType.MID);
					
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
	
	public void correctionGlnUnlabeledTest() throws FragmentNotFoundException {
		MassSpectrum measured = new MassSpectrum(FrequencyType.ABSOLUTE);
		measured.put(156.083871, 2177824768.0);
		measured.put(157.081106, 3256251.75);
		measured.put(157.083466, 105339544.0);
		measured.put(157.087178, 164780256.0);
		measured.put(158.063075, 5476358.0);
		measured.put(158.080719, 75050424.0);
		measured.put(158.086352, 6758987.5);
		measured.put(158.090634, 3685533.25);
		measured.put(159.083991, 4425675.0);
	
		MassShiftDataSet shifts = new MassShiftDataSet();
		shifts.put(new MassShiftList(new MassShift(0, 0, null)), new IsotopeListList(new IsotopeList(Isotope.NONE)));
		shifts.put(new MassShiftList(new MassShift(0, 1, null)), new IsotopeListList(new IsotopeList(Isotope.N_15)));
		shifts.put(new MassShiftList(new MassShift(0, 2, null)), new IsotopeListList(new IsotopeList(Isotope.Si_29)));
		shifts.put(new MassShiftList(new MassShift(0, 3, null)), new IsotopeListList(new IsotopeList(Isotope.C_13)));
		shifts.put(new MassShiftList(new MassShift(0, 2, null), new MassShift(2, 4, null)), new IsotopeListList(new IsotopeList(Isotope.Si_29), new IsotopeList(Isotope.N_15)));
		shifts.put(new MassShiftList(new MassShift(0, 5, null)), new IsotopeListList(new IsotopeList(Isotope.Si_30)));
		shifts.put(new MassShiftList(new MassShift(0, 3, null), new MassShift(3, 6, null)), new IsotopeListList(new IsotopeList(Isotope.C_13), new IsotopeList(Isotope.Si_29)));
		shifts.put(new MassShiftList(new MassShift(0, 3, null), new MassShift(3, 7, null)), new IsotopeListList(new IsotopeList(Isotope.C_13), new IsotopeList(Isotope.C_13)));
		shifts.put(new MassShiftList(new MassShift(0, 5, null), new MassShift(5, 8, null)), new IsotopeListList(new IsotopeList(Isotope.Si_30), new IsotopeList(Isotope.C_13)));
		
		IncorporationMap incorporationMap = new IncorporationMap(measured, shifts, new IsotopeList(Isotope.C_13, Isotope.N_15));
		LOGGER.infoValue("incorporationMap", incorporationMap.asTable());
		LOGGER.infoValue("incorporationMap.normalize();", incorporationMap.normalize(4).asTable());
		ElementFormula fragmentFormula = ElementFormula.fromString(FragmentsDatabase.getFragment(FragmentKey.GLN_156).getFormula());
		ElementFormula elementFormula = new ElementFormula();
		elementFormula.put(Element.C, fragmentFormula.get(Element.C));
		elementFormula.put(Element.N, fragmentFormula.get(Element.N));
		IncorporationMap correctedMap = incorporationMap.correctForNaturalAbundance(elementFormula);
		LOGGER.infoValue("correctedMap", correctedMap.asTable());
		LOGGER.infoValue("correctedMap.normalize()", correctedMap.normalize(4).asTable());
		
		IncorporationMap normalizedCorrectedMap = correctedMap.normalize(4);
		IsotopeFormula cn00 = new IsotopeFormula();
		cn00.put(Isotope.C_13, 0);
		cn00.put(Isotope.N_15, 0);
		IsotopeFormula cn01 = new IsotopeFormula();
		cn01.put(Isotope.C_13, 0);
		cn01.put(Isotope.N_15, 1);
		IsotopeFormula cn10 = new IsotopeFormula();
		cn10.put(Isotope.C_13, 1);
		cn10.put(Isotope.N_15, 0);
		IsotopeFormula cn20 = new IsotopeFormula();
		cn20.put(Isotope.C_13, 2);
		cn20.put(Isotope.N_15, 0);
		assertEquals(1.0, normalizedCorrectedMap.get(cn00));
		assertEquals(0.0, normalizedCorrectedMap.get(cn10));
		assertEquals(0.0, normalizedCorrectedMap.get(cn01));
		assertEquals(0.0, normalizedCorrectedMap.get(cn20));
	}
	
	public void correctionGlnTotallyCNLabeledTest() throws FragmentNotFoundException {
		MassSpectrum measured = new MassSpectrum(FrequencyType.ABSOLUTE);
		measured.put(161.094388, 3383957504.000000);
		measured.put(162.093845, 167757680.000000);
		measured.put(162.097430, 96693112.000000);
		measured.put(162.100503, 2915952.500000);
		measured.put(163.091187, 109522104.000000);
		measured.put(163.098605, 6520942.500000);
		measured.put(163.104047, 994677.062500);

		MassShiftDataSet shifts = new MassShiftDataSet();
		shifts.put(new MassShiftList(new MassShift(0, 0, null)),new IsotopeListList(new IsotopeList(
				Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.N_15)));
		shifts.put(new MassShiftList(new MassShift(0, 1, null)), new IsotopeListList(new IsotopeList(
				Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.N_15, Isotope.Si_29)));
		shifts.put(new MassShiftList(new MassShift(0, 2, null)), new IsotopeListList(new IsotopeList(
				Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.N_15, Isotope.C_13)));
		shifts.put(new MassShiftList(new MassShift(0, 3, null)), new IsotopeListList(new IsotopeList(
				Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.N_15, Isotope.H_2)));
		shifts.put(new MassShiftList(new MassShift(0, 4, null)), new IsotopeListList(new IsotopeList(
				Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.N_15, Isotope.Si_30)));
		shifts.put(new MassShiftList(new MassShift(0, 2, null), new MassShift(2, 5, null)), new IsotopeListList(
				new IsotopeList(
						Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.N_15, Isotope.C_13),
				new IsotopeList(Isotope.Si_29)));
		shifts.put(new MassShiftList(new MassShift(0, 6, null)), new IsotopeListList(new IsotopeList(
				Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.N_15, Isotope.O_18)));
		
		IncorporationMap incorporationMap = new IncorporationMap(measured, shifts, new IsotopeList(Isotope.C_13, Isotope.N_15));
		LOGGER.infoValue("incorporationMap", incorporationMap.asTable());
		LOGGER.infoValue("incorporationMap.normalize();", incorporationMap.normalize(4).asTable());
		ElementFormula fragmentFormula = ElementFormula.fromString(FragmentsDatabase.getFragment(FragmentKey.GLN_156).getFormula());
		ElementFormula elementFormula = new ElementFormula();
		elementFormula.put(Element.C, fragmentFormula.get(Element.C));
		elementFormula.put(Element.N, fragmentFormula.get(Element.N));
		IncorporationMap correctedMap = incorporationMap.correctForNaturalAbundance(elementFormula);
		LOGGER.infoValue("correctedMap", correctedMap.asTable());
		LOGGER.infoValue("correctedMap.normalize()", correctedMap.normalize(4).asTable());
		
		IncorporationMap normalizedCorrectedMap = correctedMap.normalize(4);
		IsotopeFormula cn41 = new IsotopeFormula();
		cn41.put(Isotope.C_13, 4);
		cn41.put(Isotope.N_15, 1);
		IsotopeFormula cn51 = new IsotopeFormula();
		cn51.put(Isotope.C_13, 5);
		cn51.put(Isotope.N_15, 1);
		assertEquals(1.0, normalizedCorrectedMap.get(cn41));
		assertEquals(0.0, normalizedCorrectedMap.get(cn51));

	}
	
	public void correctionGlnTotallyCLabeledTest() throws FragmentNotFoundException {
		MassSpectrum measured = new MassSpectrum(FrequencyType.ABSOLUTE);
		measured.put(160.097400, 1584645632.000000);
		measured.put(161.094435, 3858969.500000);
		measured.put(161.096895, 75836104.000000);
		measured.put(161.100447, 44920384.000000);
		measured.put(161.103490, 1408230.125000);
		measured.put(162.094165, 51429216.000000);
		measured.put(162.101593, 2085006.750000);
		measured.put(163.097390, 1833341.625000);

		MassShiftDataSet shifts = new MassShiftDataSet();
		shifts.put(new MassShiftList(new MassShift(0, 0, null)),new IsotopeListList(new IsotopeList(
				Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.C_13)));
		shifts.put(new MassShiftList(new MassShift(0, 1, null)), new IsotopeListList(new IsotopeList(
				Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.N_15)));
		shifts.put(new MassShiftList(new MassShift(0, 2, null)), new IsotopeListList(new IsotopeList(
				Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.Si_29)));
		shifts.put(new MassShiftList(new MassShift(0, 3, null)), new IsotopeListList(new IsotopeList(
				Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.C_13)));
		shifts.put(new MassShiftList(new MassShift(0, 4, null)), new IsotopeListList(new IsotopeList(
				Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.H_2)));
		shifts.put(new MassShiftList(new MassShift(0, 5, null)), new IsotopeListList(new IsotopeList(
						Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.Si_30)));
		shifts.put(new MassShiftList(new MassShift(0, 6, null)), new IsotopeListList(new IsotopeList(
				Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.O_18)));
		shifts.put(new MassShiftList(new MassShift(0, 5, null), new MassShift(5, 7, null)), new IsotopeListList(
				new IsotopeList(
						Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.Si_30),
				new IsotopeList(Isotope.C_13)));
		
		IncorporationMap incorporationMap = new IncorporationMap(measured, shifts, new IsotopeList(Isotope.C_13, Isotope.N_15));
		LOGGER.infoValue("incorporationMap", incorporationMap.asTable());
		LOGGER.infoValue("incorporationMap.normalize();", incorporationMap.normalize(4).asTable());
		ElementFormula fragmentFormula = ElementFormula.fromString(FragmentsDatabase.getFragment(FragmentKey.GLN_156).getFormula());
		ElementFormula elementFormula = new ElementFormula();
		elementFormula.put(Element.C, fragmentFormula.get(Element.C));
		elementFormula.put(Element.N, fragmentFormula.get(Element.N));
		IncorporationMap correctedMap = incorporationMap.correctForNaturalAbundance(elementFormula);
		LOGGER.infoValue("correctedMap", correctedMap.asTable());
		LOGGER.infoValue("correctedMap.normalize()", correctedMap.normalize(4).asTable());
		
		IncorporationMap normalizedCorrectedMap = correctedMap.normalize(4);
		IsotopeFormula cn40 = new IsotopeFormula();
		cn40.put(Isotope.C_13, 4);
		cn40.put(Isotope.N_15, 0);
		IsotopeFormula cn41 = new IsotopeFormula();
		cn41.put(Isotope.C_13, 4);
		cn41.put(Isotope.N_15, 1);
		IsotopeFormula cn50 = new IsotopeFormula();
		cn50.put(Isotope.C_13, 5);
		cn50.put(Isotope.N_15, 0);
		assertEquals(1.0, normalizedCorrectedMap.get(cn40));
		assertEquals(0.0, normalizedCorrectedMap.get(cn41));
		assertEquals(0.0, normalizedCorrectedMap.get(cn50));

	}
	
	public void correctionGlnTotallyNLabeledTest() throws FragmentNotFoundException {
		MassSpectrum measured = new MassSpectrum(FrequencyType.ABSOLUTE);
		measured.put(157.081106, 4505609216.000000);
		measured.put(158.080438, 203910720.000000);
		measured.put(158.084197, 329013920.000000);
		measured.put(158.097247, 3530218.250000);
		measured.put(159.077794, 143005824.000000);
		measured.put(159.083474, 14104742.000000);
		measured.put(159.087566, 10400924.000000);
		measured.put(159.090714, 1040433.375000);
		measured.put(160.081109, 11535833.000000);

		MassShiftDataSet shifts = new MassShiftDataSet();
		shifts.put(new MassShiftList(new MassShift(0, 0, null)),new IsotopeListList(new IsotopeList(
				Isotope.N_15)));
		shifts.put(new MassShiftList(new MassShift(0, 1, null)), new IsotopeListList(new IsotopeList(
				Isotope.N_15, Isotope.Si_29)));
		shifts.put(new MassShiftList(new MassShift(0, 2, null)), new IsotopeListList(new IsotopeList(
				Isotope.N_15, Isotope.C_13)));
		shifts.put(new MassShiftList(new MassShift(0, 3, null)), new IsotopeListList(new IsotopeList(
				Isotope.N_15, Isotope.H_2)));
		shifts.put(new MassShiftList(new MassShift(0, 4, null)), new IsotopeListList(new IsotopeList(
				Isotope.N_15, Isotope.Si_30)));
		shifts.put(new MassShiftList(new MassShift(0, 2, null), new MassShift(2, 5, null)), new IsotopeListList(new IsotopeList(
				Isotope.N_15, Isotope.C_13), new IsotopeList(Isotope.Si_29)));
		shifts.put(new MassShiftList(new MassShift(0, 6, null)), new IsotopeListList(new IsotopeList(
				Isotope.N_15, Isotope.O_18)));
		shifts.put(new MassShiftList(new MassShift(0, 2, null), new MassShift(2, 7, null)), new IsotopeListList(
				new IsotopeList(
						Isotope.N_15, Isotope.C_13),
				new IsotopeList(Isotope.C_13)));
		shifts.put(new MassShiftList(new MassShift(0, 4, null), new MassShift(4, 8, null)), new IsotopeListList(
				new IsotopeList(
						Isotope.N_15, Isotope.Si_30),
				new IsotopeList(Isotope.C_13)));
		
		IncorporationMap incorporationMap = new IncorporationMap(measured, shifts, new IsotopeList(Isotope.C_13, Isotope.N_15));
		LOGGER.infoValue("incorporationMap", incorporationMap.asTable());
		LOGGER.infoValue("incorporationMap.normalize();", incorporationMap.normalize(4).asTable());
		ElementFormula fragmentFormula = ElementFormula.fromString(FragmentsDatabase.getFragment(FragmentKey.GLN_156).getFormula());
		ElementFormula elementFormula = new ElementFormula();
		elementFormula.put(Element.C, fragmentFormula.get(Element.C));
		elementFormula.put(Element.N, fragmentFormula.get(Element.N));
		IncorporationMap correctedMap = incorporationMap.correctForNaturalAbundance(elementFormula);
		LOGGER.infoValue("correctedMap", correctedMap.asTable());
		LOGGER.infoValue("correctedMap.normalize()", correctedMap.normalize(4).asTable());
		
		IncorporationMap normalizedCorrectedMap = correctedMap.normalize(4);
		IsotopeFormula cn01 = new IsotopeFormula();
		cn01.put(Isotope.C_13, 0);
		cn01.put(Isotope.N_15, 1);
		IsotopeFormula cn11 = new IsotopeFormula();
		cn11.put(Isotope.C_13, 1);
		cn11.put(Isotope.N_15, 1);
		IsotopeFormula cn21 = new IsotopeFormula();
		cn21.put(Isotope.C_13, 2);
		cn21.put(Isotope.N_15, 1);
		assertEquals(1.0, normalizedCorrectedMap.get(cn01));
		assertEquals(0.0, normalizedCorrectedMap.get(cn11));
		assertEquals(0.0, normalizedCorrectedMap.get(cn21));

	}



	public void test12CGLN() throws FragmentNotFoundException {
		LOGGER.info("12CGln");
		IsotopeFormula cn00 = new IsotopeFormula();
		cn00.put(Isotope.C_13, 0);
		cn00.put(Isotope.N_15, 0);
		IsotopeFormula cn01 = new IsotopeFormula();
		cn01.put(Isotope.C_13, 0);
		cn01.put(Isotope.N_15, 1);
		IsotopeFormula cn10 = new IsotopeFormula();
		cn10.put(Isotope.C_13, 1);
		cn10.put(Isotope.N_15, 0);
		IsotopeFormula cn20 = new IsotopeFormula();
		cn20.put(Isotope.C_13, 2);
		cn20.put(Isotope.N_15, 0);
		IsotopeFormula[] isotopeFormulas = { cn00, cn01, cn10, cn20 };
		Double[] intensities = { 2358214736.0, 8732609.75, 175964918.5, 3685533.25 };
		IncorporationMap incorporationMap = new IncorporationMap(isotopeFormulas, intensities);
		LOGGER.infoValue("uncorrectedMap", incorporationMap.asTable());
		LOGGER.infoValue("uncorrectedMap normalized", incorporationMap.normalize(4).asTable());
		ElementFormula fragmentFormula = ElementFormula
				.fromString(FragmentsDatabase.getFragment(FragmentKey.GLN_156).getFormula());
		ElementFormula elementFormula = new ElementFormula();
		elementFormula.put(Element.C, fragmentFormula.get(Element.C));
		elementFormula.put(Element.N, fragmentFormula.get(Element.N));
		IncorporationMap correctedMap = incorporationMap.correctForNaturalAbundance(elementFormula);
		LOGGER.infoValue("correctedMap", correctedMap.asTable());
		LOGGER.infoValue("correctedMap normalized", correctedMap.normalize(4).asTable());
		IncorporationMap normalizedCorrectedMap = correctedMap.normalize(4);
		assertEquals(1.0, normalizedCorrectedMap.get(0, 0));
		assertEquals(0.0, normalizedCorrectedMap.get(0, 1));
		assertEquals(0.0, normalizedCorrectedMap.get(1, 0));
		assertEquals(0.0, normalizedCorrectedMap.get(2, 0));
	}

	public void test13C15NGLN() throws FragmentNotFoundException {
		LOGGER.info("13C15NGln");
		IsotopeFormula cn41 = new IsotopeFormula();
		cn41.put(Isotope.C_13, 4);
		cn41.put(Isotope.N_15, 1);
		IsotopeFormula cn51 = new IsotopeFormula();
		cn51.put(Isotope.C_13, 5);
		cn51.put(Isotope.N_15, 1);
		IsotopeFormula[] isotopeFormulas = { cn41, cn51 };
		Double[] intensities = { 3664153240.500000, 103214054.5 };
		IncorporationMap incorporationMap = new IncorporationMap(isotopeFormulas, intensities);
		LOGGER.infoValue("uncorrectedMap", incorporationMap.asTable());
		LOGGER.infoValue("uncorrectedMap normalized", incorporationMap.normalize(4).asTable());
		ElementFormula fragmentFormula = ElementFormula
				.fromString(FragmentsDatabase.getFragment(FragmentKey.GLN_156).getFormula());
		ElementFormula elementFormula = new ElementFormula();
		elementFormula.put(Element.C, fragmentFormula.get(Element.C));
		elementFormula.put(Element.N, fragmentFormula.get(Element.N));
		IncorporationMap correctedMap = incorporationMap.correctForNaturalAbundance(elementFormula);
		LOGGER.infoValue("correctedMap", correctedMap.asTable());
		LOGGER.infoValue("correctedMap normalized", correctedMap.normalize(4).asTable());
		IncorporationMap normalizedCorrectedMap = correctedMap.normalize(4);
		assertEquals(1.0, normalizedCorrectedMap.get(4, 1));
		assertEquals(0.0, normalizedCorrectedMap.get(5, 1));
	}

	public void test13CGLN() throws FragmentNotFoundException {
		LOGGER.info("13CGln");
		IsotopeFormula cn40 = new IsotopeFormula();
		cn40.put(Isotope.C_13, 4);
		cn40.put(Isotope.N_15, 0);
		IsotopeFormula cn41 = new IsotopeFormula();
		cn41.put(Isotope.C_13, 4);
		cn41.put(Isotope.N_15, 1);
		IsotopeFormula cn50 = new IsotopeFormula();
		cn50.put(Isotope.C_13, 5);
		cn50.put(Isotope.N_15, 0);
		IsotopeFormula[] isotopeFormulas = { cn40, cn41, cn50 };
		Double[] intensities = { 1717237531.0, 3858969.5, 44920384.0 };
		IncorporationMap incorporationMap = new IncorporationMap(isotopeFormulas, intensities);
		LOGGER.infoValue("uncorrectedMap", incorporationMap.asTable());
		LOGGER.infoValue("uncorrectedMap normalized", incorporationMap.normalize(4).asTable());
		ElementFormula fragmentFormula = ElementFormula
				.fromString(FragmentsDatabase.getFragment(FragmentKey.GLN_156).getFormula());
		ElementFormula elementFormula = new ElementFormula();
		elementFormula.put(Element.C, fragmentFormula.get(Element.C));
		elementFormula.put(Element.N, fragmentFormula.get(Element.N));
		IncorporationMap correctedMap = incorporationMap.correctForNaturalAbundance(elementFormula);
		LOGGER.infoValue("correctedMap", correctedMap.asTable());
		LOGGER.infoValue("correctedMap normalized", correctedMap.normalize(4).asTable());
		IncorporationMap normalizedCorrectedMap = correctedMap.normalize(4);
		assertEquals(1.0, normalizedCorrectedMap.get(4, 0));
		assertEquals(0.0, normalizedCorrectedMap.get(4, 1));
		assertEquals(0.0, normalizedCorrectedMap.get(5, 0));
	}

}
