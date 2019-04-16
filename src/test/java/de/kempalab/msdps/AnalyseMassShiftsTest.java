package de.kempalab.msdps;

import java.io.File;

import de.kempalab.msdps.constants.PathConstants;
import de.kempalab.msdps.constants.SpectrumType;
import de.kempalab.msdps.log.MyLogger;
import junit.framework.TestCase;

public class AnalyseMassShiftsTest extends TestCase {
	
	public static final MyLogger LOGGER = MyLogger.getLogger(AnalyseMassShiftsTest.class);
	private File[] testFiles = new File(PathConstants.TEST_RESOURCES.toAbsolutePath()).listFiles();
	
	public void testAnalyseNaturalMassShifts() {
		for (File file : testFiles) {
			if (file.getName().contains(this.getClass().getSimpleName())) {
				LOGGER.infoConcat("Checking natural MSD for file", file.getName());
				MSShiftDatabase msShiftDatabase = new MSShiftDatabase(file.getAbsolutePath(), SpectrumType.CENTROIDED);
				MassSpectrum naturalSpectrum = msShiftDatabase.getNaturalSpectrum();
				MassShiftDataSet expectedMassShiftDataset = msShiftDatabase.getNaturalMassShifts();
				ElementList availableElements = ElementList.fromFormula(msShiftDatabase.getFragmentFormula());
				MassShiftDataSet actualMassShiftDataset = naturalSpectrum.analyseMassShifts(availableElements);
				LOGGER.info("actualMassShiftDataset:\t" + actualMassShiftDataset);
				LOGGER.info("expectedMassShiftDataset:\t" + expectedMassShiftDataset);
				assertTrue(expectedMassShiftDataset.equalsUpToPermutationOfIsotopes(actualMassShiftDataset));
			}
		}
	}
	
	public void testAnalyseMarkedMassShifts() {
		for (File file : testFiles) {
			if (file.getName().contains(this.getClass().getSimpleName())) {
				LOGGER.infoConcat("Checking marked MSD for file", file.getName());
				MSShiftDatabase msShiftDatabase = new MSShiftDatabase(file.getAbsolutePath(), SpectrumType.CENTROIDED);
				MassSpectrum markedSpectrum = msShiftDatabase.getMarkedSpectrum();
				MassShiftDataSet expectedMassShiftDataset = msShiftDatabase.getMarkedMassShifts();
				ElementList availableElements = ElementList.fromFormula(msShiftDatabase.getFragmentFormula());
				MassShiftDataSet actualMassShiftDataset = markedSpectrum.analyseMassShifts(availableElements);
				LOGGER.info("actualMassShiftDataset:\t" + actualMassShiftDataset);
				LOGGER.info("expectedMassShiftDataset:\t" + expectedMassShiftDataset);
				assertTrue(expectedMassShiftDataset.equalsUpToPermutationOfIsotopes(actualMassShiftDataset));
			}
		}
	}
	
	public void testAnalyseMixedMassShifts() {
		for (File file : testFiles) {
			if (file.getName().contains(this.getClass().getSimpleName())) {
				LOGGER.infoConcat("Checking mixed MSD for file", file.getName());
				MSShiftDatabase msShiftDatabase = new MSShiftDatabase(file.getAbsolutePath(), SpectrumType.CENTROIDED);
				MassSpectrum mixedSpectrum = msShiftDatabase.getMixedSpectrum();
				MassShiftDataSet expectedMassShiftDataset = msShiftDatabase.getMixedMassShifts();
				ElementList availableElements = ElementList.fromFormula(msShiftDatabase.getFragmentFormula());
				MassShiftDataSet actualMassShiftDataset = mixedSpectrum.analyseMassShifts(availableElements);
				LOGGER.info("actualMassShiftDataset:\t" + actualMassShiftDataset);
				LOGGER.info("expectedMassShiftDataset:\t" + expectedMassShiftDataset);
				assertTrue(expectedMassShiftDataset.equalsUpToPermutationOfIsotopes(actualMassShiftDataset));
			}
		}
	}
}
