package de.kempalab.msdps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.kempalab.msdps.constants.Element;
import de.kempalab.msdps.constants.ErrorMessage;
/**
 * A map of each element in a molecule to its total number in the molecule
 * @author sfuerst
 *
 */
@SuppressWarnings("serial")
public class ElementFormula extends HashMap<Element, Integer> {
	
	public static final String FORMULA_REG_EX = "([A-Z][a-z]{0,1})([0-9]{0,3})";
	
	public List<Entry<Element, Integer>> toEntryList() {
		List<Entry<Element, Integer>> entryList = new ArrayList<>(this.entrySet());
		return entryList;
	}
	
	public static ElementFormula fromString(String formula) {
		if (formula.contains("(")) {
			throw new InputMismatchException(ErrorMessage.INVALID_FORMULA.getMessage() + "[" + formula + "]");
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
				Integer quantity = elementMatcher.group(2).equals("") ? Integer.valueOf(1) : Integer.valueOf(elementMatcher.group(2));
				elements.put(element, quantity);
			}
		}
		return elements;
	}

}
