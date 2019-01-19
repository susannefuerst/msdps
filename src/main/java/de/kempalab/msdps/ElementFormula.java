package de.kempalab.msdps;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.kempalab.msdps.constants.Element;
import de.kempalab.msdps.constants.ErrorMessage;

/**
 * A map of each element in a formula to its count.
 * 
 * @author Susanne Fürst, susannefuerst@freenet.de, susanne.fuerst@mdc-berlin.de
 *
 */
@SuppressWarnings("serial")
public class ElementFormula extends LinkedHashMap<Element, Integer> {

	public static final String FORMULA_REG_EX = "([A-Z][a-z]{0,1})([0-9]{0,3})";

	public List<Entry<Element, Integer>> toEntryList() {
		List<Entry<Element, Integer>> entryList = new ArrayList<>(this.entrySet());
		return entryList;
	}

	public static ElementFormula fromString(String formula) {
		if (formula.contains("(")) {
			throw new InputMismatchException(ErrorMessage.INVALID_FORMULA.getMessage() + "[" + formula + "]");
		}
		if (formula.equals("NA")) {
			return new ElementFormula();
		}
		ElementFormula elements = new ElementFormula();
		Matcher formulaMatcher = Pattern.compile(FORMULA_REG_EX).matcher(formula);
		ArrayList<String> elementTokens = new ArrayList<String>();
		while (formulaMatcher.find()) {
			elementTokens.add(formulaMatcher.group());
		}
		for (String elementToken : elementTokens) {
			Matcher elementMatcher = Pattern.compile(FORMULA_REG_EX).matcher(elementToken);
			if (elementMatcher.matches()) {
				Element element = Element.valueOf(elementMatcher.group(1));
				Integer quantity = elementMatcher.group(2).equals("") ? Integer.valueOf(1)
						: Integer.valueOf(elementMatcher.group(2));
				elements.put(element, quantity);
			}
		}
		return elements;
	}

	public String toSimpleString() {
		StringBuilder builder = new StringBuilder();
		for (Entry<Element, Integer> entry : this.entrySet()) {
			if (entry.getValue() != 0) {
				String number = entry.getValue() > 1 ? String.valueOf(entry.getValue()) : "";
				builder.append(entry.getKey().name() + number);
			}
		}
		return builder.toString();
	}

	public ElementFormula copy() {
		ElementFormula copy = new ElementFormula();
		for (Entry<Element, Integer> entry : this.entrySet()) {
			copy.put(entry.getKey(), entry.getValue());
		}
		return copy;
	}

	public IsotopeFormula toIsotopeFormula() {
		IsotopeFormula formula = new IsotopeFormula();
		for (Entry<Element, Integer> entry : this.entrySet()) {
			formula.put(entry.getKey().lightestIsotope(), entry.getValue());
		}
		return formula;
	}

	public Double calculateMass() {
		Double mass = 0.0;
		for (Entry<Element, Integer> entry : this.entrySet()) {
			mass = mass + entry.getKey().mostCommonIsotope().getAtomicMass() * entry.getValue();
		}
		return mass;
	}

	public ElementList toElementList() {
		ElementList list = new ElementList();
		for (Entry<Element,Integer> entry : this.entrySet()) {
			list.add(entry.getKey());
		}
		return list;
	}
	
	public ArrayList<MassSpectrum> multiElementSpectra() {
		ArrayList<MassSpectrum> multiElementSpectra = new ArrayList<>();
		for (Entry<Element,Integer> formulaEntry : this.entrySet()) {
			Element element = formulaEntry.getKey();
			multiElementSpectra.add(element.multiElementSpectrum(formulaEntry.getValue(), 0.0));
		}
		return multiElementSpectra;
	}

}
