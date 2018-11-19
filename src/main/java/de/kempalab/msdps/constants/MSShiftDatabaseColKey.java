package de.kempalab.msdps.constants;

import java.util.ArrayList;
/**
 * All the headers and the corresponding columnIndex in a simulated MassSpectra csv file
 * that includes the analyzed mass shifts.
 * @author sfuerst
 *
 */
public enum MSShiftDatabaseColKey {
	INC_RATE("IncRate", 0),
	NATURAL_MASS("NaturalMass", 1),
	NATURAL_FREQUENCY("NaturalFrequency", 2),
	NATURAL_SHIFT_VALUES("NaturalShiftValues", 3),
	NATURAL_SHIFT_ISOTOPES("NaturalShiftIsotopes", 4),
	MARKED_MASS("MarkedMass", 5),
	MARKED_FREQUENCY("MarkedFrequency", 6),
	MARKED_SHIFT_VALUES("MarkedShiftValues", 7),
	MARKED_SHIFT_ISOTOPES("MarkedShiftIsotopes", 8),
	MIXED_MASS("MixedMass", 9),
	MIXED_FREQUENCY("MixedFrequency", 10),
	MIXED_SHIFT_VALUES("MixedShiftValues", 11),
	MIXED_SHIFT_ISOTOPES("MixedShiftIsotopes", 12),
	FRAGMENT_KEY("FragmentKey", 13),
	INCORPORATED_TRACERS("IncorporatedTracers", 14),
	FRAGMENT_FORMULA("FragmentFormula", 15);
	
	private String header;
	private int columnIndex;

	private MSShiftDatabaseColKey(String header, int columnIndex) {
		this.header = header;
		this.columnIndex = columnIndex;
	}

	public String getHeader() {
		return header;
	}

	public int getColumnIndex() {
		return columnIndex;
	}
	
	/**
	 * 
	 * @return all the headers as ArrayList
	 */
	public static ArrayList<String> toHeaderList() {
		MSShiftDatabaseColKey[] keys = values();
		ArrayList<String> headers = new ArrayList<>();
		for (int index = 0; index < keys.length; index++) {
			headers.add(keys[index].header);
		}
		return headers;
	}
}
