package de.kempalab.msdps.fileconversion;

public enum MZminePeakOutputHeader {
	EXACT_MASS("row m/z", 0),//
	RETENTION_TIME("row retention time", 1),//
	SEPARATED_DETAILS("row identity (main ID + details)", 2),//
	PEAK_HEIGHT(" Peak height", 3),// the header has actually a filename prefix
	PEAK_AREA(" Peak area", 4);// the header has actually a filename prefix

	private String headerValue;
	private int columnNumber;

	private MZminePeakOutputHeader(String headerValue, int columnNumber) {
		this.headerValue = headerValue;
		this.columnNumber = columnNumber;
	}

	public String getHeaderValue() {
		return headerValue;
	}

	public int getColumnNumber() {
		return columnNumber;
	}   
}

