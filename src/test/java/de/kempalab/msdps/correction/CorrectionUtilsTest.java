package de.kempalab.msdps.correction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.kempalab.msdps.ElementFormula;
import de.kempalab.msdps.Fragment;
import de.kempalab.msdps.FragmentList;
import de.kempalab.msdps.IncorporationMap;
import de.kempalab.msdps.IsotopeFormula;
import de.kempalab.msdps.IsotopeList;
import de.kempalab.msdps.IsotopeListList;
import de.kempalab.msdps.MSShiftDatabase;
import de.kempalab.msdps.MassShift;
import de.kempalab.msdps.MassShiftDataSet;
import de.kempalab.msdps.MassShiftList;
import de.kempalab.msdps.MassSpectrum;
import de.kempalab.msdps.constants.Element;
import de.kempalab.msdps.constants.FragmentKey;
import de.kempalab.msdps.constants.IntensityType;
import de.kempalab.msdps.constants.Isotope;
import de.kempalab.msdps.constants.SpectrumType;
import de.kempalab.msdps.data.IncorporationRate;
import de.kempalab.msdps.exception.TypeMismatchException;
import de.kempalab.msdps.simulation.IsotopePatternSimulator;
import de.kempalab.msdps.simulation.IsotopePatternSimulatorRequest;
import de.kempalab.msdps.simulation.IsotopePatternSimulatorResponse;
import de.kempalab.msdps.util.MathUtils;
import junit.framework.TestCase;

/**
 * 
 * @author Susanne Fürst, susannefuerst@freenet.de, susanne.fuerst@mdc-berlin.de
 *
 */
public class CorrectionUtilsTest extends TestCase {

	private static final Logger LOGGER = LoggerFactory.getLogger(CorrectionUtilsTest.class);
	private static final Double ALLOWED_INC_ERROR = 0.006;
	// some of these tests take a lot of time so do not run them automatically
	private static final boolean TEST_INCS = false;

