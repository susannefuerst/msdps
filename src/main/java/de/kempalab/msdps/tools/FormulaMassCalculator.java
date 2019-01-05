package de.kempalab.msdps.tools;

import de.kempalab.msdps.ElementFormula;
import de.kempalab.msdps.constants.NaturalConstants;

public class FormulaMassCalculator {

	public static final String[] FORMULAS = { "C6HOSi", "CHO2Si" };
	final static int CHARGE = 0;

	public static void main(String[] args) {
		for (String formula : FORMULAS) {
			ElementFormula elementFormula = ElementFormula.fromString(formula);
			Double mass = elementFormula.calculateMass() - CHARGE * NaturalConstants.ELECTRON_MASS.getValue();
			System.out.println(formula + ": " + mass);
		}

	}

}
