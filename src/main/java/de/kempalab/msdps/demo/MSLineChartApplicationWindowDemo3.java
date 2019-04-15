package de.kempalab.msdps.demo;

import java.io.IOException;

import org.jfree.ui.RefineryUtilities;

import de.kempalab.msdps.Fragment;
import de.kempalab.msdps.FragmentList;
import de.kempalab.msdps.FragmentsDatabase;
import de.kempalab.msdps.MassSpectrum;
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

public class MSLineChartApplicationWindowDemo3 {

	public static final MyLogger LOGGER = MyLogger.getLogger(MSLineChartApplicationWindowDemo3.class);

	public static void main(String[] args) throws TypeMismatchException, IOException, FragmentNotFoundException {
		IsotopePatternSimulatorRequest simulatorRequest = new IsotopePatternSimulatorRequest();
		Fragment fragment188 = FragmentsDatabase.getFragment(FragmentKey.ASN_188);
		Fragment fragment243 = FragmentsDatabase.getFragment(FragmentKey.ASN_243);
		Fragment fragment419 = FragmentsDatabase.getFragment(FragmentKey.ASN_419);
		fragment188.changeCapacity("N");
		simulatorRequest.setFragments(new FragmentList(fragment188, fragment243, fragment419));
		simulatorRequest.setIncorporationRate(new IncorporationRate(1));
		simulatorRequest.setMinimalIntensity(0.1);
		simulatorRequest.setAnalyzeMassShifts(false);
		simulatorRequest.setTotalNumberOfFragments(10000.0);
		simulatorRequest.setRoundedMassPrecision(4);
		simulatorRequest.setTargetIntensityType(IntensityType.RELATIVE);
		simulatorRequest.setCharge(1);
		IsotopePatternSimulatorResponse response = IsotopePatternSimulator.simulate(simulatorRequest);
		MassSpectrum spectrum188 = response.getMsDatabaseList().get(0).getMixedSpectrum();
		MassSpectrum spectrum243 = response.getMsDatabaseList().get(1).getMixedSpectrum();
		MassSpectrum spectrum419 = response.getMsDatabaseList().get(2).getMixedSpectrum();

		MassSpectrum spectrum = spectrum188.merge(spectrum243);
		spectrum = spectrum.merge(spectrum419);
		MassSpectrum continuous = spectrum.simulateContinuousHighRes(120000, 100);

//		continuous = continuous.roundMasses(3);
		MSLineChartApplicationWindow demo = new MSLineChartApplicationWindow("ASN-15N2-13C4", "ASN-15N2-13C4", "",
				continuous);
		demo.pack();
		demo.setSize(1300, 750);
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
	}

}
