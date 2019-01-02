package de.kempalab.msdps.constants;

public enum NaturalConstants {

	ELECTRON_MASS(0.00054857, "u");

	private double value;
	private String unit;

	private NaturalConstants(double value, String unit) {
		this.value = value;
		this.unit = unit;
	}

	public double getValue() {
		return value;
	}

	public String getUnit() {
		return unit;
	}

}
