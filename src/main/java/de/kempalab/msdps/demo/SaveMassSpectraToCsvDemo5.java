package de.kempalab.msdps.demo;

import java.io.IOException;

import org.jfree.ui.RefineryUtilities;

import de.kempalab.msdps.Fragment;
import de.kempalab.msdps.FragmentList;
import de.kempalab.msdps.FragmentsDatabase;
import de.kempalab.msdps.MassSpectrum;
import de.kempalab.msdps.constants.Element;
import de.kempalab.msdps.constants.FragmentKey;
import de.kempalab.msdps.constants.IntensityType;
import de.kempalab.msdps.data.IncorporationRate;
import de.kempalab.msdps.exception.FragmentNotFoundException;
import de.kempalab.msdps.exception.TypeMismatchException;
import de.kempalab.msdps.log.MyLogger;
import de.kempalab.msdps.simulation.IsotopePatternSimulator;
import de.kempalab.msdps.simulation.IsotopePatternSimulatorRequest;
import de.kempalab.msdps.simulation.IsotopePatternSimulatorResponse;
import de.kempalab.msdps.visualisation.MSLineChartApplicationWindow;

public class SaveMassSpectraToCsvDemo5 {

	public static final MyLogger LOGGER = MyLogger.getLogger(SaveMassSpectraToCsvDemo5.class);

	public static void main(String[] args)
			throws TypeMismatchException, IOException, FragmentNotFoundException {
		IsotopePatternSimulatorRequest simulatorRequest = new IsotopePatternSimulatorRequest();
		Fragment fragment = FragmentsDatabase.getFragment(FragmentKey.ASN_419);
		simulatorRequest.setFragments(new FragmentList(fragment));
		simulatorRequest.setTracer1(Element.C);
		simulatorRequest.setTracer2(Element.N);
		simulatorRequest.setTracer1Inc(new IncorporationRate(0.0));
		simulatorRequest.setTracer2Inc(new IncorporationRate(0.0));
		simulatorRequest.setTracerAllInc(new IncorporationRate(1.0));
		simulatorRequest.setMinimalIntensity(0.003);
		simulatorRequest.setAnalyzeMassShifts(false);
		simulatorRequest.setTotalNumberOfFragments(10000.0);
		simulatorRequest.setRoundedMassPrecision(4);
		simulatorRequest.setTargetIntensityType(IntensityType.MID);
		simulatorRequest.setCharge(1);
		IsotopePatternSimulatorResponse response = IsotopePatternSimulator
				.simulateIndependentTracerIncorporation(simulatorRequest);

		IsotopePatternSimulatorRequest simulatorRequest2 = new IsotopePatternSimulatorRequest();
		Fragment fragment2 = FragmentsDatabase.getFragment(fragment.getFragmentKey());
		fragment2.changeCapacity("N");
		simulatorRequest2.setFragments(new FragmentList(fragment2));
		simulatorRequest2.setTracer1(Element.C);
		simulatorRequest2.setTracer2(Element.N);
		simulatorRequest2.setTracer1Inc(new IncorporationRate(0.0));
		simulatorRequest2.setTracer2Inc(new IncorporationRate(1.0));
		simulatorRequest2.setTracerAllInc(new IncorporationRate(0.0));
		simulatorRequest2.setMinimalIntensity(simulatorRequest.getMinimalIntensity());
		simulatorRequest2.setAnalyzeMassShifts(false);
		simulatorRequest2.setTotalNumberOfFragments(10000.0);
		simulatorRequest2.setRoundedMassPrecision(4);
		simulatorRequest2.setTargetIntensityType(simulatorRequest.getTargetIntensityType());
		simulatorRequest2.setCharge(1);
		IsotopePatternSimulatorResponse response2 = IsotopePatternSimulator
				.simulateIndependentTracerIncorporation(simulatorRequest2);

		MassSpectrum spectrum1 = response.getSpectrum(0).scale(0.333);
		MassSpectrum spectrum2 = response2.getSpectrum(0).scale(0.333);

		IsotopePatternSimulatorRequest simulatorRequest3 = new IsotopePatternSimulatorRequest();
		Fragment fragment3 = FragmentsDatabase.getFragment(fragment.getFragmentKey());
		fragment3.changeCapacity("N");
		simulatorRequest3.setFragments(new FragmentList(fragment2));
		simulatorRequest3.setTracer1(Element.C);
		simulatorRequest3.setTracer2(Element.N);
		simulatorRequest3.setTracer1Inc(new IncorporationRate(0.0));
		simulatorRequest3.setTracer2Inc(new IncorporationRate(0.0));
		simulatorRequest3.setTracerAllInc(new IncorporationRate(0.0));
		simulatorRequest3.setMinimalIntensity(simulatorRequest.getMinimalIntensity());
		simulatorRequest3.setAnalyzeMassShifts(false);
		simulatorRequest3.setTotalNumberOfFragments(10000.0);
		simulatorRequest3.setRoundedMassPrecision(4);
		simulatorRequest3.setTargetIntensityType(simulatorRequest.getTargetIntensityType());
		simulatorRequest3.setCharge(1);
		IsotopePatternSimulatorResponse response3 = IsotopePatternSimulator
				.simulateIndependentTracerIncorporation(simulatorRequest3);
		MassSpectrum spectrum3 = response3.getSpectrum(0).scale(0.333);

		MassSpectrum spectrum = spectrum1.merge(spectrum2);
		spectrum = spectrum.merge(spectrum3);
		spectrum = IsotopePatternSimulator.prepareSpectrum(spectrum, simulatorRequest.getRoundedMassPrecision(),
				simulatorRequest.getRoundedIntensityPrecision(), 0.1, IntensityType.RELATIVE);

//		IsotopePattern pattern = new IsotopePattern(spectrum, true);
		
		MSLineChartApplicationWindow demo = new MSLineChartApplicationWindow("Bar Demo 1", "", "",
				spectrum.simulateContinuousHighRes(120000, 100, false), true);
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
	}

}
