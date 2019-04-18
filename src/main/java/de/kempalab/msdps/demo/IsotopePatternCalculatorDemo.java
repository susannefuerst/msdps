package de.kempalab.msdps.demo;

import de.kempalab.msdps.Fragment;
import de.kempalab.msdps.FragmentList;
import de.kempalab.msdps.FragmentsDatabase;
import de.kempalab.msdps.calculation.IsotopePatternCalculator;
import de.kempalab.msdps.calculation.IsotopePatternCalculatorRequest;
import de.kempalab.msdps.calculation.IsotopePatternCalculatorResponse;
import de.kempalab.msdps.constants.FragmentKey;
import de.kempalab.msdps.constants.IntensityType;
import de.kempalab.msdps.exception.FragmentNotFoundException;
import de.kempalab.msdps.exception.TypeMismatchException;

public class IsotopePatternCalculatorDemo {
	
	public static void main(String[] args) throws FragmentNotFoundException, TypeMismatchException {
		IsotopePatternCalculatorRequest calculatorRequest = new IsotopePatternCalculatorRequest();
		Fragment fragment = FragmentsDatabase.getFragment(FragmentKey.ASN_419);
		calculatorRequest.setFragments(new FragmentList(fragment));
		calculatorRequest.setCharge(1);
		calculatorRequest.setRoundedIntensityPrecision(4);
		calculatorRequest.setRoundedMassPrecision(4);
		calculatorRequest.setTargetIntensityType(IntensityType.RELATIVE);
		calculatorRequest.setMinimalIntensity(0.0);
		calculatorRequest.setAnalyseComposition(false);
		IsotopePatternCalculatorResponse response = IsotopePatternCalculator.calculateNaturalPattern(calculatorRequest);
		System.out.println((response.getPatternList().get(0)).toString());
	}

}
