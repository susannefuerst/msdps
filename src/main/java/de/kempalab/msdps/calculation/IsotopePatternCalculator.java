package de.kempalab.msdps.calculation;

import java.util.ArrayList;
import java.util.Map.Entry;

import de.kempalab.msdps.ElementFormula;
import de.kempalab.msdps.Fragment;
import de.kempalab.msdps.FragmentList;
import de.kempalab.msdps.IsotopeComposition;
import de.kempalab.msdps.IsotopeFormula;
import de.kempalab.msdps.IsotopePattern;
import de.kempalab.msdps.MassSpectrum;
import de.kempalab.msdps.MassSpectrumList;
import de.kempalab.msdps.constants.Element;
import de.kempalab.msdps.constants.IntensityType;
import de.kempalab.msdps.constants.SpectrumType;
import de.kempalab.msdps.data.IncorporationRate;
import de.kempalab.msdps.exception.TypeMismatchException;
import de.kempalab.msdps.simulation.IsotopePatternSimulator;

public class IsotopePatternCalculator {
	
	public static IsotopePatternCalculatorResponse calculateNaturalPattern(IsotopePatternCalculatorRequest request) throws TypeMismatchException {
		Double minimalIntensity = request.getMinimalIntensity();
		Integer roundedMassPrecision = request.getRoundedMassPrecision();
		Integer roundedIntensityPrecision = request.getRoundedMassPrecision();
		FragmentList fragments = request.getFragments();
		Boolean analyseComposition = request.getAnalyseComposition();
		IntensityType targetIntensityType = request.getTargetIntensityType();
		int charge = request.getCharge();
		IsotopePatternCalculatorResponse response = new IsotopePatternCalculatorResponse();
		MassSpectrumList patternList = new MassSpectrumList();
		response.setPatternList(patternList);
		for (Fragment fragment : fragments) {
			MassSpectrum calculatedSpectrum;
			ElementFormula formula = fragment.getFormula();
			ArrayList<MassSpectrum> multiElementSpectra = formula.multiElementSpectra();
			if (multiElementSpectra.size() == 1) {
				calculatedSpectrum = multiElementSpectra.get(0);
			} else {
				ArrayList<MassSpectrum> combinedSpectra = calculateCombinedSpectra(multiElementSpectra);
				calculatedSpectrum = combinedSpectra.get(combinedSpectra.size() - 1);
			}
			if (charge > 0) {
				calculatedSpectrum = calculatedSpectrum.adjustToCharge(charge);
			}
			MassSpectrum modifiedSpectrum = IsotopePatternSimulator.prepareSpectrum(calculatedSpectrum, roundedMassPrecision, roundedIntensityPrecision, minimalIntensity, targetIntensityType);
			if (!analyseComposition) {
				patternList.add(modifiedSpectrum);
			} else {
				IsotopePattern pattern = modifiedSpectrum.analyseCompositions(formula);
				patternList.add(pattern);
			}
		}
		return response;
	}
	
