package de.kempalab.msdps.constants;

public enum MetaboliteKey {
	
	LAC("Lactic acid", "Lac"),
	ASP("Aspartic acid", "Asp"),
	MAL("Malic acid", "Mal"),
	GLC("Glucose_MP / BP", "Glc"),
	ALA("Alanine", "Ala"),
	GLY("Glycine", "Gly"),
	SER("Serine", "Ser"),
	GLN("Glutamine", "Gln"),
	UNKNOWN("Unknown", "UNKNOWN"),
	;
	private String moleculeName;
	private String abbreviation;
	
	MetaboliteKey(String moleculeName, String abbreviation) {
		this.moleculeName = moleculeName;
		this.abbreviation = abbreviation;
	}

	public String getMoleculeName() {
		return moleculeName;
	}

	public String getAbbreviation() {
		return abbreviation;
	}
	
	public MetaboliteKey byName(String name) {
		for (MetaboliteKey key : values()) {
			if (key.name().equals(name)) {
				return key;
			}
		}
		return MetaboliteKey.UNKNOWN;
	}
	
	public MetaboliteKey byAbbreviation(String abbreviation) {
		for (MetaboliteKey key : values()) {
			if (key.getAbbreviation().equals(abbreviation)) {
				return key;
			}
		}
		return MetaboliteKey.UNKNOWN;
	}

}
