package de.kempalab.msdps.constants;

public enum MetaboliteKey {
	
	ALA("Alanine", "Ala"), //
	ASP("Aspartic acid", "Asp"), //
	CIT("Citric acid", "Cit"), //
	DHAP("Dihydroxyacetone phosphate", "DHAP"), //
	FRC("Fructose_MP", "Frc"), //
	FBP("Fructose-1_6-diphosphate", "FBP"), //
	F6P("Fructose-6-phosphate", "F6P"), //
	FUM("Fumaric acid", "Fum"), //
	PGA6("Gluconic acid-6-phosphate", "6PGA"), //
	GLC("Glucose_MP / BP", "Glc"), //
	G16P("Glucose-1/6-phosphate", "G1/6P"), //
	G6P("Glucose-6-phosphate", "G6P"), //
	GLU("Glutamic acid", "Glu"), //
	GLN("Glutamine", "Gln"), //
	HG2("Glutaric acid_ 2-hydroxy-", "2HG"), //
	AKG("Glutaric acid_ 2-oxo-", "aKG"), //
	GLT("Glutaric acid", "Glt"), //
	PGA3("Glyceric acid-3-phosphate", "3PGA"), //
	GLYC("Glycerol", "Glyc"), //
	GLYC3P("Glycerol-3-phosphate", "Glyc3P"), //
	GLY("Glycine", "Gly"), //
	LAC("Lactic acid", "Lac"), //
	MAL("Malic acid", "Mal"), //
	PEP("Phosphoenolpyruvic acid", "PEP"), //
	PYR("Pyruvic acid", "Pyr"), //
	R5P("Ribose-5-phosphate", "R5P"), //
	SER("Serine", "Ser"), //
	SUC("Succinic acid", "Suc"), //
	UNKNOWN("Unknown", "UNKNOWN"),//
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