	public static IsotopePatternCalculatorResponse calculateTracedPattern(IsotopePatternCalculatorRequest request) throws TypeMismatchException {
		Double minimalIntensity = request.getMinimalIntensity();
		Integer roundedMassPrecision = request.getRoundedMassPrecision();
		Integer roundedIntensityPrecision = request.getRoundedMassPrecision();
		Boolean analyseComposition = request.getAnalyseComposition();
		IntensityType targetIntensityType = request.getTargetIntensityType();
		int charge = request.getCharge();
		Element tracer1 = request.getTracer1();
		Element tracer2 = request.getTracer2();
		IncorporationRate tracer1Inc = request.getTracer1Inc();
		IncorporationRate tracer2Inc = request.getTracer2Inc();
		IncorporationRate tracerAllInc = request.getTracerAllInc();
		IncorporationRate totalInc = new IncorporationRate(tracer1Inc.getRateValue() + tracer2Inc.getRateValue() + tracerAllInc.getRateValue());
		FragmentList fragments = request.getFragments();
		IsotopePatternCalculatorRequest newRequest = new IsotopePatternCalculatorRequest();
		newRequest.setAnalyseComposition(false);
		newRequest.setCharge(0);
		newRequest.setMinimalIntensity(null);
		newRequest.setRoundedIntensityPrecision(null);
		newRequest.setRoundedIntensityPrecision(null);
		newRequest.setTargetIntensityType(IntensityType.MID);
		newRequest.setFragments(fragments);
		IsotopePatternCalculatorResponse naturalResponse = calculateNaturalPattern(newRequest);
		newRequest.setFragments(reducedFragments(fragments, null));
		IsotopePatternCalculatorResponse reducedNaturalTracerAllResponse = calculateNaturalPattern(newRequest);
		newRequest.setFragments(reducedFragments(fragments, tracer1));
		IsotopePatternCalculatorResponse reducedNaturalTracer1Response = calculateNaturalPattern(newRequest);
		newRequest.setFragments(reducedFragments(fragments, tracer2));
		IsotopePatternCalculatorResponse reducedNaturalTracer2Response = calculateNaturalPattern(newRequest);
		IsotopePatternCalculatorResponse response = new IsotopePatternCalculatorResponse();
		MassSpectrumList patternList = new MassSpectrumList();
		response.setPatternList(patternList);
		for (int index = 0; index < fragments.size(); index++) {
			Fragment fragment = fragments.get(index);
			MassSpectrum reducedNaturalTracerAllSpectrum = reducedNaturalTracerAllResponse.getPatternList().get(index);
			MassSpectrum reducedNaturalTracer1Spectrum = reducedNaturalTracer1Response.getPatternList().get(index);
			MassSpectrum reducedNaturalTracer2Spectrum = reducedNaturalTracer2Response.getPatternList().get(index);
			ElementFormula tracerAllFormula = fragment.getTracerCapacity();
			ElementFormula tracer1Formula = new ElementFormula();
			tracer1Formula.put(tracer1, fragment.getTracerCapacity().get(tracer1));
			ElementFormula tracer2Formula = new ElementFormula();
			tracer2Formula.put(tracer2, fragment.getTracerCapacity().get(tracer2));
			MassSpectrum naturalSpectrum = naturalResponse.getPatternList().get(index);
			MassSpectrum tracerAllSpectrum = reducedNaturalTracerAllSpectrum.addLabel(tracerAllFormula);
			MassSpectrum tracer1Spectrum = reducedNaturalTracer1Spectrum.addLabel(tracer1Formula);
			MassSpectrum tracer2Spectrum = reducedNaturalTracer2Spectrum.addLabel(tracer2Formula);
			tracerAllSpectrum = tracerAllSpectrum.scale(tracerAllInc.getRateValue());
			tracer1Spectrum = tracer1Spectrum.scale(tracer1Inc.getRateValue());
			tracer2Spectrum = tracer2Spectrum.scale(tracer2Inc.getRateValue());
			naturalSpectrum = naturalSpectrum.scale(1 - totalInc.getRateValue());
			MassSpectrum finalSpectrum = naturalSpectrum.merge(tracer1Spectrum);
			finalSpectrum = finalSpectrum.merge(tracer2Spectrum);
			finalSpectrum = finalSpectrum.merge(tracerAllSpectrum);
			if (charge > 0) {
				finalSpectrum = finalSpectrum.adjustToCharge(charge);
			}
			MassSpectrum modifiedSpectrum = IsotopePatternSimulator.prepareSpectrum(finalSpectrum, roundedMassPrecision, roundedIntensityPrecision, minimalIntensity, targetIntensityType);
			if (!analyseComposition) {
				patternList.add(modifiedSpectrum);
			} else {
				IsotopePattern pattern = modifiedSpectrum.analyseCompositions(fragment.getFormula());
				patternList.add(pattern);
			}
		}
		return response;
	}

	private static FragmentList reducedFragments(FragmentList fragments, Element tracer) {
		FragmentList reducedFragments = new FragmentList();
		for (Fragment fragment : fragments) {
			Fragment reducedFragment = fragment.reduceByCapacity(tracer);
			reducedFragments.add(reducedFragment);
		}
		return reducedFragments;
	}

	private static ArrayList<MassSpectrum> calculateCombinedSpectra(ArrayList<MassSpectrum> multiElementSpectra) {
		ArrayList<MassSpectrum> combinedSpectra = new ArrayList<>();
		combinedSpectra.add(multiElementSpectra.get(0));
		for (int i = 0; i < multiElementSpectra.size() - 1; i++) {
			MassSpectrum firstSpectrum = combinedSpectra.get(i);
			MassSpectrum secondSpectrum = multiElementSpectra.get(i+1);
			MassSpectrum newCombinedSpectrum = new MassSpectrum(IntensityType.MID, SpectrumType.CENTROIDED);
			IsotopeComposition newComposition = new IsotopeComposition();
			for (Entry<Double,Double> firstEntry : firstSpectrum.entrySet()) {
				for (Entry<Double,Double> secondEntry : secondSpectrum.entrySet()) {
					Double newMass = firstEntry.getKey() + secondEntry.getKey();
					Double newIntensity = firstEntry.getValue() * secondEntry.getValue();
					IsotopeFormula newFormula = firstSpectrum.getComposition(firstEntry.getKey()).add(secondSpectrum.getComposition(secondEntry.getKey()));
					newCombinedSpectrum.put(newMass, newIntensity);
					newComposition.put(newMass, newFormula);
				}
			}
			newCombinedSpectrum.setCompositions(newComposition);
			combinedSpectra.add(newCombinedSpectrum);
		}
		return combinedSpectra;
	}
}
