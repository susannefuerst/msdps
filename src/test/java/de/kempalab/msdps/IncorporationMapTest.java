package de.kempalab.msdps;

import de.kempalab.msdps.constants.Element;
import de.kempalab.msdps.constants.FragmentKey;
import de.kempalab.msdps.constants.IncorporationType;
import de.kempalab.msdps.constants.IntensityType;
import de.kempalab.msdps.constants.Isotope;
import de.kempalab.msdps.exception.FragmentNotFoundException;
import de.kempalab.msdps.exception.TypeMismatchException;
import de.kempalab.msdps.log.MyLogger;
import de.kempalab.msdps.simulation.IsotopePatternSimulator;
import de.kempalab.msdps.util.MathUtils;
import junit.framework.TestCase;

public class IncorporationMapTest extends TestCase {
	
	public static final MyLogger LOG = MyLogger.getLogger(IncorporationMapTest.class);
	
	public void testConstructor() throws TypeMismatchException, FragmentNotFoundException {
		final double INC_CN = 0.1;
		final double INC_C = 0.5;
		final double INC_N = 0.2;
		final double NUMBER_OF_FRAGMENTS = 100000.0;
		final Integer PRECISION = 4;
		final double MIN_FREQUENCY = 0.003;
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
		
		
		naturalSpectrum = IsotopePatternSimulator.prepareSpectrum(naturalSpectrum, PRECISION, PRECISION, MIN_FREQUENCY,
				IntensityType.MID);
		markedSpectrumCN = IsotopePatternSimulator.prepareSpectrum(markedSpectrumCN, PRECISION, PRECISION,
				MIN_FREQUENCY, IntensityType.MID);
		markedSpectrumC = IsotopePatternSimulator.prepareSpectrum(markedSpectrumC, PRECISION, PRECISION, MIN_FREQUENCY,
				IntensityType.MID);
		markedSpectrumN = IsotopePatternSimulator.prepareSpectrum(markedSpectrumN, PRECISION, PRECISION, MIN_FREQUENCY,
				IntensityType.MID);
		mixedSpectrum = IsotopePatternSimulator.prepareSpectrum(mixedSpectrum, PRECISION, PRECISION, MIN_FREQUENCY,
				IntensityType.MID);
		
		MSShiftDatabase msShiftDatabase = new MSShiftDatabase();
		msShiftDatabase.setIncorporatedTracers("CN,C,N");
		msShiftDatabase.setIncorporationRate(INC);
		msShiftDatabase.setFragmentKey(fragmentCN.getFragmentKey());
		msShiftDatabase.setNaturalSpectrum(naturalSpectrum);
		msShiftDatabase.setMarkedSpectrum(markedSpectrumCN);
		msShiftDatabase.setMixedSpectrum(mixedSpectrum);
		msShiftDatabase.setFragmentFormula(fragmentCN.getFormula().toSimpleString());
		msShiftDatabase.analyseAllShifts();
		
		LOG.info(msShiftDatabase);
		LOG.horizontalLine();
		IncorporationMap incorporationMap = new IncorporationMap(
				msShiftDatabase.getMixedSpectrum(), msShiftDatabase.getMixedMassShifts(), new IsotopeList(Isotope.C_13, Isotope.N_15));
		LOG.info(incorporationMap.asTable());
	}
	
	public void testCorrection() {
		IncorporationMap incorporationMap = new IncorporationMap();
		IsotopeFormula _00 = new IsotopeFormula();
		_00.put(Isotope.C_13, 0);
		_00.put(Isotope.N_15, 0);
		incorporationMap.put(_00, 2736425697.0);
		
		IsotopeFormula _01 = new IsotopeFormula();
		_01.put(Isotope.C_13, 0);
		_01.put(Isotope.N_15, 1);
		incorporationMap.put(_01, 11895695.5);
		
		IsotopeFormula _10 = new IsotopeFormula();
		_10.put(Isotope.C_13, 1);
		_10.put(Isotope.N_15, 0);
		incorporationMap.put(_10, 210763094.5);
		
		IsotopeFormula _20 = new IsotopeFormula();
		_20.put(Isotope.C_13, 2);
		_20.put(Isotope.N_15, 0);
		incorporationMap.put(_20, 4886826.5);
		
		LOG.info(incorporationMap.asTable());
		
		ElementFormula maxElementsFormular = new ElementFormula();
		maxElementsFormular.put(Element.C, 7);
		maxElementsFormular.put(Element.N, 1);
		
		incorporationMap = incorporationMap.correctForNaturalAbundance(maxElementsFormular);
		
		LOG.info(incorporationMap.asTable());

	}
	
	public void testGetValueByKey() {
		IncorporationMap incorporationMap = new IncorporationMap();
		IsotopeFormula formula = new IsotopeFormula();
		formula.put(Isotope.C_13, 2);
		IsotopeFormula formula2 = new IsotopeFormula();
		formula2.put(Isotope.C_13, 2);
		incorporationMap.put(formula, 1.0);
		assertTrue(formula.equals(formula2));
		assertEquals(1.0, incorporationMap.get(formula2));
	}

	public void testAdditionalGetterMethod() {
		IncorporationMap incorporationMap = new IncorporationMap();
		IsotopeFormula _00 = new IsotopeFormula();
		_00.put(Isotope.C_13, 0);
		_00.put(Isotope.N_15, 0);
		incorporationMap.put(_00, 2736425697.0);

		IsotopeFormula _01 = new IsotopeFormula();
		_01.put(Isotope.C_13, 0);
		_01.put(Isotope.N_15, 1);
		incorporationMap.put(_01, 11895695.5);

		IsotopeFormula _10 = new IsotopeFormula();
		_10.put(Isotope.C_13, 1);
		_10.put(Isotope.N_15, 0);
		incorporationMap.put(_10, 210763094.5);

		IsotopeFormula _20 = new IsotopeFormula();
		_20.put(Isotope.C_13, 2);
		_20.put(Isotope.N_15, 0);
		incorporationMap.put(_20, 4886826.5);
		assertEquals(incorporationMap.get(0, 0), 2736425697.0);
		assertEquals(incorporationMap.get(0, 1), 11895695.5);
		assertEquals(incorporationMap.get(1, 0), 210763094.5);
		assertEquals(incorporationMap.get(2, 0), 4886826.5);
	}

}
