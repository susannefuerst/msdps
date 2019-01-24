package de.kempalab.msdps;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

public class IsotopeComposition extends LinkedHashMap<Double, IsotopeFormula> {
	
	public IsotopeComposition sortDescendingByMass() {
		List<Entry<Double, IsotopeFormula>> entryList = new ArrayList<>(this.entrySet());
		entryList.sort(Entry.comparingByKey());
		IsotopeComposition sortedList = new IsotopeComposition();
		for (int index = entryList.size() - 1; index >= 0; index--) {
			Entry<Double, IsotopeFormula> entry = entryList.get(index);
	            sortedList.put(entry.getKey(), entry.getValue());
		}
        return sortedList;
	}
	
	public IsotopeComposition sortAscendingByMass() {
		List<Entry<Double, IsotopeFormula>> entryList = new ArrayList<>(this.entrySet());
		entryList.sort(Entry.comparingByKey());
		IsotopeComposition sortedList = new IsotopeComposition();
		for (Entry<Double, IsotopeFormula> entry : entryList) {
			sortedList.put(entry.getKey(), entry.getValue());
        }
        return sortedList;
	}

}
