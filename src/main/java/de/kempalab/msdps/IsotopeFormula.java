package de.kempalab.msdps;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import de.kempalab.msdps.constants.Element;
import de.kempalab.msdps.constants.Isotope;
import de.kempalab.msdps.constants.NaturalConstants;
import de.kempalab.msdps.util.StringUtils;

/**
 * A map of each isotope in a molecule to its total number in the molecule.
 * @author sfuerst
 *
 */
@SuppressWarnings("serial")
public class IsotopeFormula extends LinkedHashMap<Isotope, Integer> {
	
	/**
	 * An {@link IsotopeFormula} with string representation {C_13=2, C_12=3} will be converted to an {@link ElementFormula} with string representation
	 * {C=5}
	 * @return An {@link ElementFormula} representation of this {@link IsotopeFormula}
	 */
	public ElementFormula toElementFormula() {
		ElementFormula elementFormula = new ElementFormula();
		for (Entry<Isotope, Integer> entry : this.entrySet()) {
			Element element = entry.getKey().getElement();
			Integer value = entry.getValue();
			if (elementFormula.get(element) == null) {
				elementFormula.put(element, value);
			} else {
				Integer oldValue = elementFormula.get(element);
				elementFormula.put(element, oldValue + value);
			}
		}
		return elementFormula;
	}
	
	public List<Entry<Isotope, Integer>> toEntryList() {
		List<Entry<Isotope, Integer>> entryList = new ArrayList<>(this.entrySet());
		return entryList;
	}
	
	/**
	 * Collects only the keys (isotopes) of this {@link IsotopeFormula} in an {@link IsotopeList}
	 * @return An {@link IsotopeList} that contains all the keys (isotopes) of this {@link IsotopeFormula}.
	 */
	public IsotopeList toIsotopeList() {
		IsotopeList isotopeList = new IsotopeList();
		for (Entry<Isotope, Integer> entry : this.entrySet()) {
			isotopeList.add(entry.getKey());
		}
		return isotopeList;
	}

	public boolean mattersForCorrectionOf(IsotopeFormula currentCorrectionIndex) {
		boolean allSameCounts = true;
		for (Entry<Isotope, Integer> thisEntry : this.entrySet()) {
			Isotope thisIsotope = thisEntry.getKey();
			Integer thisCount = thisEntry.getValue();
			if (thisCount > currentCorrectionIndex.get(thisIsotope)) {
				return false;
			}
			if (thisCount < currentCorrectionIndex.get(thisIsotope)) {
				allSameCounts = false;
			}
		}
		return !allSameCounts;
	}

	/**
	 * 
	 * @return a string representation using sub- and superscript.
	 */
	public String toNiceFormattedFormula() {
		StringBuffer buffer = new StringBuffer();
		for (Entry<Isotope, Integer> entry : this.entrySet()) {
			Integer countValue = entry.getValue();
			Isotope isotope = entry.getKey();
			if (countValue == 1) {
				buffer.append(isotope.toNiceFormattedString());
			} else {
				buffer.append("(" + isotope.toNiceFormattedString() + ")" + StringUtils.subscript(String.valueOf(countValue)));
			}
		}
		return buffer.toString();
	}

	/**
	 * 
	 * @return i.e (12C)2(13C)5(1H)15(2H)(15N)2
	 */
	public String toSimpleString() {
		StringBuffer buffer = new StringBuffer();
		for (Entry<Isotope, Integer> entry : this.entrySet()) {
			Integer countValue = entry.getValue();
			Isotope isotope = entry.getKey();
			if (countValue == 1) {
				buffer.append(isotope.toSimpleString());
			} else {
				buffer.append(isotope.toSimpleString() + countValue);
			}
		}
		return buffer.toString();
	}

	public Double calculateAbundanceForOneIsotopologue() {
		Double abundance = 1.0;
		for (Entry<Isotope, Integer> entry : this.entrySet()) {
			abundance = abundance * Math.pow(entry.getKey().getAbundance(), entry.getValue());
		}
		return abundance;
	}

	public Double calculateMass(int charge) {
		Double mass = 0.0;
		for (Entry<Isotope, Integer> entry : this.entrySet()) {
			mass = mass + entry.getKey().getAtomicMass() * entry.getValue();
		}
		return mass - charge * NaturalConstants.ELECTRON_MASS.getValue();
	}
	
	
	public IsotopeFormula getHeavyIsotopes() {
		IsotopeFormula heavyIsotopes = new IsotopeFormula();
		for (Entry<Isotope, Integer> entry : this.entrySet()) {
			if (!(entry.getKey().getElement().lightestIsotope().equals(entry.getKey()))) {
				heavyIsotopes.put(entry.getKey(), entry.getValue());
			}
		}
		return heavyIsotopes;
	}

}
