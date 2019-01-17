package de.kempalab.msdps.tools;

import de.kempalab.msdps.IsotopeFormula;
import de.kempalab.msdps.constants.Isotope;

public class AbundanceCalculator {

	public static void main(String[] args) {
		IsotopeFormula monoisotopic = new IsotopeFormula();
		monoisotopic.put(Isotope.C_12, 7);
		monoisotopic.put(Isotope.H_1, 14);
		monoisotopic.put(Isotope.N_14, 1);
		monoisotopic.put(Isotope.O_16, 1);
		monoisotopic.put(Isotope.Si_28, 1);

		IsotopeFormula isotopologue = new IsotopeFormula();
		isotopologue.put(Isotope.C_12, 6);
		isotopologue.put(Isotope.C_13, 1);
		isotopologue.put(Isotope.H_1, 14);
		isotopologue.put(Isotope.N_14, 1);
		isotopologue.put(Isotope.O_16, 1);
		isotopologue.put(Isotope.Si_28, 1);

		Double abundance = isotopologue.calculateAbundanceForOneIsotopologue();
		Double monoAbundance = monoisotopic.calculateAbundanceForOneIsotopologue();

		System.out.println("Monoistopic " + isotopologue.toSimpleString() + ": " + monoisotopic.calculateMass(1) + "\t"
				+ monoAbundance);
		System.out.println("Single isotopologue " + isotopologue.toSimpleString() + ": " + isotopologue.calculateMass(1)
				+ "\t" + abundance);
		System.out.println("Total isotopologues " + isotopologue.toSimpleString() + ": " + isotopologue.calculateMass(1)
				+ "\t" + abundance * 7);
		System.out.println("Relative to monoistopic: " + isotopologue.toSimpleString() + ": "
				+ isotopologue.calculateMass(1) + "\t" + abundance * 7 / monoAbundance);

		IsotopeFormula isotopologueN = new IsotopeFormula();
		isotopologueN.put(Isotope.C_12, 7);
		isotopologueN.put(Isotope.H_1, 14);
		isotopologueN.put(Isotope.N_15, 1);
		isotopologueN.put(Isotope.O_16, 1);
		isotopologueN.put(Isotope.Si_28, 1);

		Double abundanceN = isotopologueN.calculateAbundanceForOneIsotopologue();

		System.out.println("Single N isotopologue " + isotopologueN.toSimpleString() + ": "
				+ isotopologueN.calculateMass(1) + "\t" + abundanceN);
		System.out.println("Relative to monoistopic: " + isotopologueN.toSimpleString() + ": "
				+ isotopologueN.calculateMass(1) + "\t" + abundanceN / monoAbundance);

	}

}
