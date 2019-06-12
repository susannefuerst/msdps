package de.kempalab.msdps.fileconversion;

public enum NACorrectionInputHeader {
	GROPUP_KEY("GroupKey", 0),//
	INTENSITY("Intensity", 1),//
	C_13_COUNT("C13", 2),//
	N_15_COUNT("N15", 3),//
	FORMULA("Formula",4);//

	private String headerValue;
	private int columnValue;

	private NACorrectionInputHeader(String headerValue, int columnValue) {
		this.headerValue = headerValue;
		this.columnValue = columnValue;
	}

	public String getHeaderValue() {
		return headerValue;
	}

	public int getColumnValue() {
		return columnValue;
	}
}