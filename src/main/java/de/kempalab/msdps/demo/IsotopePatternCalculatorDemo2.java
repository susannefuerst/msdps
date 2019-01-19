package de.kempalab.msdps.demo;

import de.kempalab.msdps.Fragment;
import de.kempalab.msdps.FragmentList;
import de.kempalab.msdps.FragmentsDatabase;
import de.kempalab.msdps.IsotopePattern;
import de.kempalab.msdps.calculation.IsotopePatternCalculator;
import de.kempalab.msdps.calculation.IsotopePatternCalculatorRequest;
import de.kempalab.msdps.calculation.IsotopePatternCalculatorResponse;
import de.kempalab.msdps.constants.Element;
import de.kempalab.msdps.constants.FragmentKey;
import de.kempalab.msdps.constants.IntensityType;
import de.kempalab.msdps.data.IncorporationRate;
import de.kempalab.msdps.exception.FragmentNotFoundException;
import de.kempalab.msdps.exception.IntensityTypeMismatchException;

public class IsotopePatternCalculatorDemo2 {
	
	public static void main(String[] args) throws FragmentNotFoundException, IntensityTypeMismatchException {
		IsotopePatternCalculatorRequest calculatorRequest = new IsotopePatternCalculatorRequest();
		Fragment fragment = FragmentsDatabase.getFragment(FragmentKey.GLN_156);
		calculatorRequest.setFragments(new FragmentList(fragment));
		calculatorRequest.setCharge(1);
		calculatorRequest.setRoundedIntensityPrecision(4);
		calculatorRequest.setRoundedMassPrecision(4);
		calculatorRequest.setTargetIntensityType(IntensityType.RELATIVE);
		calculatorRequest.setMinimalIntensity(0.1);
		calculatorRequest.setAnalyseComposition(true);
		calculatorRequest.setTracer1(Element.C);
		calculatorRequest.setTracer2(Element.N);
		calculatorRequest.setTracer1Inc(new IncorporationRate(0.2));
		calculatorRequest.setTracer2Inc(new IncorporationRate(0.2));
		calculatorRequest.setTracerAllInc(new IncorporationRate(0.2));
		IsotopePatternCalculatorResponse response = IsotopePatternCalculator.calculateTracedPattern(calculatorRequest);
		System.out.println(((IsotopePattern) response.getPatternList().get(0)).toString());
	}

}
