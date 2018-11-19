package de.kempalab.msdps;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;
import de.kempalab.msdps.ElementFormula;
import de.kempalab.msdps.Fragment;
import de.kempalab.msdps.FragmentsDatabase;
import de.kempalab.msdps.IncorporationMap;
import de.kempalab.msdps.IsotopeFormula;
import de.kempalab.msdps.IsotopeList;
import de.kempalab.msdps.IsotopeSet;
import de.kempalab.msdps.MSShiftDatabase;
import de.kempalab.msdps.MassSpectrum;
import de.kempalab.msdps.constants.Element;
import de.kempalab.msdps.constants.FragmentKey;
import de.kempalab.msdps.constants.IncorporationType;
import de.kempalab.msdps.constants.Isotope;
import de.kempalab.msdps.constants.PathConstants;
import de.kempalab.msdps.exception.FragmentNotFoundException;
import de.kempalab.msdps.exception.FrequencyTypeMismatchException;
import de.kempalab.msdps.log.MyLogger;
import de.kempalab.msdps.simulation.IsotopePatternSimulator;
import de.kempalab.msdps.util.MathUtils;

public class MSShiftDatabaseTest extends TestCase {
	
	private File[] testFiles = new File(PathConstants.TEST_RESOURCES.toAbsolutePath()).listFiles();
	private static final MyLogger LOGGER = MyLogger.getLogger(MSShiftDatabaseTest.class);
	
	public void testReadWriteCsv() throws IOException {
		String filePath = "";
		for (File file : testFiles) {
			if (file.getName().contains("MSShiftDatabaseTest")) {
				filePath = file.getAbsolutePath();
				LOGGER.infoValue("Test file", file.getName());
				MSShiftDatabase msDatabase = new MSShiftDatabase(filePath);
				LOGGER.infoValue("msDatabase\n", msDatabase);
				msDatabase.writeCsv(PathConstants.TMP_FOLDER.toAbsolutePath());
			}
		}
		File[] created = new File(PathConstants.TMP_FOLDER.toAbsolutePath()).listFiles();
		for (File file : created) {
			file.delete();
		}
	}
	
}
