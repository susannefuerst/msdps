package de.kempalab.msdps;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

@SuppressWarnings("serial")
/**
 * If we have an isotope pattern P represented by peaks p_0,..., p_n and corresponding masses m_0,..., m_n, then
 * the key of the i-th entry of a MassShiftDataSet represents the sequence of MassShifts,
 * needed to step from peak p_0 to peak p_i and the value of this entry represent the list of Isotopes that induce these
 * sequence of MassShifts.
 * @author sfuerst
 *
 */
public class MassShiftDataSet extends LinkedHashMap<MassShiftList,IsotopeListList> {
	
	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (!(other instanceof MassShiftDataSet)) {
			return false;
		}
		MassShiftDataSet otherSet = ((MassShiftDataSet) other);
		for (Entry<MassShiftList, IsotopeListList> otherEntry : otherSet.entrySet()) {
			boolean foundMatchingEntry = false;
			MassShiftList otherKey = otherEntry.getKey();
			IsotopeListList otherValue = otherEntry.getValue();
			for (Entry<MassShiftList, IsotopeListList> thisEntry : this.entrySet()) {
				MassShiftList thisKey = thisEntry.getKey();
				IsotopeListList thisValue = thisEntry.getValue();
				if (otherKey.equals(thisKey) && otherValue.equals(thisValue)) {
					foundMatchingEntry = true;
				}
			}
			if (!foundMatchingEntry) {
				return false;
			}
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public boolean equalsUpToPermutationOfIsotopes(MassShiftDataSet other) {
		if (other == null) {
			return false;
		}
		Object[] thisEntries = this.entrySet().toArray();
		Object[] otherEntries = this.entrySet().toArray();
		if (this.size() != other.size()) {
			return false;
		}
		for (int thisIndex = 0; thisIndex < thisEntries.length; thisIndex++) {
			MassShiftList thisMassShiftList = ((Entry<MassShiftList, IsotopeList>)thisEntries[thisIndex]).getKey();
			MassShiftList otherMassShiftList = ((Entry<MassShiftList, IsotopeList>)otherEntries[thisIndex]).getKey();
			if (!thisMassShiftList.equalsUpToPermutationOfIsotopes(otherMassShiftList)) {
				return false;
			}
		}
		return true;
	}
	
	public List<Entry<MassShiftList, IsotopeListList>> toEntryList() {
		List<Entry<MassShiftList, IsotopeListList>> entryList = new ArrayList<>(this.entrySet());
		return entryList;
	}
}
