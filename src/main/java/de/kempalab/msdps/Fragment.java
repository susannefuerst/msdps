package de.kempalab.msdps;

import java.util.Map.Entry;

import de.kempalab.msdps.constants.Element;
import de.kempalab.msdps.constants.FragmentKey;

/**
 * A Fragment consists of a formula and its tracer capacity. The tracer capacity
 * describes how many atoms of a tracer (i.e. 13C, 15N,...) can be incorporated
 * in the fragment. This depends on the experimental setup. For example
 * (13C)6-glucose labeling would result in (13C)3-lactic acid and
 * (13C)2-alanine, so lactic acid would have a capacity of 3 carbons and alanine
 * a capacity of 2 carbons. (13C)6-glucose an (15N)2-labeling would result in
 * (13C)3-lactic acid and (13C)2(15N)-alanine, so lactic acid would have a
 * capacity of 3 carbons and alanine a cpacity of 2 carbons an 1 nitrogen. But
 * the tracer capacitiy does not need to mirror reality as you just want to
 * simulate how the spectrum of a fragment with a certain incorporation would
 * look like.
 * 
 * @author Susanne Fürst, susannefuerst@freenet.de, susanne.fuerst@mdc-berlin.de
 *
 */
public class Fragment {

	/*
	 * the formula of the fragment
	 */
	private ElementFormula formula = new ElementFormula();
	/*
	 * defines which and how many elements in the fragment may be labeled as the
	 * result of an experimental setup (labeling experiment)
	 */
	private ElementFormula tracerCapacity = new ElementFormula();
	private FragmentKey fragmentKey;
	private String derivate;
	private Double retentionTime;

	public Fragment() {

	}

	/**
	 * 
	 * @param fragmentFormula, format: C5H14NSi
	 * @param capacityFormula, format: C4N
	 */
	public Fragment(String fragmentFormula, String tracerCapacity) {
		this.formula = ElementFormula.fromString(fragmentFormula);
		this.tracerCapacity = ElementFormula.fromString(tracerCapacity);
	}

	public Fragment(FragmentKey fragmentKey, String fragmentFormula, String capacityFormula) {
		this(fragmentFormula, capacityFormula);
		this.fragmentKey = fragmentKey;
	}

	public Fragment(FragmentKey fragmentKey, String fragmentFormula, String capacityFormula, Double retentionTime) {
		this(fragmentKey, fragmentFormula, capacityFormula);
		this.retentionTime = retentionTime;
	}

	public double relativeMass() {
		double mass = 0.0;
		for (Entry<Element, Integer> entry : formula.entrySet()) {
			Element element = entry.getKey();
			Integer numberOfElements = entry.getValue();
			mass = mass + element.getRelativeAtomicMass() * numberOfElements;
		}
		return mass;
	}

	public double lowestMass() {
		double mass = 0;
		for (Entry<Element, Integer> entry : formula.entrySet()) {
			Element element = entry.getKey();
			Integer numberOfElements = entry.getValue();
			mass = mass + element.lowestMass() * numberOfElements;
		}
		return mass;
	}

	public double highestMass() {
		double mass = 0;
		for (Entry<Element, Integer> entry : formula.entrySet()) {
			Element element = entry.getKey();
			Integer numberOfElements = entry.getValue();
			mass = mass + element.highestMass() * numberOfElements;
		}
		return mass;
	}

	public double lowestFullIncorporatedMass() {
		double mass = 0;
		for (Entry<Element, Integer> entry : formula.entrySet()) {
			Element element = entry.getKey();
			Integer totalNumberOfElements = entry.getValue();
			Integer numberOfIncorporatedElements = tracerCapacity.get(element) == null ? 0
					: tracerCapacity.get(element);
			Integer numberOfLightElements = totalNumberOfElements - numberOfIncorporatedElements;
			mass = mass + numberOfIncorporatedElements * element.highestMass()
					+ numberOfLightElements * element.lowestMass();
		}
		return mass;
	}

	/**
	 * a string representation as follows: Fragment: C5H13OSi
	 */
	public String toString() {
		return "Fragment: " + formula.toSimpleString();
	}

	/**
	 * 
	 * @return the {@link ElementFormula} that corresponds to this fragment
	 */
	public ElementFormula getFormula() {
		return formula;
	}

	/**
	 * 
	 * @return the {@link TracerCapacity} corresponding to this fragment
	 */
	public ElementFormula getTracerCapacity() {
		return tracerCapacity;
	}

	public void setFormula(ElementFormula formula) {
		this.formula = formula;
	}

	public void setTracerCapacity(ElementFormula tracerCapacity) {
		this.tracerCapacity = tracerCapacity;
	}

	/**
	 * the capacity defines which and how many elements in the fragment may be
	 * labeled as the result of an experimental setup (labeling experiment).
	 * 
	 * @param formula the formula describing the new capacity in the format C2N
	 */
	public void setCapacity(String formula) {
		tracerCapacity = ElementFormula.fromString(formula);
	}

	public FragmentKey getFragmentKey() {
		return fragmentKey;
	}

	public void setFragmentKey(FragmentKey fragmentKey) {
		this.fragmentKey = fragmentKey;
	}

	public Fragment copy() {
		Fragment fragment = new Fragment();
		fragment.setFormula(formula.copy());
		fragment.setTracerCapacity(tracerCapacity.copy());
		fragment.setFragmentKey(getFragmentKey());
		fragment.setDerivate(getDerivate());
		fragment.setRetentionTime(getRetentionTime());
		return fragment;
	}

	public String getDerivate() {
		return derivate;
	}

	public void setDerivate(String derivate) {
		this.derivate = derivate;
	}

	public int baseMass() {
		return getFragmentKey().getBaseMass();
	}

	public Double getRetentionTime() {
		return retentionTime;
	}

	public void setRetentionTime(Double retentionTime) {
		this.retentionTime = retentionTime;
	}

	public String metaboliteAbbreviation() {
		return getFragmentKey().getMetaboliteKey().getAbbreviation();
	}

	public String metaboliteName() {
		return getFragmentKey().getMetaboliteKey().getMoleculeName();
	}

	public void changeCapacity(String formula) {
		setCapacity(formula);
	}

	public Fragment reduceByCapacity(Element tracer) {
		Fragment newFragment = this.copy();
		ElementFormula newFormula = formula.copy();
		if (tracer == null) {
			for (Entry<Element,Integer> entry : this.tracerCapacity.entrySet()) {
				newFormula.put(entry.getKey(), formula.get(entry.getKey()) - entry.getValue());
			}
		} else {
			newFormula.put(tracer, formula.get(tracer) - tracerCapacity.get(tracer));
		}
		newFragment.setFormula(newFormula);
		newFragment.setCapacity("");
		return newFragment;
	}


}
