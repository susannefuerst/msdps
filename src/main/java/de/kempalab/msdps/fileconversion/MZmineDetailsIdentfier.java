package de.kempalab.msdps.fileconversion;

public enum MZmineDetailsIdentfier {
	ID("ID: ", 0),//
	NAME("Name ", 1),//
	FORMULA("Molecular formula: ", 2);//

	private String identifier;
	private int index;
	private MZmineDetailsIdentfier(String identifier, int index) {
		this.identifier = identifier;
		this.index = index;
	}
	public String getIdentifier() {
		return identifier;
	}
	public int getIndex() {
		return index;
	}

}
