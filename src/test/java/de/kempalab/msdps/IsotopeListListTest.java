package de.kempalab.msdps;

import junit.framework.TestCase;
import de.kempalab.msdps.IsotopeList;
import de.kempalab.msdps.IsotopeListList;
import de.kempalab.msdps.constants.Isotope;
import de.kempalab.msdps.log.MyLogger;

public class IsotopeListListTest extends TestCase {
	
	public static final MyLogger LOG = MyLogger.getLogger(IsotopeListListTest.class);
	
	public void testToString() {
		IsotopeListList isotopeListList = new IsotopeListList(
				new IsotopeList(Isotope.B_10, Isotope.B_11),
				new IsotopeList(Isotope.C_12, Isotope.C_13));
		LOG.infoValue("isotopeListList", isotopeListList);
		assertEquals("[B_10|B_11][C_12|C_13]", isotopeListList.toString());
	}
	
	public void testFromString() {
		IsotopeListList expectedList = new IsotopeListList(
				new IsotopeList(Isotope.B_10, Isotope.B_11),
				new IsotopeList(Isotope.C_12, Isotope.C_13));
		LOG.infoValue("actualList", IsotopeListList.fromString("[B_10|B_11][C_12|C_13]"));
		assertEquals(expectedList, IsotopeListList.fromString("[B_10|B_11][C_12|C_13]"));
	}
	
	public void testToCommaSeparatedCountString() {
		IsotopeListList isotopeListList = new IsotopeListList(
				new IsotopeList(Isotope.B_10, Isotope.B_11, Isotope.C_13),
				new IsotopeList(Isotope.C_12, Isotope.C_13, Isotope.B_10));
		LOG.infoValue("actualCountString", isotopeListList.toCommaSeparatedCountString());
		assertEquals("B_10: 2, B_11: 1, C_13: 2, C_12: 1", isotopeListList.toCommaSeparatedCountString());
	}

}
