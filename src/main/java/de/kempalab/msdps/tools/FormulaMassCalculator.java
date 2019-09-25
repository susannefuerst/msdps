package de.kempalab.msdps.tools;

import de.kempalab.msdps.ElementFormula;
import de.kempalab.msdps.IsotopeFormula;
import de.kempalab.msdps.constants.NaturalConstants;

public class FormulaMassCalculator {

	public static final String[] FORMULAS = { "C10H25N2OSi2" };
	public static final String[] ISOTOPE_FORMULAS = { "(12C)10(1H)25(15N)2(16O)(29Si)2" };
	final static int CHARGE = 1;

	public static void main(String[] args) {
		for (String formula : FORMULAS) {
			ElementFormula elementFormula = ElementFormula.fromString(formula);
			Double mass = elementFormula.calculateMass() - CHARGE * NaturalConstants.ELECTRON_MASS.getValue();
			System.out.println(formula + ": " + mass);
		}

		for (String formula : ISOTOPE_FORMULAS) {
			IsotopeFormula isotopeFormula = IsotopeFormula.fromSimpleString(formula);
			Double mass = isotopeFormula.calculateMass(CHARGE);
			System.out.println(formula + ": " + mass);
		}

	}

}
