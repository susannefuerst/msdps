package de.kempalab.msdps.simulation;

import de.kempalab.msdps.FragmentList;
import de.kempalab.msdps.constants.Element;
import de.kempalab.msdps.constants.IntensityType;
import de.kempalab.msdps.data.IncorporationRate;

/**
 * A request class to specify simulation options. A general
 * {@link IsotopePatternSimulatorRequest} uses the following standards: <br>
 * incorporation rate: 0.1 <br>
 * total number of fragments: 100000 <br>
 * minimal relative mass intensities: 0.003 <br>
 * mass precision: 4 <br>
 * mass intensity precision: 4 <br>
 * analyze mass shifts: false <br>
 * 
 * @author sfuerst
 *
 */
public class IsotopePatternSimulatorRequest {
	
	private IncorporationRate incorporationRate = new IncorporationRate(0.1);
	private Double totalNumberOfFragments = 100000.0;
	private Double minimalIntensity = 0.1;
	private Integer roundedMassPrecision = 4;
	private Integer roundedIntensityPrecision = 4;
	private FragmentList fragments;
	private Boolean analyzeMassShifts = false;
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
	
	/**
	 * Creates a general {@link IsotopePatternSimulatorRequest} using the following
	 * standards: <br>
	 * incorporation rate: 1 <br>
	 * total number of fragments: 100000 <br>
	 * minimal relative mass intensities: 0.003 <br>
	 * mass precision: 4 <br>
	 * intensity precision: 4 <br>
	 * analyze mass shifts: false <br>
	 * intensity type: RELATIVE <br>
	 */
	public IsotopePatternSimulatorRequest()  {
		
	}
	
	/**
	 * Creates a {@link IsotopePatternSimulatorRequest} using the specified
	 * parameters. If a parameter is null the following standards will be used: <br>
	 * incorporation rate: 1 <br>
	 * total number of fragments: 100000 <br>
	 * minimal relative mass intensities: 0.003 <br>
	 * mass precision: 4 <br>
	 * mass intensity precision: 4 <br>
	 * analyze mass shifts: false <br>
	 * target intensity type: RELATIVE <br>
	 * <br>
	 * 
	 * @param incorporationRate
	 * @param totalNumberOfFragments
	 * @param minimalRelativeIntensity
	 * @param roundedMassPrecision
	 * @param roundedIntensitiesPrecision
	 * @param fragments
	 * @param analyzeMassShifts
	 */
	public IsotopePatternSimulatorRequest(IncorporationRate incorporationRate, Double totalNumberOfFragments,
			IntensityType targetIntensityType, Double minimalRelativeIntensity, Integer roundedMassPrecision,
			Integer roundedIntensityPrecision,
			FragmentList fragments, Boolean analyzeMassShifts) {
		if (incorporationRate != null) {
			this.incorporationRate = incorporationRate;
		}
		if (totalNumberOfFragments != null) {
			this.totalNumberOfFragments = totalNumberOfFragments;
		}
		if (minimalRelativeIntensity != null) {
			this.minimalIntensity = minimalRelativeIntensity;
		}
		if (roundedMassPrecision != null) {
			this.roundedMassPrecision = roundedMassPrecision;
		}
		if (roundedIntensityPrecision != null) {
			this.roundedIntensityPrecision = roundedIntensityPrecision;
		}
		if (analyzeMassShifts != null) {
			this.setAnalyzeMassShifts(analyzeMassShifts);
		}
		if (targetIntensityType != null) {
			this.targetIntensityType = targetIntensityType;
		}
		this.fragments = fragments;
	}

	public IncorporationRate getIncorporationRate() {
		return incorporationRate;
	}

	public void setIncorporationRate(IncorporationRate incorporationRate) {
		this.incorporationRate = incorporationRate;
	}

	public Double getTotalNumberOfFragments() {
		return totalNumberOfFragments;
	}

	public void setTotalNumberOfFragments(Double totalNumberOfFragments) {
		this.totalNumberOfFragments = totalNumberOfFragments;
	}

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

	/**
	 * @return the analyzeMassShifts
	 */
	public Boolean getAnalyzeMassShifts() {
		return analyzeMassShifts;
	}

	/**
	 * @param analyzeMassShifts the analyzeMassShifts to set
	 */
	public void setAnalyzeMassShifts(Boolean analyzeMassShifts) {
		this.analyzeMassShifts = analyzeMassShifts;
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
