package de.kempalab.msdps;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import de.kempalab.msdps.constants.Element;
import de.kempalab.msdps.constants.Isotope;

/**
 * A map of each isotope in a molecule to its total number in the molecule.
 * @author sfuerst
 *
 */
@SuppressWarnings("serial")
public class IsotopeFormula extends LinkedHashMap<Isotope, Integer> {

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

}
