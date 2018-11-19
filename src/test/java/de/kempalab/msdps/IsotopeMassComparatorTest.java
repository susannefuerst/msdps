package de.kempalab.msdps;

import junit.framework.TestCase;
import de.kempalab.msdps.IsotopeMassComparator;
import de.kempalab.msdps.constants.Isotope;

public class IsotopeMassComparatorTest extends TestCase {
	
	public void testCompare() {
		assertEquals(-1, new IsotopeMassComparator().compare(Isotope.H_1, Isotope.H_2));
		assertEquals(0, new IsotopeMassComparator().compare(Isotope.C_12, Isotope.C_12));
		assertEquals(1, new IsotopeMassComparator().compare(Isotope.N_15, Isotope.N_14));
	}

}
