package de.kempalab.msdps.constants;

import java.util.InputMismatchException;

import junit.framework.TestCase;
import de.kempalab.msdps.IsotopeList;
import de.kempalab.msdps.constants.Isotope;

public class IsotopeTest extends TestCase {
	
	public void testByName() {
		assertEquals(Isotope.H_1, Isotope.byName("H_1"));
		assertEquals(Isotope.H_2, Isotope.byName("H_2"));
		assertEquals(Isotope.UNDEFINED, Isotope.byName("UNDEFINED"));
		assertEquals(Isotope.C_13, Isotope.byName("C_13"));
	}
	
	public void testFailByName() {
		try {
			assertEquals(Isotope.UNDEFINED, Isotope.byName("C13"));		
			fail("This should shrow an InputMismatchException");
		} catch (InputMismatchException e) {
			// this exception was expected
		}
	}
	
	public void testApproximatelyByMassShift() {
		IsotopeList expectedIsotopes = new IsotopeList(
				Isotope.C_13,
				Isotope.C_13,
				Isotope.N_15,
				Isotope.Si_29,
				Isotope.Si_30,
				Isotope.UNDEFINED);
		IsotopeList actualList = new IsotopeList(
				Isotope.approximatelyByMassShiftValue(1.0033),
				Isotope.approximatelyByMassShiftValue(1.0034),
				Isotope.approximatelyByMassShiftValue(0.9970),
				Isotope.approximatelyByMassShiftValue(0.9996),
				Isotope.approximatelyByMassShiftValue(2.0),
				Isotope.approximatelyByMassShiftValue(0.5));
		assertEquals(expectedIsotopes, actualList);
	}

}
