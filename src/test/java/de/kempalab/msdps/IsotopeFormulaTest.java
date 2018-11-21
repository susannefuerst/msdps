package de.kempalab.msdps;

import de.kempalab.msdps.constants.Element;
import de.kempalab.msdps.constants.Isotope;
import de.kempalab.msdps.log.MyLogger;
import junit.framework.TestCase;

public class IsotopeFormulaTest extends TestCase {
	public static final MyLogger LOGGER = MyLogger.getLogger(IsotopeFormulaTest.class);
	
	public void testToElementFormula() {
		IsotopeFormula isotopeFormula = new IsotopeFormula();
		isotopeFormula.put(Isotope.C_12, 2);
		isotopeFormula.put(Isotope.C_13, 3);
		isotopeFormula.put(Isotope.H_1, 2);
		isotopeFormula.put(Isotope.H_2, 5);
		isotopeFormula.put(Isotope.N_15, 2);
		ElementFormula elementFormula = new ElementFormula();
		elementFormula.put(Element.C, 5);
		elementFormula.put(Element.H, 7);
		elementFormula.put(Element.N, 2);
		LOGGER.infoValue("expected element Formula", elementFormula);
		LOGGER.infoValue("actual element Formula", isotopeFormula.toElementFormula());
		assertEquals(elementFormula, isotopeFormula.toElementFormula());
	}
	
	public void testToIsotopeList() {
		IsotopeFormula isotopeFormula = new IsotopeFormula();
		isotopeFormula.put(Isotope.C_12, 2);
		isotopeFormula.put(Isotope.C_13, 3);
		isotopeFormula.put(Isotope.H_1, 2);
		isotopeFormula.put(Isotope.H_2, 5);
		isotopeFormula.put(Isotope.N_15, 2);
		IsotopeList isotopeList = new IsotopeList();
		isotopeList.add(Isotope.C_12);
		isotopeList.add(Isotope.C_13);
		isotopeList.add(Isotope.H_1);
		isotopeList.add(Isotope.H_2);
		isotopeList.add(Isotope.N_15);
		LOGGER.infoValue("expected isotopeList", isotopeList);
		LOGGER.infoValue("actual isotopeList", isotopeFormula.toIsotopeList());
		assertEquals(isotopeList, isotopeFormula.toIsotopeList());
	}
	
	public void testToNiceFormattedFormula() {
		IsotopeFormula isotopeFormula = new IsotopeFormula();
		isotopeFormula.put(Isotope.C_12, 2);
		isotopeFormula.put(Isotope.C_13, 3);
		isotopeFormula.put(Isotope.H_1, 2);
		isotopeFormula.put(Isotope.H_2, 5);
		isotopeFormula.put(Isotope.N_15, 2);
		LOGGER.infoValue("actual niceFormula", isotopeFormula.toNiceFormattedFormula());
	}

}
