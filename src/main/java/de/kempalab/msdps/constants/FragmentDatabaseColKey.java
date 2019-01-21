package de.kempalab.msdps.constants;
/**
 * An enumeration of all the headers in the FragmentsDatabase.csv and the corresponding column.
 * @author sfuerst
 *
 */
public enum FragmentDatabaseColKey {
	FRAGMENT_FORMULA("fragmentFormula", 0), //
	MOLECULE_NAME("moleculeName", 1), //
	SHORT_MOLECULE_NAME("shortMoleculeName", 2), //
	FRAGMENT_CAPACITY("fragmentCapacity", 3), //
	FRAGMENT_BASE_INT_MASS("fragmentFormula", 4), //
	DERIVATE("derivate", 5), //
	RETENTION_TIME("retentionTime", 7);
	
	private String header;
	private int columnIndex;

	private FragmentDatabaseColKey(String header, int columnIndex) {
		this.header = header;
		this.columnIndex = columnIndex;
	}

	public String getHeader() {
		return header;
	}

	public int getColumnIndex() {
		return columnIndex;
	}
	
	
}
