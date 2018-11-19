package de.kempalab.msdps.constants;

import java.util.ArrayList;
/**
 * All the headers in a simulated MassSpectra csv file with the corresponding columns
 * @author sfuerst
 *
 */
public enum MSDatabaseColKey {
	INC_RATE("IncRate", 0),
	NATURAL_MASS("NaturalMass", 1),
	NATURAL_FREQUENCY("NaturalFrequency", 2),
	MARKED_MASS("MarkedMass", 3),
	MARKED_FREQUENCY("MarkedFrequency", 4),
	MIXED_MASS("MixedMass", 5),
	MIXED_FREQUENCY("MixedFrequency", 6),
	FRAGMENT_KEY("FragmentKey", 7),
	INCORPORATED_TRACERS("IncorporatedTracers", 8),
	FRAGMENT_FORMULA("FragmentFormula", 9);
	
	private String header;
	private int columnIndex;

	private MSDatabaseColKey(String header, int columnIndex) {
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
		MSDatabaseColKey[] keys = values();
		ArrayList<String> headers = new ArrayList<>();
		for (int index = 0; index < keys.length; index++) {
			headers.add(keys[index].header);
		}
		return headers;
	}

}
