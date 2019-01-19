package de.kempalab.msdps.calculation;

import de.kempalab.msdps.FragmentList;
import de.kempalab.msdps.constants.Element;
import de.kempalab.msdps.constants.IntensityType;
import de.kempalab.msdps.data.IncorporationRate;

public class IsotopePatternCalculatorRequest {
	
	private Double minimalIntensity = 0.1;
	private Integer roundedMassPrecision = 4;
	private Integer roundedIntensityPrecision = 4;
	private FragmentList fragments;
	private Boolean analyseComposition = false;
	private IntensityType targetIntensityType = IntensityType.RELATIVE;
	private int charge = 1;
	/*
	 * only for independent tracer incorporation
	 */
	private Element tracer1;
	private Element tracer2;
	private IncorporationRate tracer1Inc;
	private IncorporationRate tracer2Inc;
	private IncorporationRate tracerAllInc;

	public Double getMinimalIntensity() {
		return minimalIntensity;
	}
	public void setMinimalIntensity(Double minimalIntensity) {
		this.minimalIntensity = minimalIntensity;
	}
	public Integer getRoundedMassPrecision() {
		return roundedMassPrecision;
	}
	public void setRoundedMassPrecision(Integer roundedMassPrecision) {
		this.roundedMassPrecision = roundedMassPrecision;
	}
	public Integer getRoundedIntensityPrecision() {
		return roundedIntensityPrecision;
	}
	public void setRoundedIntensityPrecision(Integer roundedIntensityPrecision) {
		this.roundedIntensityPrecision = roundedIntensityPrecision;
	}
	public FragmentList getFragments() {
		return fragments;
	}
	public void setFragments(FragmentList fragments) {
		this.fragments = fragments;
	}
	public Boolean getAnalyseComposition() {
		return analyseComposition;
	}
	public void setAnalyseComposition(Boolean analyseComposition) {
		this.analyseComposition = analyseComposition;
	}
	public IntensityType getTargetIntensityType() {
		return targetIntensityType;
	}
	public void setTargetIntensityType(IntensityType targetIntensityType) {
		this.targetIntensityType = targetIntensityType;
	}
	public int getCharge() {
		return charge;
	}
	public void setCharge(int charge) {
		this.charge = charge;
	}
	public Element getTracer1() {
		return tracer1;
	}
	public void setTracer1(Element tracer1) {
		this.tracer1 = tracer1;
	}
	public Element getTracer2() {
		return tracer2;
	}
	public void setTracer2(Element tracer2) {
		this.tracer2 = tracer2;
	}
	public IncorporationRate getTracer1Inc() {
		return tracer1Inc;
	}
	public void setTracer1Inc(IncorporationRate tracer1Inc) {
		this.tracer1Inc = tracer1Inc;
	}
	public IncorporationRate getTracer2Inc() {
		return tracer2Inc;
	}
	public void setTracer2Inc(IncorporationRate tracer2Inc) {
		this.tracer2Inc = tracer2Inc;
	}
	public IncorporationRate getTracerAllInc() {
		return tracerAllInc;
	}
	public void setTracerAllInc(IncorporationRate tracerAllInc) {
		this.tracerAllInc = tracerAllInc;
	}

}
