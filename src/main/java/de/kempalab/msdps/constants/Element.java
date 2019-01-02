package de.kempalab.msdps.constants;

import java.util.ArrayList;

import de.kempalab.msdps.IsotopeList;
import de.kempalab.msdps.MassSpectrum;
import de.kempalab.msdps.data.Partition;
import de.kempalab.msdps.data.PermutationSet;
import de.kempalab.msdps.log.MyLogger;
import de.kempalab.msdps.util.MathUtils;

/**
 * An enumeration of chemical elemnts, given by their name, atomicNumber and relativeAtomicMass.
 * @author sfuerst
 *
 */
public enum Element {
	
	H("Hydrogen", 1, 1.0079),
	B("Boron", 5, 10.811),
	C("Carbon", 6, 12.011),
	N("Nitrogen", 7, 14.0067),
	O("Oxygen", 8, 15.9994),
	F("Fluorine", 9, 18.9984),
	Si("Silicon", 14, 28.0855),
	Na("Sodium", 11, 22.9898),
	P("Phosphorus", 15, 30.9738),
	S("Sulfur", 16, 32.066),
	Cl("Chlorine", 17, 35.4527),
	K("Potassium", 19, 39.0983),
	I("Iodine", 53, 126.9044),
	UNDEFINED("Undefined", 0, 0),
	NONE("None", 0, 0);
	
	public final static MyLogger LOG = MyLogger.getLogger(Element.class);
	private String name;
	private int atomicNumber;
	private double relativeAtomicMass;
	
	private Element(String name, int atomicNumber, double relativeAtomicMass) {
		this.name = name;
		this.atomicNumber = atomicNumber;
		this.relativeAtomicMass = relativeAtomicMass;
	}

	public String getName() {
		return name;
	}

	public int getAtomicNumber() {
		return atomicNumber;
	}

	public double getRelativeAtomicMass() {
		return relativeAtomicMass;
	}
	
	
	public IsotopeList getIsotopes() {
		IsotopeList isotopes = new IsotopeList();
		for (Isotope isotope : Isotope.values()) {
			if (isotope.getElement().equals(this)) {
				isotopes.add(isotope);
			}
		}
		return isotopes;
	}
	
	public double lowestMass() {
		return getIsotopes().get(0).getAtomicMass();
	}
	
	public double highestMass() {
		return heaviestIsotope().getAtomicMass();
	}
	
	public Isotope heaviestIsotope() {
		int index = getIsotopes().size() - 1;
		return getIsotopes().get(index);
	}
	
	/**
	 * use a combinatorial approach to determine the masses of all isotopologues of a molecule E_n,
	 * where E is some element and n a natural number.
	 * @param numberOfElements
	 * @param treshFaktor factor to determine which low abundance masses shall be excluded.
	 * @return mass spectrum/isotope pattern of E_n
	 */
	public MassSpectrum multiElementSpectrum(int numberOfElements, Double treshFaktor) {
		MassSpectrum spectrum = new MassSpectrum(FrequencyType.MID);
		IsotopeList isotopes = this.getIsotopes();
//		isotopes.sort(new IsotopeAbundancyComparator());
		ArrayList<PermutationSet> permutationSets = PermutationSet.allIsotopeCombinations(isotopes.size(), numberOfElements);
		ArrayList<Partition> allCombinations = new ArrayList<>();
		for (PermutationSet set : permutationSets) {
			allCombinations.addAll(set);
		}
		ArrayList<Double> massAbundancies = MathUtils.calculateAbundancies(allCombinations, isotopes,
				numberOfElements, treshFaktor);
		ArrayList<Double> masses = new ArrayList<>();
		for (Partition partition : allCombinations) {
			Double mass = 0.0;
			for (int index = 0; index < partition.size(); index++) {
				LOG.debugValue("summand", partition.get(index));
				Isotope isotope = isotopes.get(index);
				LOG.debugValue("isotope", isotope);
				LOG.debugValue("atomicMass", isotope.getAtomicMass());
				mass = mass + partition.get(index) * isotope.getAtomicMass();
				
			}
			masses.add(mass);
		}
		for (Double mass : masses) {
			Double abundance = massAbundancies.get(masses.indexOf(mass));
			if (spectrum.get(mass) != null) {
				spectrum.put(mass, spectrum.get(mass) + abundance);
			} else {
				spectrum.put(mass, abundance);
			}
		}
		return spectrum;
	}
	
	/**
	 * 
	 * @return The tracer that refers to this element.
	 */
	public Isotope getTracer() {
		IsotopeList isotopes = this.getIsotopes();
		for (Isotope isotope : isotopes) {
			if (isotope.isTracer()) {
				return isotope;
			}
		}
		return Isotope.NONE;
	}
	
	/**
	 * 
	 * @return the most common isotope
	 */
	public Isotope mostCommonIsotope() {
		IsotopeList isotopeList = this.getIsotopes();
		Isotope mostCommonIsotope = isotopeList.get(0);
		for (Isotope isotope : isotopeList) {
			if (isotope.getAbundance() > mostCommonIsotope.getAbundance()) {
				mostCommonIsotope = isotope;
			}
		}
		return mostCommonIsotope;
	}
}
