package de.kempalab.msdps.tools;

import de.kempalab.msdps.ElementFormula;
import de.kempalab.msdps.IsotopeFormula;
import de.kempalab.msdps.constants.NaturalConstants;

public class FormulaMassCalculator {

	public static final String[] FORMULAS = { "C9H19O2N2Si2" };
	public static final String[] ISOTOPE_FORMULAS = { "(12C)2(13C)2(1H)10(15N)1(16O)2(28Si)1",
			"(12C)5(13C)4(1H)19(15N)2(16O)2(28Si)2" };
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