	public void testIncorporationRate01() throws TypeMismatchException {
		if (TEST_INCS) {
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
						/*
						 * simulate the spectrum
						 */
						IsotopePatternSimulatorRequest request = new IsotopePatternSimulatorRequest();
						request.setAnalyzeMassShifts(true);
						request.setCharge(1);
						request.setFragments(new FragmentList(new Fragment("C10H26NO2Si3", "CN")));
						request.setMinimalIntensity(MIN_FREQUENCY);
						request.setRoundedIntensityPrecision(PRECISION);
						request.setRoundedMassPrecision(PRECISION);
						request.setTargetIntensityType(IntensityType.MID);
						request.setTracer1(Element.C);
						request.setTracer1Inc(new IncorporationRate(INC_C));
						request.setTracer2(Element.N);
						request.setTracer2Inc(new IncorporationRate(INC_N));
						request.setTracerAllInc(new IncorporationRate(INC_CN));
						IsotopePatternSimulatorResponse response = IsotopePatternSimulator
								.simulateIndependentTracerIncorporation(request);
						MSShiftDatabase msShiftDatabase = (MSShiftDatabase) response.getMsDatabaseList().get(0);

						LOGGER.info(msShiftDatabase.toString());
						IncorporationMap incorporationMap = new IncorporationMap(msShiftDatabase.getMixedSpectrum(),
								msShiftDatabase.getMixedMassShifts(), new IsotopeList(Isotope.C_13, Isotope.N_15));
						LOGGER.info("UncorrectedIncorporation " + incorporationMap.asTable());

						ElementFormula fragmentFormula = ElementFormula
								.fromString(msShiftDatabase.getFragmentFormula());
						ElementFormula elementFormula = new ElementFormula();
						elementFormula.put(Element.C, fragmentFormula.get(Element.C));
						elementFormula.put(Element.N, fragmentFormula.get(Element.N));
						/*
						 * recalculate the incorporation rates
						 */
						IncorporationMap correctedMap = incorporationMap.correctForNaturalAbundance(elementFormula);

						LOGGER.info("correctedMap " + correctedMap.asTable());
						LOGGER.info("Simulated incorporations ");
						LOGGER.info("INC_C " + INC_C);
						LOGGER.info("INC_N " + INC_N);
						LOGGER.info("INC_CN " + INC_CN);
						LOGGER.info("Check C incorporation...");
						IsotopeFormula formulaC = new IsotopeFormula();
						formulaC.put(Isotope.C_13, 1);
						formulaC.put(Isotope.N_15, 0);
						LOGGER.info("calculatedIncC " + correctedMap.get(formulaC));
						LOGGER.info("expectedIncC " + INC_C);

						LOGGER.info("Check N incorporation...");
						IsotopeFormula formulaN = new IsotopeFormula();
						formulaN.put(Isotope.C_13, 0);
						formulaN.put(Isotope.N_15, 1);
						LOGGER.info("calculatedIncN " + correctedMap.get(formulaN));
						LOGGER.info("expectedIncN " + INC_N);
						LOGGER.info("Check CN incorporation...");
						IsotopeFormula formulaCN = new IsotopeFormula();
						formulaCN.put(Isotope.C_13, 1);
						formulaCN.put(Isotope.N_15, 1);
						LOGGER.info("calculatedIncCN " + correctedMap.get(formulaCN));
						LOGGER.info("expectedIncCN " + INC_CN);
						Double actualCN = correctedMap.get(formulaCN) != null ? correctedMap.get(formulaCN) : 0.0;
						Double actualC = correctedMap.get(formulaC) != null ? correctedMap.get(formulaC) : 0.0;
						Double actualN = correctedMap.get(formulaN) != null ? correctedMap.get(formulaN) : 0.0;
						/*
						 * compare the recalculated with the start values
						 */
						assertTrue(MathUtils.approximatelyEquals(actualC, INC_C, ALLOWED_INC_ERROR));
						assertTrue(MathUtils.approximatelyEquals(actualN, INC_N, ALLOWED_INC_ERROR));
						assertTrue(MathUtils.approximatelyEquals(actualCN, INC_CN, ALLOWED_INC_ERROR));
						LOGGER.info("---------------------------- PASSED -----------------------------");
					}
				}
			}
		}
	}

	public void testIncorporationRate02() throws TypeMismatchException {
		double maxError = 0.0;
		for (int c = 0; c < 10; c++) {
			final double INC_C = 0.0 + c * 0.1;
			final Integer PRECISION = 4;
			final double MIN_FREQUENCY = 0.001;
			/*
			 * simulate the spectrum
			 */
			IsotopePatternSimulatorRequest request = new IsotopePatternSimulatorRequest();
			request.setAnalyzeMassShifts(true);
			request.setCharge(1);
			request.setIncorporationRate(new IncorporationRate(INC_C));
			request.setFragments(new FragmentList(new Fragment(FragmentKey.GLY_276, "C10H26NO2Si3", "C")));
			request.setMinimalIntensity(MIN_FREQUENCY);
			request.setRoundedIntensityPrecision(PRECISION);
			request.setRoundedMassPrecision(PRECISION);
			request.setTargetIntensityType(IntensityType.MID);
			IsotopePatternSimulatorResponse response = IsotopePatternSimulator.simulate(request);
			MSShiftDatabase msShiftDatabase = (MSShiftDatabase) response.getMsDatabaseList().get(0);

			LOGGER.info(msShiftDatabase.toString());
			IncorporationMap incorporationMap = new IncorporationMap(msShiftDatabase.getMixedSpectrum(),
					msShiftDatabase.getMixedMassShifts(), new IsotopeList(Isotope.C_13));
			LOGGER.info("UncorrectedIncorporation " + incorporationMap.asTable());
			/*
			 * recalculate the incorporation rates
			 */
			ElementFormula fragmentFormula = ElementFormula.fromString(msShiftDatabase.getFragmentFormula());
			ElementFormula elementFormula = new ElementFormula();
			elementFormula.put(Element.C, fragmentFormula.get(Element.C));
			IncorporationMap correctedMap = incorporationMap.correctForNaturalAbundance(elementFormula);

			LOGGER.info("correctedMap " + correctedMap.asTable());
			LOGGER.info("Simulated incorporation ");
			LOGGER.info("INC_C " + INC_C);
			LOGGER.info("Check C incorporation...");
			IsotopeFormula formulaC = new IsotopeFormula();
			formulaC.put(Isotope.C_13, 1);
			LOGGER.info("calculatedIncC " + correctedMap.get(formulaC));
			LOGGER.info("expectedIncC " + INC_C);
			Double actualC = correctedMap.get(formulaC) != null ? correctedMap.get(formulaC) : 0.0;
			if (Math.abs(actualC - INC_C) > maxError) {
				maxError = Math.abs(actualC - INC_C);
			}
			/*
			 * compare the recalculated with the start values
			 */
			assertTrue(MathUtils.approximatelyEquals(actualC, INC_C, ALLOWED_INC_ERROR));
			LOGGER.info("---------------------------- PASSED -----------------------------");
		}
		LOGGER.info("maxError " + maxError);
	}

	public void testIncorporationRate03() throws TypeMismatchException {
		Double maxError = 0.0;
		for (int n = 0; n < 10; n++) {
			final double INC_N = 0.0 + n * 0.1;
			final Integer PRECISION = 4;
			final double MIN_FREQUENCY = 0.001;
			/*
			 * simulate the spectrum
			 */
			IsotopePatternSimulatorRequest request = new IsotopePatternSimulatorRequest();
			request.setAnalyzeMassShifts(true);
			request.setCharge(1);
			request.setIncorporationRate(new IncorporationRate(INC_N));
			request.setFragments(new FragmentList(new Fragment(FragmentKey.GLY_276, "C10H26NO2Si3", "N")));
			request.setMinimalIntensity(MIN_FREQUENCY);
			request.setRoundedIntensityPrecision(PRECISION);
			request.setRoundedMassPrecision(PRECISION);
			request.setTargetIntensityType(IntensityType.MID);
			IsotopePatternSimulatorResponse response = IsotopePatternSimulator.simulate(request);
			MSShiftDatabase msShiftDatabase = (MSShiftDatabase) response.getMsDatabaseList().get(0);

			LOGGER.info(msShiftDatabase.toString());

			IncorporationMap incorporationMap = new IncorporationMap(msShiftDatabase.getMixedSpectrum(),
					msShiftDatabase.getMixedMassShifts(), new IsotopeList(Isotope.N_15));
			LOGGER.info("UncorrectedIncorporation" + incorporationMap.asTable());
			/*
			 * recalculate the incorporation rates
			 */
			ElementFormula fragmentFormula = ElementFormula.fromString(msShiftDatabase.getFragmentFormula());
			ElementFormula elementFormula = new ElementFormula();
			elementFormula.put(Element.N, fragmentFormula.get(Element.N));
			IncorporationMap correctedMap = incorporationMap.correctForNaturalAbundance(elementFormula);

			LOGGER.info("correctedMap " + correctedMap.asTable());

			LOGGER.info("Simulated incorporation");
			LOGGER.info("INC_N " + INC_N);

			LOGGER.info("Check N incorporation...");
			IsotopeFormula formulaN = new IsotopeFormula();
			formulaN.put(Isotope.N_15, 1);
			LOGGER.info("calculatedIncN " + correctedMap.get(formulaN));
			LOGGER.info("expectedIncN " + INC_N);

			Double actualN = correctedMap.get(formulaN) != null ? correctedMap.get(formulaN) : 0.0;
			if (Math.abs(actualN - INC_N) > maxError) {
				maxError = Math.abs(actualN - INC_N);
			}
			/*
			 * compare the recalculated with the start values
			 */
			assertTrue(MathUtils.approximatelyEquals(actualN, INC_N, ALLOWED_INC_ERROR));
			LOGGER.info("---------------------------- PASSED -----------------------------");
		}
		LOGGER.info("maxError " + maxError);
	}

	public void testIncorporationRate04() throws TypeMismatchException {
		if (TEST_INCS) {
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
					/*
					 * simulate the spectrum
					 */
					IsotopePatternSimulatorRequest request = new IsotopePatternSimulatorRequest();
					request.setAnalyzeMassShifts(true);
					request.setCharge(1);
					request.setFragments(new FragmentList(new Fragment("C10H26NO2Si3", "C2N")));
					request.setTotalNumberOfFragments(NUMBER_OF_FRAGMENTS);
					request.setMinimalIntensity(MIN_FREQUENCY);
					request.setRoundedIntensityPrecision(PRECISION);
					request.setRoundedMassPrecision(PRECISION);
					request.setTargetIntensityType(IntensityType.MID);
					request.setTracer1(Element.C);
					request.setTracer1Inc(new IncorporationRate(INC_C2));
					request.setTracer2(Element.N);
					request.setTracer2Inc(new IncorporationRate(INC_N));
					request.setTracerAllInc(new IncorporationRate(0));
					IsotopePatternSimulatorResponse response = IsotopePatternSimulator
							.simulateIndependentTracerIncorporation(request);
					MSShiftDatabase msShiftDatabase = (MSShiftDatabase) response.getMsDatabaseList().get(0);

					LOGGER.info(msShiftDatabase.toString());

					IncorporationMap incorporationMap = new IncorporationMap(msShiftDatabase.getMixedSpectrum(),
							msShiftDatabase.getMixedMassShifts(), new IsotopeList(Isotope.C_13, Isotope.N_15));
					LOGGER.info("UncorrectedIncorporation" + incorporationMap.asTable());
					/*
					 * recalculate the incorporation rates
					 */
					ElementFormula fragmentFormula = ElementFormula.fromString(msShiftDatabase.getFragmentFormula());
					ElementFormula elementFormula = new ElementFormula();
					elementFormula.put(Element.C, fragmentFormula.get(Element.C));
					elementFormula.put(Element.N, fragmentFormula.get(Element.N));
					IncorporationMap correctedMap = incorporationMap.correctForNaturalAbundance(elementFormula);

					LOGGER.info("correctedMap " + correctedMap.asTable());

					LOGGER.info("Simulated incorporations");
					LOGGER.info("INC_C2 " + INC_C2);
					LOGGER.info("INC_N " + INC_N);

					LOGGER.info("Check C incorporation...");
					IsotopeFormula formulaC2 = new IsotopeFormula();
					formulaC2.put(Isotope.C_13, 2);
					formulaC2.put(Isotope.N_15, 0);
					LOGGER.info("calculatedIncC2 " + correctedMap.get(formulaC2));
					LOGGER.info("expectedIncC2 " + INC_C2);

					LOGGER.info("Check N incorporation...");
					IsotopeFormula formulaN = new IsotopeFormula();
					formulaN.put(Isotope.C_13, 0);
					formulaN.put(Isotope.N_15, 1);
					LOGGER.info("calculatedIncN " + correctedMap.get(formulaN));
					LOGGER.info("expectedIncN " + INC_N);

					LOGGER.info("Check CN incorporation...");
					IsotopeFormula formulaCN = new IsotopeFormula();
					formulaCN.put(Isotope.C_13, 2);
					formulaCN.put(Isotope.N_15, 1);
					LOGGER.info("calculatedIncCN " + correctedMap.get(formulaCN));
					LOGGER.info("expectedIncCN " + 0.0);

					Double actualCN = correctedMap.get(formulaCN) != null ? correctedMap.get(formulaCN) : 0.0;
					Double actualC = correctedMap.get(formulaC2) != null ? correctedMap.get(formulaC2) : 0.0;
					Double actualN = correctedMap.get(formulaN) != null ? correctedMap.get(formulaN) : 0.0;
					/*
					 * compare the recalculated with the start values
					 */
					assertTrue(MathUtils.approximatelyEquals(actualC, INC_C2, ALLOWED_INC_ERROR));
					assertTrue(MathUtils.approximatelyEquals(actualN, INC_N, ALLOWED_INC_ERROR));
					assertTrue(MathUtils.approximatelyEquals(actualCN, 0.0, ALLOWED_INC_ERROR));
					LOGGER.info("---------------------------- PASSED -----------------------------");
				}
			}
		}
	}

	public void correctionGlnUnlabeledTest() {
		MassSpectrum measured = new MassSpectrum(IntensityType.ABSOLUTE, SpectrumType.CENTROIDED);
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
		shifts.put(new MassShiftList(new MassShift(0, 2, null), new MassShift(2, 4, null)),
				new IsotopeListList(new IsotopeList(Isotope.Si_29), new IsotopeList(Isotope.N_15)));
		shifts.put(new MassShiftList(new MassShift(0, 5, null)), new IsotopeListList(new IsotopeList(Isotope.Si_30)));
		shifts.put(new MassShiftList(new MassShift(0, 3, null), new MassShift(3, 6, null)),
				new IsotopeListList(new IsotopeList(Isotope.C_13), new IsotopeList(Isotope.Si_29)));
		shifts.put(new MassShiftList(new MassShift(0, 3, null), new MassShift(3, 7, null)),
				new IsotopeListList(new IsotopeList(Isotope.C_13), new IsotopeList(Isotope.C_13)));
		shifts.put(new MassShiftList(new MassShift(0, 5, null), new MassShift(5, 8, null)),
				new IsotopeListList(new IsotopeList(Isotope.Si_30), new IsotopeList(Isotope.C_13)));

		IncorporationMap incorporationMap = new IncorporationMap(measured, shifts,
				new IsotopeList(Isotope.C_13, Isotope.N_15));
		LOGGER.info("incorporationMap " + incorporationMap.asTable());
		LOGGER.info("incorporationMap.normalize() " + incorporationMap.normalize(4).asTable());
		ElementFormula fragmentFormula = ElementFormula.fromString("C7H14NOSi");
		ElementFormula elementFormula = new ElementFormula();
		elementFormula.put(Element.C, fragmentFormula.get(Element.C));
		elementFormula.put(Element.N, fragmentFormula.get(Element.N));
		IncorporationMap correctedMap = incorporationMap.correctForNaturalAbundance(elementFormula);
		LOGGER.info("correctedMap " + correctedMap.asTable());
		LOGGER.info("correctedMap.normalize() " + correctedMap.normalize(4).asTable());

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

	public void correctionGlnTotallyCNLabeledTest() {
		MassSpectrum measured = new MassSpectrum(IntensityType.ABSOLUTE, SpectrumType.CENTROIDED);
		measured.put(161.094388, 3383957504.000000);
		measured.put(162.093845, 167757680.000000);
		measured.put(162.097430, 96693112.000000);
		measured.put(162.100503, 2915952.500000);
		measured.put(163.091187, 109522104.000000);
		measured.put(163.098605, 6520942.500000);
		measured.put(163.104047, 994677.062500);

		MassShiftDataSet shifts = new MassShiftDataSet();
		shifts.put(new MassShiftList(new MassShift(0, 0, null)), new IsotopeListList(
				new IsotopeList(Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.N_15)));
		shifts.put(new MassShiftList(new MassShift(0, 1, null)), new IsotopeListList(
				new IsotopeList(Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.N_15, Isotope.Si_29)));
		shifts.put(new MassShiftList(new MassShift(0, 2, null)), new IsotopeListList(
				new IsotopeList(Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.N_15, Isotope.C_13)));
		shifts.put(new MassShiftList(new MassShift(0, 3, null)), new IsotopeListList(
				new IsotopeList(Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.N_15, Isotope.H_2)));
		shifts.put(new MassShiftList(new MassShift(0, 4, null)), new IsotopeListList(
				new IsotopeList(Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.N_15, Isotope.Si_30)));
		shifts.put(new MassShiftList(new MassShift(0, 2, null), new MassShift(2, 5, null)), new IsotopeListList(
				new IsotopeList(Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.N_15, Isotope.C_13),
				new IsotopeList(Isotope.Si_29)));
		shifts.put(new MassShiftList(new MassShift(0, 6, null)), new IsotopeListList(
				new IsotopeList(Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.N_15, Isotope.O_18)));

		IncorporationMap incorporationMap = new IncorporationMap(measured, shifts,
				new IsotopeList(Isotope.C_13, Isotope.N_15));
		LOGGER.info("incorporationMap " + incorporationMap.asTable());
		LOGGER.info("incorporationMap.normalize() " + incorporationMap.normalize(4).asTable());
		ElementFormula fragmentFormula = ElementFormula.fromString("C7H14NOSi");
		ElementFormula elementFormula = new ElementFormula();
		elementFormula.put(Element.C, fragmentFormula.get(Element.C));
		elementFormula.put(Element.N, fragmentFormula.get(Element.N));
		IncorporationMap correctedMap = incorporationMap.correctForNaturalAbundance(elementFormula);
		LOGGER.info("correctedMap " + correctedMap.asTable());
		LOGGER.info("correctedMap.normalize() " + correctedMap.normalize(4).asTable());

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

	public void correctionGlnTotallyCLabeledTest() {
		MassSpectrum measured = new MassSpectrum(IntensityType.ABSOLUTE, SpectrumType.CENTROIDED);
		measured.put(160.097400, 1584645632.000000);
		measured.put(161.094435, 3858969.500000);
		measured.put(161.096895, 75836104.000000);
		measured.put(161.100447, 44920384.000000);
		measured.put(161.103490, 1408230.125000);
		measured.put(162.094165, 51429216.000000);
		measured.put(162.101593, 2085006.750000);
		measured.put(163.097390, 1833341.625000);

		MassShiftDataSet shifts = new MassShiftDataSet();
		shifts.put(new MassShiftList(new MassShift(0, 0, null)),
				new IsotopeListList(new IsotopeList(Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.C_13)));
		shifts.put(new MassShiftList(new MassShift(0, 1, null)), new IsotopeListList(
				new IsotopeList(Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.N_15)));
		shifts.put(new MassShiftList(new MassShift(0, 2, null)), new IsotopeListList(
				new IsotopeList(Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.Si_29)));
		shifts.put(new MassShiftList(new MassShift(0, 3, null)), new IsotopeListList(
				new IsotopeList(Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.C_13)));
		shifts.put(new MassShiftList(new MassShift(0, 4, null)), new IsotopeListList(
				new IsotopeList(Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.H_2)));
		shifts.put(new MassShiftList(new MassShift(0, 5, null)), new IsotopeListList(
				new IsotopeList(Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.Si_30)));
		shifts.put(new MassShiftList(new MassShift(0, 6, null)), new IsotopeListList(
				new IsotopeList(Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.O_18)));
		shifts.put(new MassShiftList(new MassShift(0, 5, null), new MassShift(5, 7, null)),
				new IsotopeListList(
						new IsotopeList(Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.C_13, Isotope.Si_30),
						new IsotopeList(Isotope.C_13)));

		IncorporationMap incorporationMap = new IncorporationMap(measured, shifts,
				new IsotopeList(Isotope.C_13, Isotope.N_15));
		LOGGER.info("incorporationMap " + incorporationMap.asTable());
		LOGGER.info("incorporationMap.normalize() " + incorporationMap.normalize(4).asTable());
		ElementFormula fragmentFormula = ElementFormula.fromString("C7H14NOSi");
		ElementFormula elementFormula = new ElementFormula();
		elementFormula.put(Element.C, fragmentFormula.get(Element.C));
		elementFormula.put(Element.N, fragmentFormula.get(Element.N));
		IncorporationMap correctedMap = incorporationMap.correctForNaturalAbundance(elementFormula);
		LOGGER.info("correctedMap " + correctedMap.asTable());
		LOGGER.info("correctedMap.normalize() " + correctedMap.normalize(4).asTable());

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

	public void correctionGlnTotallyNLabeledTest() {
		MassSpectrum measured = new MassSpectrum(IntensityType.ABSOLUTE, SpectrumType.CENTROIDED);
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
		shifts.put(new MassShiftList(new MassShift(0, 0, null)), new IsotopeListList(new IsotopeList(Isotope.N_15)));
		shifts.put(new MassShiftList(new MassShift(0, 1, null)),
				new IsotopeListList(new IsotopeList(Isotope.N_15, Isotope.Si_29)));
		shifts.put(new MassShiftList(new MassShift(0, 2, null)),
				new IsotopeListList(new IsotopeList(Isotope.N_15, Isotope.C_13)));
		shifts.put(new MassShiftList(new MassShift(0, 3, null)),
				new IsotopeListList(new IsotopeList(Isotope.N_15, Isotope.H_2)));
		shifts.put(new MassShiftList(new MassShift(0, 4, null)),
				new IsotopeListList(new IsotopeList(Isotope.N_15, Isotope.Si_30)));
		shifts.put(new MassShiftList(new MassShift(0, 2, null), new MassShift(2, 5, null)),
				new IsotopeListList(new IsotopeList(Isotope.N_15, Isotope.C_13), new IsotopeList(Isotope.Si_29)));
		shifts.put(new MassShiftList(new MassShift(0, 6, null)),
				new IsotopeListList(new IsotopeList(Isotope.N_15, Isotope.O_18)));
		shifts.put(new MassShiftList(new MassShift(0, 2, null), new MassShift(2, 7, null)),
				new IsotopeListList(new IsotopeList(Isotope.N_15, Isotope.C_13), new IsotopeList(Isotope.C_13)));
		shifts.put(new MassShiftList(new MassShift(0, 4, null), new MassShift(4, 8, null)),
				new IsotopeListList(new IsotopeList(Isotope.N_15, Isotope.Si_30), new IsotopeList(Isotope.C_13)));

		IncorporationMap incorporationMap = new IncorporationMap(measured, shifts,
				new IsotopeList(Isotope.C_13, Isotope.N_15));
		LOGGER.info("incorporationMap " + incorporationMap.asTable());
		LOGGER.info("incorporationMap.normalize() ", incorporationMap.normalize(4).asTable());
		ElementFormula fragmentFormula = ElementFormula.fromString("C7H14NOSi");
		ElementFormula elementFormula = new ElementFormula();
		elementFormula.put(Element.C, fragmentFormula.get(Element.C));
		elementFormula.put(Element.N, fragmentFormula.get(Element.N));
		IncorporationMap correctedMap = incorporationMap.correctForNaturalAbundance(elementFormula);
		LOGGER.info("correctedMap " + correctedMap.asTable());
		LOGGER.info("correctedMap.normalize() " + correctedMap.normalize(4).asTable());

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

	public void test12CGLN() {
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
		LOGGER.info("uncorrectedMap " + incorporationMap.asTable());
		LOGGER.info("uncorrectedMap normalized " + incorporationMap.normalize(4).asTable());
		ElementFormula fragmentFormula = ElementFormula.fromString("C7H14NOSi");
		ElementFormula elementFormula = new ElementFormula();
		elementFormula.put(Element.C, fragmentFormula.get(Element.C));
		elementFormula.put(Element.N, fragmentFormula.get(Element.N));
		IncorporationMap correctedMap = incorporationMap.correctForNaturalAbundance(elementFormula);
		LOGGER.info("correctedMap " + correctedMap.asTable());
		LOGGER.info("correctedMap normalized " + correctedMap.normalize(4).asTable());
		IncorporationMap normalizedCorrectedMap = correctedMap.normalize(4);
		assertEquals(1.0, normalizedCorrectedMap.get(0, 0));
		assertEquals(0.0, normalizedCorrectedMap.get(0, 1));
		assertEquals(0.0, normalizedCorrectedMap.get(1, 0));
		assertEquals(0.0, normalizedCorrectedMap.get(2, 0));
	}

	public void test13C15NGLN() {
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
		LOGGER.info("uncorrectedMap " + incorporationMap.asTable());
		LOGGER.info("uncorrectedMap normalized " + incorporationMap.normalize(4).asTable());
		ElementFormula fragmentFormula = ElementFormula.fromString("C7H14NOSi");
		ElementFormula elementFormula = new ElementFormula();
		elementFormula.put(Element.C, fragmentFormula.get(Element.C));
		elementFormula.put(Element.N, fragmentFormula.get(Element.N));
		IncorporationMap correctedMap = incorporationMap.correctForNaturalAbundance(elementFormula);
		LOGGER.info("correctedMap " + correctedMap.asTable());
		LOGGER.info("correctedMap normalized " + correctedMap.normalize(4).asTable());
		IncorporationMap normalizedCorrectedMap = correctedMap.normalize(4);
		assertEquals(1.0, normalizedCorrectedMap.get(4, 1));
		assertEquals(0.0, normalizedCorrectedMap.get(5, 1));
	}

	public void test13CGLN() {
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
		LOGGER.info("uncorrectedMap " + incorporationMap.asTable());
		LOGGER.info("uncorrectedMap normalized " + incorporationMap.normalize(4).asTable());
		ElementFormula fragmentFormula = ElementFormula.fromString("C7H14NOSi");
		ElementFormula elementFormula = new ElementFormula();
		elementFormula.put(Element.C, fragmentFormula.get(Element.C));
		elementFormula.put(Element.N, fragmentFormula.get(Element.N));
		IncorporationMap correctedMap = incorporationMap.correctForNaturalAbundance(elementFormula);
		LOGGER.info("correctedMap " + correctedMap.asTable());
		LOGGER.info("correctedMap normalized " + correctedMap.normalize(4).asTable());
		IncorporationMap normalizedCorrectedMap = correctedMap.normalize(4);
		assertEquals(1.0, normalizedCorrectedMap.get(4, 0));
		assertEquals(0.0, normalizedCorrectedMap.get(4, 1));
		assertEquals(0.0, normalizedCorrectedMap.get(5, 0));
	}

//		IsotopeFormula a = new IsotopeFormula();
//		a.put(Isotope.C_13, 1);
//		a.put(Isotope.N_15, 0);

	public void testMixA() {
		LOGGER.info("MixA");
		IsotopeFormula _1 = new IsotopeFormula();
		_1.put(Isotope.C_13, 0);
		_1.put(Isotope.N_15, 0);

		IsotopeFormula _2 = new IsotopeFormula();
		_2.put(Isotope.C_13, 0);
		_2.put(Isotope.N_15, 1);

		IsotopeFormula _3 = new IsotopeFormula();
		_3.put(Isotope.C_13, 1);
		_3.put(Isotope.N_15, 0);

		IsotopeFormula _4 = new IsotopeFormula();
		_4.put(Isotope.C_13, 1);
		_4.put(Isotope.N_15, 1);

		IsotopeFormula _5 = new IsotopeFormula();
		_5.put(Isotope.C_13, 2);
		_5.put(Isotope.N_15, 0);

		IsotopeFormula _6 = new IsotopeFormula();
		_6.put(Isotope.C_13, 4);
		_6.put(Isotope.N_15, 0);

		IsotopeFormula _7 = new IsotopeFormula();
		_7.put(Isotope.C_13, 4);
		_7.put(Isotope.N_15, 1);

		IsotopeFormula _8 = new IsotopeFormula();
		_8.put(Isotope.C_13, 5);
		_8.put(Isotope.N_15, 0);

		IsotopeFormula[] isotopeFormulas = { _1, _2, _3, _4, _5, _6, _7, _8 };
		Double[] intensities = { 2.14E+09 + 9.55E+07 + 3061416.25, 6358834 + 5.88E+07, 1.51E+08 + 5468670.5, 4384727.0,
				4592408.5, 2.36E+09 + 1.21E+08 + 2262192, 5495782.0 + 8.08E+07, 7.14E+07 + 2812413 };
		IncorporationMap incorporationMap = new IncorporationMap(isotopeFormulas, intensities);
		LOGGER.info("uncorrectedMap " + incorporationMap.asTable());
		LOGGER.info("uncorrectedMap normalized " + incorporationMap.normalize(4).asTable());
		ElementFormula fragmentFormula = ElementFormula.fromString("C7H14NOSi");
		ElementFormula elementFormula = new ElementFormula();
		elementFormula.put(Element.C, fragmentFormula.get(Element.C));
		elementFormula.put(Element.N, fragmentFormula.get(Element.N));
		IncorporationMap correctedMap = incorporationMap.correctForNaturalAbundance(elementFormula);
		LOGGER.info("correctedMap " + correctedMap.asTable());
		LOGGER.info("correctedMap normalized " + correctedMap.normalize(4).asTable());
		IncorporationMap normalizedCorrectedMap = correctedMap.normalize(4);
//		assertEquals(1.0, normalizedCorrectedMap.get(4, 0));
//		assertEquals(0.0, normalizedCorrectedMap.get(4, 1));
//		assertEquals(0.0, normalizedCorrectedMap.get(5, 0));
	}

	public void testMixB() {
		LOGGER.info("MixB");
		IsotopeFormula _1 = new IsotopeFormula();
		_1.put(Isotope.C_13, 0);
		_1.put(Isotope.N_15, 0);

		IsotopeFormula _2 = new IsotopeFormula();
		_2.put(Isotope.C_13, 0);
		_2.put(Isotope.N_15, 1);

		IsotopeFormula _3 = new IsotopeFormula();
		_3.put(Isotope.C_13, 1);
		_3.put(Isotope.N_15, 0);

		IsotopeFormula _4 = new IsotopeFormula();
		_4.put(Isotope.C_13, 1);
		_4.put(Isotope.N_15, 1);

		IsotopeFormula _5 = new IsotopeFormula();
		_5.put(Isotope.C_13, 2);
		_5.put(Isotope.N_15, 0);

		IsotopeFormula _6 = new IsotopeFormula();
		_6.put(Isotope.C_13, 4);
		_6.put(Isotope.N_15, 0);

		IsotopeFormula _7 = new IsotopeFormula();
		_7.put(Isotope.C_13, 4);
		_7.put(Isotope.N_15, 1);

		IsotopeFormula[] isotopeFormulas = { _1, _2, _3, _4, _5, _6, _7, };
		Double[] intensities = { 2.29E+09 + 1.53E+07 + 1.86E+08, 2.75E+09 + 1.93E+08, 1.80E+08 + 6760809 + 5204173.5,
				1.44E+07, 5719703.0, 2790484.75, 1173324.625 };
		IncorporationMap incorporationMap = new IncorporationMap(isotopeFormulas, intensities);
		LOGGER.info("uncorrectedMap " + incorporationMap.asTable());
		LOGGER.info("uncorrectedMap normalized " + incorporationMap.normalize(4).asTable());
		ElementFormula fragmentFormula = ElementFormula.fromString("C7H14NOSi");
		ElementFormula elementFormula = new ElementFormula();
		elementFormula.put(Element.C, fragmentFormula.get(Element.C));
		elementFormula.put(Element.N, fragmentFormula.get(Element.N));
		IncorporationMap correctedMap = incorporationMap.correctForNaturalAbundance(elementFormula);
		LOGGER.info("correctedMap " + correctedMap.asTable());
		LOGGER.info("correctedMap normalized " + correctedMap.normalize(4).asTable());
		IncorporationMap normalizedCorrectedMap = correctedMap.normalize(4);
//		assertEquals(1.0, normalizedCorrectedMap.get(4, 0));
//		assertEquals(0.0, normalizedCorrectedMap.get(4, 1));
//		assertEquals(0.0, normalizedCorrectedMap.get(5, 0));
	}

	public void testMixC() {
		LOGGER.info("MixC");
		IsotopeFormula _1 = new IsotopeFormula();
		_1.put(Isotope.C_13, 0);
		_1.put(Isotope.N_15, 0);

		IsotopeFormula _2 = new IsotopeFormula();
		_2.put(Isotope.C_13, 0);
		_2.put(Isotope.N_15, 1);

		IsotopeFormula _3 = new IsotopeFormula();
		_3.put(Isotope.C_13, 1);
		_3.put(Isotope.N_15, 0);

		IsotopeFormula _4 = new IsotopeFormula();
		_4.put(Isotope.C_13, 1);
		_4.put(Isotope.N_15, 1);

		IsotopeFormula _5 = new IsotopeFormula();
		_5.put(Isotope.C_13, 2);
		_5.put(Isotope.N_15, 0);

		IsotopeFormula[] isotopeFormulas = { _1, _2, _3, _4, _5 };
		Double[] intensities = { 3.77E+09 + 1.75E+08 + 3646758.5, 9576020 + 1.13E+08, 2.78E+08 + 1.15E+07, 8700155.0,
				8158939.0 };
		IncorporationMap incorporationMap = new IncorporationMap(isotopeFormulas, intensities);
		LOGGER.info("uncorrectedMap " + incorporationMap.asTable());
		LOGGER.info("uncorrectedMap normalized " + incorporationMap.normalize(4).asTable());
		ElementFormula fragmentFormula = ElementFormula.fromString("C7H14NOSi");
		ElementFormula elementFormula = new ElementFormula();
		elementFormula.put(Element.C, fragmentFormula.get(Element.C));
		elementFormula.put(Element.N, fragmentFormula.get(Element.N));
		IncorporationMap correctedMap = incorporationMap.correctForNaturalAbundance(elementFormula);
		LOGGER.info("correctedMap " + correctedMap.asTable());
		LOGGER.info("correctedMap normalized " + correctedMap.normalize(4).asTable());
		IncorporationMap normalizedCorrectedMap = correctedMap.normalize(4);
//		assertEquals(1.0, normalizedCorrectedMap.get(4, 0));
//		assertEquals(0.0, normalizedCorrectedMap.get(4, 1));
//		assertEquals(0.0, normalizedCorrectedMap.get(5, 0));
	}

	public void testMixD() {
		LOGGER.info("MixD");
		IsotopeFormula _1 = new IsotopeFormula();
		_1.put(Isotope.C_13, 0);
		_1.put(Isotope.N_15, 0);

		IsotopeFormula _2 = new IsotopeFormula();
		_2.put(Isotope.C_13, 0);
		_2.put(Isotope.N_15, 1);

		IsotopeFormula _3 = new IsotopeFormula();
		_3.put(Isotope.C_13, 1);
		_3.put(Isotope.N_15, 0);

		IsotopeFormula _4 = new IsotopeFormula();
		_4.put(Isotope.C_13, 1);
		_4.put(Isotope.N_15, 1);

		IsotopeFormula _6 = new IsotopeFormula();
		_6.put(Isotope.C_13, 4);
		_6.put(Isotope.N_15, 0);

		IsotopeFormula _7 = new IsotopeFormula();
		_7.put(Isotope.C_13, 4);
		_7.put(Isotope.N_15, 1);

		IsotopeFormula _8 = new IsotopeFormula();
		_8.put(Isotope.C_13, 5);
		_8.put(Isotope.N_15, 0);

		IsotopeFormula[] isotopeFormulas = { _1, _2, _3, _4, _6, _7, _8 };
		Double[] intensities = { 1.09E+07 + 2.74E+08, 3.83E+09 + 1.74E+08 + 5680198, 4220880.5 + 8957809, 1.09E+07,
				3.23E+09 + 1.49E+08 + 4651094, 1.11E+07 + 1.01E+08 + 5334199.5, 1.10E+08 + 4461271.5,

		};
		IncorporationMap incorporationMap = new IncorporationMap(isotopeFormulas, intensities);
		LOGGER.info("uncorrectedMap " + incorporationMap.asTable());
		LOGGER.info("uncorrectedMap normalized " + incorporationMap.normalize(4).asTable());
		ElementFormula fragmentFormula = ElementFormula.fromString("C7H14NOSi");
		ElementFormula elementFormula = new ElementFormula();
		elementFormula.put(Element.C, fragmentFormula.get(Element.C));
		elementFormula.put(Element.N, fragmentFormula.get(Element.N));
		IncorporationMap correctedMap = incorporationMap.correctForNaturalAbundance(elementFormula);
		LOGGER.info("correctedMap " + correctedMap.asTable());
		LOGGER.info("correctedMap normalized " + correctedMap.normalize(4).asTable());
		IncorporationMap normalizedCorrectedMap = correctedMap.normalize(4);
//		assertEquals(1.0, normalizedCorrectedMap.get(4, 0));
//		assertEquals(0.0, normalizedCorrectedMap.get(4, 1));
//		assertEquals(0.0, normalizedCorrectedMap.get(5, 0));
	}

	public void testMixE() {
		LOGGER.info("MixE");
		IsotopeFormula _1 = new IsotopeFormula();
		_1.put(Isotope.C_13, 0);
		_1.put(Isotope.N_15, 0);

		IsotopeFormula _2 = new IsotopeFormula();
		_2.put(Isotope.C_13, 0);
		_2.put(Isotope.N_15, 1);

		IsotopeFormula _3 = new IsotopeFormula();
		_3.put(Isotope.C_13, 1);
		_3.put(Isotope.N_15, 0);

		IsotopeFormula _6 = new IsotopeFormula();
		_6.put(Isotope.C_13, 4);
		_6.put(Isotope.N_15, 0);

		IsotopeFormula _7 = new IsotopeFormula();
		_7.put(Isotope.C_13, 4);
		_7.put(Isotope.N_15, 1);

		IsotopeFormula _8 = new IsotopeFormula();
		_8.put(Isotope.C_13, 5);
		_8.put(Isotope.N_15, 0);

		IsotopeFormula[] isotopeFormulas = { _1, _2, _3, _6, _7, _8 };
		Double[] intensities = { 3725457.5, 3385010.75, 2947243.75, 2.35E+09 + 9.98E+07,
				3.33E+09 + 2.35E+08 + 4329975.5, 7.71E+07 + 5394337.5, };
		IncorporationMap incorporationMap = new IncorporationMap(isotopeFormulas, intensities);
		LOGGER.info("uncorrectedMap " + incorporationMap.asTable());
		LOGGER.info("uncorrectedMap normalized " + incorporationMap.normalize(4).asTable());
		ElementFormula fragmentFormula = ElementFormula.fromString("C7H14NOSi");
		ElementFormula elementFormula = new ElementFormula();
		elementFormula.put(Element.C, fragmentFormula.get(Element.C));
		elementFormula.put(Element.N, fragmentFormula.get(Element.N));
		IncorporationMap correctedMap = incorporationMap.correctForNaturalAbundance(elementFormula);
		LOGGER.info("correctedMap " + correctedMap.asTable());
		LOGGER.info("correctedMap normalized " + correctedMap.normalize(4).asTable());
		IncorporationMap normalizedCorrectedMap = correctedMap.normalize(4);
//		assertEquals(1.0, normalizedCorrectedMap.get(4, 0));
//		assertEquals(0.0, normalizedCorrectedMap.get(4, 1));
//		assertEquals(0.0, normalizedCorrectedMap.get(5, 0));
	}

	public void testMixF() {
		LOGGER.info("MixF");
		IsotopeFormula _1 = new IsotopeFormula();
		_1.put(Isotope.C_13, 0);
		_1.put(Isotope.N_15, 0);

		IsotopeFormula _2 = new IsotopeFormula();
		_2.put(Isotope.C_13, 0);
		_2.put(Isotope.N_15, 1);

		IsotopeFormula _3 = new IsotopeFormula();
		_3.put(Isotope.C_13, 1);
		_3.put(Isotope.N_15, 0);

		IsotopeFormula _4 = new IsotopeFormula();
		_4.put(Isotope.C_13, 1);
		_4.put(Isotope.N_15, 1);

		IsotopeFormula _6 = new IsotopeFormula();
		_6.put(Isotope.C_13, 4);
		_6.put(Isotope.N_15, 0);

		IsotopeFormula _7 = new IsotopeFormula();
		_7.put(Isotope.C_13, 4);
		_7.put(Isotope.N_15, 1);

		IsotopeFormula _8 = new IsotopeFormula();
		_8.put(Isotope.C_13, 5);
		_8.put(Isotope.N_15, 0);

		IsotopeFormula[] isotopeFormulas = { _1, _2, _3, _4, _6, _7, _8 };
		Double[] intensities = { 5698802 + 1.05E+08, 1.52E+09 + 6.68E+07 + 3231464.25, 7299979.5, 4369896.0,
				1.63E+07 + 6.38E+07, 2.21E+09 + 1.01E+08 + 2971350.75, 3236815.75

		};
		IncorporationMap incorporationMap = new IncorporationMap(isotopeFormulas, intensities);
		LOGGER.info("uncorrectedMap " + incorporationMap.asTable());
		LOGGER.info("uncorrectedMap normalized " + incorporationMap.normalize(4).asTable());
		ElementFormula fragmentFormula = ElementFormula.fromString("C7H14NOSi");
		ElementFormula elementFormula = new ElementFormula();
		elementFormula.put(Element.C, fragmentFormula.get(Element.C));
		elementFormula.put(Element.N, fragmentFormula.get(Element.N));
		IncorporationMap correctedMap = incorporationMap.correctForNaturalAbundance(elementFormula);
		LOGGER.info("correctedMap " + correctedMap.asTable());
		LOGGER.info("correctedMap normalized " + correctedMap.normalize(4).asTable());
		IncorporationMap normalizedCorrectedMap = correctedMap.normalize(4);
//		assertEquals(1.0, normalizedCorrectedMap.get(4, 0));
//		assertEquals(0.0, normalizedCorrectedMap.get(4, 1));
//		assertEquals(0.0, normalizedCorrectedMap.get(5, 0));
	}

}
