package de.kempalab.msdps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.kempalab.msdps.constants.Element;
import de.kempalab.msdps.constants.ErrorMessage;
import de.kempalab.msdps.constants.FragmentKey;

public class Fragment {
	
	private FragmentKey fragmentKey;
	/*
	 * the formula of the fragment in the simple format like C11H28NO3Si3
	 */
	private String fragmentFormula = new String();
	/*
	 * defines which and how many elements in the fragment may be labeled as the result of an experimental setup (labeling experiment)
	 */
	private String capacityFormula = new String();
	private ElementFormula fragmentComponents = new ElementFormula();
	private ExperimentalIncorporationCapacity experimentalIncorporationCapacity = new ExperimentalIncorporationCapacity();
	private String derivate;
	
	public static final String FORMULA_REG_EX = "([A-Z][a-z]{0,1})([0-9]{0,3})";
	
	public Fragment() {
		
	}
	
	public Fragment(FragmentKey fragmentKey, String fragmentFormula, String capacityFormula) {
		this.fragmentKey = fragmentKey;
		this.fragmentFormula = fragmentFormula;
		this.capacityFormula = capacityFormula;
		fromFormula(fragmentFormula, fragmentComponents);
		fromFormula(capacityFormula, experimentalIncorporationCapacity);
	}

	private void fromFormula(String formula, HashMap<Element, Integer> map) {	
		if (formula.contains("(")) {
			throw new InputMismatchException(ErrorMessage.INVALID_FORMULA.getMessage() + "[" + formula + "]");
		}
		if (formula.equals("NA")) {
			return;
		}
		Matcher formulaMatcher = Pattern.compile(FORMULA_REG_EX).matcher(formula);
		ArrayList<String> elementTokens = new ArrayList<String>();
		while (formulaMatcher.find()) {
			elementTokens.add(formulaMatcher.group());
		}
		for (String elementToken : elementTokens) {
			Matcher elementMatcher = Pattern.compile(FORMULA_REG_EX).matcher(elementToken);
			if (elementMatcher.matches()) {
				Element element = Element.valueOf(elementMatcher.group(1));
				Integer quantity = elementMatcher.group(2).equals("") ? Integer.valueOf(1) : Integer.valueOf(elementMatcher.group(2));
				map.put(element, quantity);
			}
		}
	}
	
	public double relativeMass() {
		double mass = 0.0;
		for (Entry<Element, Integer> entry : fragmentComponents.entrySet()) {
			Element element = entry.getKey();
			Integer numberOfElements = entry.getValue();
			mass = mass + element.getRelativeAtomicMass() * numberOfElements;
		}
		return mass;
	}
	
	public double lowestMass() {
		double mass = 0;
		for (Entry<Element, Integer> entry : fragmentComponents.entrySet()) {
			Element element = entry.getKey();
			Integer numberOfElements = entry.getValue();
			mass = mass + element.lowestMass() * numberOfElements;
		}
		return mass;
	}
	
	public double highestMass() {
		double mass = 0;
		for (Entry<Element, Integer> entry : fragmentComponents.entrySet()) {
			Element element = entry.getKey();
			Integer numberOfElements = entry.getValue();
			mass = mass + element.highestMass() * numberOfElements;
		}
		return mass;
	}
	
	public double lowestFullIncorporatedMass() {
		double mass = 0;
		for (Entry<Element, Integer> entry : fragmentComponents.entrySet()) {
			Element element = entry.getKey();
			Integer totalNumberOfElements = entry.getValue();
			Integer numberOfIncorporatedElements = experimentalIncorporationCapacity.get(element) == null ? 0 : experimentalIncorporationCapacity.get(element);
			Integer numberOfLightElements = totalNumberOfElements - numberOfIncorporatedElements;
			mass = mass + numberOfIncorporatedElements * element.highestMass() + numberOfLightElements * element.lowestMass();
		}
		return mass;
	}
	
	public FragmentKey getFragmentKey() {
		return fragmentKey;
	}
	
	/**
	 * a string representation as follows:
	 * Fragment: C5H13OSi, derived from Lactic acid
	 */
	public String toString() {
		return "Fragment: " + fragmentFormula + ", derived from: " + fragmentKey.getMetaboliteKey().getMoleculeName();
	}
	
	/**
	 * 
	 * @return the formula of the fragment in a format like: C5H13OSi
	 */
	public String getFormula() {
		return fragmentFormula;
	}
	
	/**
	 * 
	 * @return the {@link ElementFormula} that corresponds to this fragment
	 */
	public ElementFormula getComponents() {
		return fragmentComponents;
	}
	
	/**
	 * 
	 * @return the {@link ExperimentalIncorporationCapacity} corresponding to this fragment
	 */
	public ExperimentalIncorporationCapacity getExperimentalIncorporationCapacity() {
		return experimentalIncorporationCapacity;
	}
	
	/**
	 * 
	 * @param additionalPart
	 * @return a filename like: GLN_156_C4N_additionalPart
	 */
	public String toFileName(String additionalPart) {
		return getFragmentKey().getMetaboliteKey().getAbbreviation() + "_" + 
				getFragmentKey().getBaseMass() + "_" + getCapacityFormula() + "_" + additionalPart;
	}
	
	/**
	 * 
	 * @return a string representation as:
	 * Fragment: C5H13OSi, derived from Lactic acid, capacity: C2
	 */
	public String toDescriptiveString() {
		return "Fragment: " + fragmentFormula + ", derived from: " + 
				getFragmentKey().getMetaboliteKey().getAbbreviation() + ", capacity: " + capacityFormula;
	}
	
	/**
	 * the capacity defines which and how many elements in the fragment may be labeled as the result of an experimental setup
	 * (labeling experiment). This changes the capacity
	 * @param formula the formula describing the new capacity in the format C2N
	 */
	public void changeCapacity(String formula) {
		this.capacityFormula = formula;
		experimentalIncorporationCapacity = new ExperimentalIncorporationCapacity();
		fromFormula(formula, experimentalIncorporationCapacity);
	}
	
	public String getCapacityFormula() {
		return capacityFormula;
	}
	
	/**
	 * 
	 * @return for example Gln for glutamine
	 */
	public String metaboliteAbbreviation() {
		return fragmentKey.getMetaboliteKey().getAbbreviation();
	}
	
	/**
	 * 
	 * @return the fragments base (nominal) mass
	 */
	public int baseMass() {
		return getFragmentKey().getBaseMass();
	}

	public String metaboliteName() {
		return getFragmentKey().getMetaboliteKey().getMoleculeName();
	}

	public Fragment copy() {
		Fragment fragment = new Fragment(getFragmentKey(), getFormula(), getCapacityFormula());
		return fragment;
	}

	public String getDerivate() {
		return derivate;
	}

	public void setDerivate(String derivate) {
		this.derivate = derivate;
	}
 
}
