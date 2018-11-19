package de.kempalab.msdps;

import junit.framework.TestCase;
import de.kempalab.msdps.IsotopeList;
import de.kempalab.msdps.constants.Isotope;
import de.kempalab.msdps.log.MyLogger;

public class IsotopeListTest extends TestCase {
	public static final MyLogger LOGGER = MyLogger.getLogger(IsotopeListTest.class);
	
	public void testConstructor() {
		IsotopeList list = new IsotopeList(Isotope.C_12, Isotope.C_13, Isotope.C_12);
		assertEquals(3, list.size());
		assertEquals(Isotope.C_12, list.get(0));
		assertEquals(Isotope.C_13, list.get(1));
		assertEquals(Isotope.C_12, list.get(2));
	}
	
	public void testToString() {
		IsotopeList list = new IsotopeList(Isotope.C_12, Isotope.C_13, Isotope.C_12);
		assertEquals("C_12|C_13|C_12", list.toString());
	}
	
	public void testFromString() {
		IsotopeList expected = new IsotopeList(Isotope.C_12, Isotope.C_13, Isotope.C_12);
		IsotopeList actual = IsotopeList.fromString("C_12|C_13|C_12");
		assertEquals(expected, actual);
	}
	
	public void testToVerticalCountString() {
		IsotopeList list = new IsotopeList(Isotope.C_12, Isotope.C_12, Isotope.C_12, Isotope.N_15, Isotope.H_1);
		assertTrue(list.toVerticalCountString().contains("C_12: 3"));
		assertTrue(list.toVerticalCountString().contains("H_1: 1"));
		assertTrue(list.toVerticalCountString().contains("N_15: 1"));
		LOGGER.info("\n" + list.toVerticalCountString());
	}
	
	public void testToCommaSeparatedCountString() {
		IsotopeList list = new IsotopeList(Isotope.C_12, Isotope.C_13, Isotope.C_12, Isotope.N_15, Isotope.H_1);
		assertEquals("C_12: 2, C_13: 1, N_15: 1, H_1: 1", list.toCommaSeparatedCountString());
		LOGGER.info(list.toCommaSeparatedCountString());
	}

}
