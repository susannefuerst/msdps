package de.kempalab.msdps.simulation;

import de.kempalab.msdps.FragmentList;
import de.kempalab.msdps.constants.FrequencyType;
import de.kempalab.msdps.data.IncorporationRate;

/**
 * A request class to specify simulation options. A general {@link IsotopePatternSimulatorRequest} uses the following
 * standards:
 * <br>
 * incorporation rate: 0.1 <br>
 * total number of fragments: 100000 <br>
 * minimal relative mass frequencies: 0.003  <br> 
 * mass precision: 4 <br>
 * mass frequency precision: 4 <br>
 * analyze mass shifts: false <br>
 * @author sfuerst
 *
 */
public class IsotopePatternSimulatorRequest {
	
	private IncorporationRate incorporationRate = new IncorporationRate(0.1);
	private Double totalNumberOfFragments = 100000.0;
	private Double minimalRelativeFrequency = 0.003;
	private Integer roundedMassPrecision = 4;
	private Integer roundedFrequenciesPrecision = 4;
	private FragmentList fragments;
	private Boolean analyzeMassShifts = false;
	private FrequencyType targetFrequencyType = FrequencyType.RELATIVE;
	private int charge = 1;
	
	/**
	 * Creates a general {@link IsotopePatternSimulatorRequest} using the following
	 * standards: <br>
	 * incorporation rate: 1 <br>
	 * total number of fragments: 100000 <br>
	 * minimal relative mass frequencies: 0.003 <br>
	 * mass precision: 4 <br>
	 * mass frequency precision: 4 <br>
	 * analyze mass shifts: false <br>
	 * frequency type: RELATIVE <br>
	 */
	public IsotopePatternSimulatorRequest()  {
		
	}
	
	/**
	 * Creates a {@link IsotopePatternSimulatorRequest} using the specified
	 * parameters. If a parameter is null the following standards will be used: <br>
	 * incorporation rate: 1 <br>
	 * total number of fragments: 100000 <br>
	 * minimal relative mass frequencies: 0.003 <br>
	 * mass precision: 4 <br>
	 * mass frequency precision: 4 <br>
	 * analyze mass shifts: false <br>
	 * target frequency type: RELATIVE <br>
	 * <br>
	 * 
	 * @param incorporationRate
	 * @param totalNumberOfFragments
	 * @param minimalRelativeFrequency
	 * @param roundedMassPrecision
	 * @param roundedFrequenciesPrecision
	 * @param fragments
	 * @param analyzeMassShifts
	 */
	public IsotopePatternSimulatorRequest(IncorporationRate incorporationRate, Double totalNumberOfFragments,
			FrequencyType targetFrequencyType, Double minimalRelativeFrequency, Integer roundedMassPrecision,
			Integer roundedFrequenciesPrecision,
			FragmentList fragments, Boolean analyzeMassShifts) {
		if (incorporationRate != null) {
			this.incorporationRate = incorporationRate;
		}
		if (totalNumberOfFragments != null) {
			this.totalNumberOfFragments = totalNumberOfFragments;
		}
		if (minimalRelativeFrequency != null) {
			this.minimalRelativeFrequency = minimalRelativeFrequency;	
		}
		if (roundedMassPrecision != null) {
			this.roundedMassPrecision = roundedMassPrecision;
		}
		if (roundedFrequenciesPrecision != null) {
			this.roundedFrequenciesPrecision = roundedFrequenciesPrecision;
		}
		if (analyzeMassShifts != null) {
			this.setAnalyzeMassShifts(analyzeMassShifts);
		}
		if (targetFrequencyType != null) {
			this.targetFrequencyType = targetFrequencyType;
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

	public Double getMinimalRelativeFrequency() {
		return minimalRelativeFrequency;
	}

	public void setMinimalRelativeFrequency(Double minimalRelativeFrequency) {
		this.minimalRelativeFrequency = minimalRelativeFrequency;
	}

	public Integer getRoundedMassPrecision() {
		return roundedMassPrecision;
	}

	public void setRoundedMassPrecision(Integer roundedMassPrecision) {
		this.roundedMassPrecision = roundedMassPrecision;
	}

	public Integer getRoundedFrequenciesPrecision() {
		return roundedFrequenciesPrecision;
	}

	public void setRoundedFrequenciesPrecision(Integer roundedFrequenciesPrecision) {
		this.roundedFrequenciesPrecision = roundedFrequenciesPrecision;
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

	public FrequencyType getTargetFrequencyType() {
		return targetFrequencyType;
	}

	public void setTargetFrequencyType(FrequencyType targetFrequencyType) {
		this.targetFrequencyType = targetFrequencyType;
	}

	public int getCharge() {
		return charge;
	}

	public void setCharge(int charge) {
		this.charge = charge;
	}

}
